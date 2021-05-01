import java.net.*;
import java.io.*;
import java.text.ParseException;
import java.util.HashMap;


// TCP Server to run PxPersonnel in client-server mode
public class PxPersonnelServer {
    private final static int port = 7733;
    private static final String dbPath = System.getProperty("user.dir") + "/db/";
    private HashMap<Integer, Employee> employeeDB;
    private static WorkCalender calender;
    private static Parameter parameter;

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to PxPersonnel Server");
        PxPersonnelServer pxServer = new PxPersonnelServer();
        pxServer.start();
    }

    // Starts process by server instance
    private void start() throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            employeeDB = readEmployeeDB();
            calender = readCalender();
            parameter = readParameter();

            while(true) {
                Socket socket;
                socket = serverSocket.accept();
                System.out.println("New Connection! " + socket);
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);

                ThreadHandler thread = new ThreadHandler(socket, inputStream, outputStream, employeeDB, calender, parameter);
                thread.start();
            }
        }
        catch(BindException e) {
            System.out.println("Server already running, or other application using port: 7733");
        }
    }

    // Reads and returns Employee database
    private static HashMap<Integer, Employee> readEmployeeDB() throws IOException {
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

    // Reads and returns calender database
    private static WorkCalender readCalender() throws IOException {
        WorkCalender wc = null;
        try {
            FileInputStream fileIn = new FileInputStream(dbPath + "calender.ser");
            ObjectInputStream inStream = new ObjectInputStream(fileIn);
            wc = (WorkCalender) inStream.readObject();
            inStream.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            System.out.println("Calender Database Not Found.");
            System.out.println("Initiating Database");
            File f = new File(dbPath + "calender.ser");
            f.createNewFile();
            wc = new WorkCalender();
            System.out.println("done");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return wc;
    }

    // Reads and returns Parameter database
    private static Parameter readParameter() throws IOException {
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

class ThreadHandler extends Thread {
    final Socket socket;
    final BufferedReader inputStream;
    final PrintWriter outputStream;
    final HashMap<Integer, Employee> employeeDB;
    final WorkCalender calender;
    final Parameter parameter;

    public ThreadHandler(Socket socket, BufferedReader inputStream, PrintWriter outputStream,
                         HashMap<Integer, Employee> employeeDB, WorkCalender calender, Parameter parameter) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.employeeDB = employeeDB;
        this.calender = calender;
        this.parameter = parameter;
    }

    @Override
    public void run() {
        try {
            LoginInterface.serverDriver(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            System.out.println("Connection end: " + socket);
        }

        try {
            socket.close();
            inputStream.close();
            outputStream.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void end() {
        this.outputStream.print((char) 4);
        this.outputStream.flush();
    }

    public void endAnswer() {
        this.outputStream.print((char) 5);
        this.outputStream.flush();
    }

    public void terminate() {
        this.outputStream.print((char) 0);
        this.outputStream.flush();
    }

    public void quitServer() throws IOException {
        System.out.println("Server ended by admin");
        System.exit(0);
    }
}
