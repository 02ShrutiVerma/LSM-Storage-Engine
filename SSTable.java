import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class SSTable {
    public static void flush(TreeMap<String, String> data, String fileName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                out.println(entry.getKey() + ":" + entry.getValue());
            }
            System.out.println(">>> [FLUSH SUCCESS] Created file: " + fileName);
        } catch (IOException e) {
            System.err.println("Flush failed: " + e.getMessage());
        }
    }
}