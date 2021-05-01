import java.io.*;
import java.util.HashMap;

public class EmployeeSampler {
    static String dbPath = System.getProperty("user.dir") + "/db/";

    public static void main(String[] args) {
        HashMap<Integer, Employee> employeeDB = new HashMap<>();

        // Employee(int ID, String password, boolean manager, String name, String company, boolean isForklift)
        // Company: tx, jj, gl
        employeeDB.put(123123, new Employee(123123, "123123", false, "aaa", "tx", false,158));
        employeeDB.put(234234, new Employee(234234, "234234", false, "bbb", "jj", false,158));
        employeeDB.put(345345, new Employee(345345, "345345", false, "ccc", "gl", true));
        employeeDB.put(456456, new Employee(456456, "456456", false, "ddd", "tx", false,158));
        employeeDB.put(567567, new Employee(567567, "567567", false, "eee", "jj", true));
        employeeDB.put(678678, new Employee(678678, "678678", false, "fff", "gl", false,158));
        employeeDB.put(789789, new Employee(789789, "789789", false, "ggg", "tx", false,158));

        try {
            new File(dbPath).mkdir();
            File f = new File(dbPath + "employeeDB.ser");
            f.createNewFile();

            FileOutputStream fileOut = new FileOutputStream(dbPath + "employeeDB.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(employeeDB);
            out.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Sample Employee Data Inserted");
    }
}
