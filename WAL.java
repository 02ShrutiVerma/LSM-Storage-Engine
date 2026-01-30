import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WAL {
    private final String fileName = "database_log.txt";

    public void write(String key, String value) {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(key + ":" + value);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing log: " + e.getMessage());
        }
    }

    public List<String> readAllLines() {
        List<String> lines = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading notebook: " + e.getMessage());
        }
        return lines;
    }
}
