public class Main {
    public static void main(String[] args) {
        MemTable myDB = new MemTable();

        System.out.println("--- Starting RUN 1: Adding 6 items ---");

        myDB.put("Item1", "Value1");
        myDB.put("Item2", "Value2");
        myDB.put("Item3", "Value3");
        myDB.put("Item4", "Value4");
        myDB.put("Item5", "Value5");

        myDB.put("Item6", "Value6");

        System.out.println("Done! Check your folder for sstable_0.txt.");
    }
}