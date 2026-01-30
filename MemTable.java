import java.io.File;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MemTable {
    private TreeMap<String, String> dataMap = new TreeMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final WAL wal = new WAL();
    private final int MAX_ITEMS = 5;
    private int sstableCount = 0;
    public static final String TOMBSTONE = "__DELETED_TOMBSTONE__";

    public MemTable() {
        this.sstableCount = countExistingSSTables();
        System.out.println(">>> Database Wake-up: Starting at SSTable index " + this.sstableCount);
        recover();
    }

    public void put(String key, String value) {
        lock.writeLock().lock();
        try {
            // TRACE: Is the WAL failing on the Tombstone string?
            wal.write(key, value);
            dataMap.put(key, value);

            if (dataMap.size() >= MAX_ITEMS) {
                flushToSSTable();
            }
        } catch (Exception e) {
            // This is the "Safety Net"
            System.err.println("FATAL ENGINE ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow so the Server knows to send an ERROR message
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(String key) {
        System.out.println(">>> [TOMBSTONE PLACED] Marking " + key + " as deleted.");
        put(key, TOMBSTONE);
    }

    public String get(String key) {
        lock.readLock().lock();
        try {
            String val = dataMap.get(key);
            if (val != null) {
                return val.equals(TOMBSTONE) ? null : val;
            }

            String diskVal = searchDisk(key);
            if (diskVal != null) {
                return diskVal.equals(TOMBSTONE) ? null : diskVal;
            }

            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void flushToSSTable() {
        String fileName = "sstable_" + sstableCount + ".txt";
        SSTable.flush(dataMap, fileName);
        dataMap.clear();
        sstableCount++;
        System.out.println(">>> [FLUSH] RAM cleared. Data moved to " + fileName);
    }

    private String searchDisk(String key) {
        for (int i = sstableCount - 1; i >= 0; i--) {
            String fileName = "sstable_" + i + ".txt";
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":", 2);
                    if (parts[0].equals(key)) return parts[1];
                }
            } catch (java.io.IOException ignored) {}
        }
        return null;
    }

    private void recover() {
        List<String> lines = wal.readAllLines();
        for (String line : lines) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) dataMap.put(parts[0], parts[1]);
        }
        System.out.println(">>> Recovery: Replayed " + lines.size() + " operations from log.");
    }

    private int countExistingSSTables() {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.startsWith("sstable_") && name.endsWith(".txt"));
        if (files == null || files.length == 0) return 0;

        int max = -1;
        for (File f : files) {
            try {
                String name = f.getName();
                int index = Integer.parseInt(name.substring(name.indexOf('_') + 1, name.lastIndexOf('.')));
                if (index > max) max = index;
            } catch (Exception ignored) {}
        }
        return max + 1;
    }
}
