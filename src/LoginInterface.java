import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Scanner;

public class LoginInterface {
    static final String dbPath = System.getProperty("user.dir") + "/db/";
    private ThreadHandler thread;
    private int loginID = 0;
    private Scanner sc;
    private HashMap<Integer, Employee> employeeDB;
    private Parameter parameter;

    // Constructor for standalone mode
    public LoginInterface() {
        sc = new Scanner(System.in);
    }

    // Constructor for server mode
    public LoginInterface(ThreadHandler thread) {
        this.thread = thread;
    }

    // Runnable method to initiate the login process, standalone mode
    public static void standaloneDriver() throws IOException, ParseException{
        LoginInterface li = new LoginInterface();
        li.standalone();
    }

    // Runnable method to initiate the login process, server mode
    public static void serverDriver(ThreadHandler thread) throws IOException, ParseException {
        LoginInterface li = new LoginInterface(thread);
        li.serverMode(li.thread);
    }

    // Asks for login and starts PxPersonnel application, standalone mode
    public void standalone() throws IOException, ParseException {
        employeeDB = readEmployeeDB();
        parameter = readParameter();

        String role = login(employeeDB);
        PxPersonnel.driver(employeeDB, role, loginID);
    }

    // // Asks for login and starts PxPersonnel application, server mode
    public void serverMode(ThreadHandler thread) throws IOException, ParseException {
        employeeDB = thread.employeeDB;
        parameter = thread.parameter;

        String role = loginServer(employeeDB, thread);
        PxPersonnel.driverServer(role, loginID, thread);
    }

    // Prompts Login and returns login role, standalone mode
    private String login(HashMap<Integer, Employee> employeeDB) {
        int id;
        String password;
        do {
            while(true) {
                try {
                    System.out.print("User ID: ");
                    id = Integer.parseInt(sc.nextLine());
                    break;
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid ID input, Please enter correct ID in integer\n");
                }
            }
            System.out.print("Password: ");
            password = sc.nextLine();
        } while(!authentication(employeeDB, id,password));
        loginID = id;
        return loginRole(employeeDB, id);
    }

    // Prompts Login and returns login role, server mode
    private String loginServer(HashMap<Integer, Employee> employeeDB, ThreadHandler thread) throws IOException {
        int id;
        String password;
        do {
            while(true) {
                try {
                    thread.outputStream.print("User ID: ");
                    thread.endAnswer();
                    id = Integer.parseInt(thread.inputStream.readLine());
                    break;
                }
                catch (NumberFormatException e) {
                    thread.outputStream.println("Invalid ID input, Please enter correct ID in integer\n");
                    thread.end();
                }
            }
            thread.outputStream.print("Password: ");
            thread.endAnswer();
            password = thread.inputStream.readLine();
        } while(!authenticationServerMode(employeeDB, id, password, thread));
        loginID = id;
        return loginRole(employeeDB, id);

    }

    // Exams login authentication, standalone mode
    private boolean authentication(HashMap<Integer, Employee> employeeDB, int id, String password) {
        if(id == parameter.getAdminID()) {
            if(password.equals(parameter.getAdminPassword())) {
                return true;
            }
            else {
               System.out.println("Incorrect Password\n");
                return false;
            }
        }
        else if(!employeeDB.containsKey(id)) {
            System.out.println("Incorrect ID\n");
            return false;
        }
        else if(!employeeDB.get(id).getPassword().equals(password)) {
            System.out.println("Incorrect Password\n");
            return false;
        }
        else {
            return true;
        }
    }

    // Exams login authentication, server mode
    private boolean authenticationServerMode(HashMap<Integer, Employee> employeeDB, int id, String password, ThreadHandler thread) {
        if(id == parameter.getAdminID()) {
            if(password.equals(parameter.getAdminPassword())) {
                return true;
            }
            else {
                thread.outputStream.print("Incorrect Password\n\n");
                thread.end();
                return false;
            }
        }
        else if(!employeeDB.containsKey(id)) {
            thread.outputStream.print("Incorrect ID\n\n");
            thread.end();
            return false;
        }
        else if(!employeeDB.get(id).getPassword().equals(password)) {
            thread.outputStream.print("Incorrect Password\n\n");
            thread.end();
            return false;
        }
        else {
            return true;
        }
    }

    // Checks Login Role
    // Returns String "admin" "manager" "employee"
    private String loginRole(HashMap<Integer, Employee> employeeDB, int id) {
        if(id == parameter.getAdminID()) {
            return "admin";
        }
        else if(employeeDB.get(id).getManager()) {
            return "manager";
        }
        else {
            return "employee";
        }
    }

    // Reads and returns Employee Database
    private HashMap<Integer, Employee> readEmployeeDB() throws IOException{
        HashMap<Integer, Employee> db = null;
        try {
            FileInputStream fileIn = new FileInputStream(dbPath + "employeeDB.ser");
            ObjectInputStream inStream = new ObjectInputStream(fileIn);
            db = (HashMap<Integer, Employee>) inStream.readObject();
            inStream.close();
            fileIn.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Employee Database Not Found.");
            System.out.println("Initiating Database");
            new File(dbPath).mkdir();

            File f = new File(dbPath + "employeeDB.ser");
            f.createNewFile();

            db = new HashMap<Integer, Employee>();
            System.out.println("done");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return db;
    }

    // Reads and returns Parameter Database
    private Parameter readParameter() throws IOException {
        Parameter param = null;
        try {
            FileInputStream fileIn = new FileInputStream(dbPath + "parameter.ser");
            ObjectInputStream inStream = new ObjectInputStream(fileIn);
            param = (Parameter) inStream.readObject();
            inStream.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            System.out.println("Parameter Not Found.");
            System.out.println("Initiating Database");
            File f = new File(dbPath + "parameter.ser");
            f.createNewFile();
            param = new Parameter();
            System.out.println("done");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return param;
    }
}
