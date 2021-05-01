import java.io.*;
import java.util.HashMap;

// Run this to reset DB
public class DBRenewer {
    static String dbPath = System.getProperty("user.dir") + "/db/";

    public static void main(String[] args) {

        // Renew Employee DB
        try {
            new File(dbPath).mkdir();
            File f = new File(dbPath + "employeeDB.ser");
            f.createNewFile();

            FileOutputStream fileOut = new FileOutputStream(dbPath+"employeeDB.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(new HashMap<Integer, Employee>());
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Renew Calender DB
        try {
            File f = new File(dbPath + "calender.ser");
            f.createNewFile();

            FileOutputStream fileOut = new FileOutputStream(dbPath+"calender.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(new WorkCalender());
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        // Renew Parameter DB
        try {
            File f = new File(dbPath + "parameter.ser");
            f.createNewFile();

            FileOutputStream fileOut = new FileOutputStream(dbPath+"parameter.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(new Parameter());
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }




        System.out.println("Clean :) ");
    }
}
