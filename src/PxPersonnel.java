import java.io.*;
import java.text.ParseException;
import java.time.YearMonth;
import java.util.*;

public class PxPersonnel {
    private final static String dbPath = System.getProperty("user.dir") + "/db/";
    private String role;
    private HashMap<Integer, Employee> employeeDB;
    private WorkCalender calender;
    private Parameter parameter;
    private Scanner sc = new Scanner(System.in);

    // Driver method, standalone mode
    public static void driver(HashMap<Integer, Employee> db, String role, int id) throws IOException, ParseException{
        PxPersonnel pxPersonnel = new PxPersonnel();
        if(role.equals("admin")) {
            pxPersonnel.admin(db);
        }
        else if(role.equals("manager")) {
            pxPersonnel.manager(db);
        }
        else {
            pxPersonnel.employee(id, db);
        }
    }

    // Driver method, server mode
    public static void driverServer(String role, int id, ThreadHandler thread) throws IOException, ParseException{
        PxPersonnel pxPersonnel = new PxPersonnel();
        if(role.equals("admin")) {
            pxPersonnel.adminServer(thread);
        }
        else if(role.equals("manager")) {
            pxPersonnel.managerServer(thread);
        }
        else {
            pxPersonnel.employeeServer(id, thread);
        }
    }

    // Runs adminMenu repeatedly, standalone mode
    private void admin(HashMap<Integer, Employee> db) throws IOException, ParseException {
        this.role = "admin";
        employeeDB = db;
        calender = readCalender();
        parameter = readParameter();

        System.out.println("\nCurrent Role: Admin");
        while (true) {
            menuAdmin();
        }
    }

    // Runs adminMenu repeatedly, server mode
    private void adminServer(ThreadHandler thread) throws IOException, ParseException {
        this.role = "admin";
        employeeDB = thread.employeeDB;
        calender = thread.calender;
        parameter = thread.parameter;

        thread.outputStream.print("\nCurrent Role: Admin\n");
        thread.end();
        while (true) {
            menuAdminServer(thread);
        }
    }

    // Runs managerMenu repeatedly, standalone mode
    private void manager(HashMap<Integer, Employee> db) throws IOException, ParseException {
        this.role = "manager";
        employeeDB = db;
        calender = readCalender();
        parameter = readParameter();

        System.out.println("\nCurrent Role: Manager");
        while (true) {
            //menuManager();
        }
    }

    // Runs managerMenu repeatedly, server mode
    private void managerServer(ThreadHandler thread) throws IOException, ParseException {
        this.role = "manager";
        employeeDB = thread.employeeDB;
        calender = thread.calender;
        parameter = thread.parameter;

        thread.outputStream.print("\nCurrent Role: Manager\n");
        thread.end();
        while (true) {
            //menuManagerServer(thread);
        }
    }

    // Runs employeeMenu repeatedly, standalone mode
    private void employee(int id, HashMap<Integer, Employee> db) throws ParseException, IOException{
        this.role = "employee";
        employeeDB = db;
        Employee employee = employeeDB.get(id);
        calender = readCalender();
        parameter = readParameter();

        System.out.println("\nCurrent Role: Employee: " + employee.getID());
        while (true) {
            menuEmployee(employee);
        }
    }

    // Runs employeeMenu repeatedly, server mode
    private void employeeServer(int id, ThreadHandler thread) throws ParseException, IOException{
        this.role = "employee";
        employeeDB = thread.employeeDB;
        Employee employee = employeeDB.get(id);
        calender = thread.calender;
        parameter = thread.parameter;

        System.out.println("\nCurrent Role: Employee: " + employee.getID());
        readCalender();
        while (true) {
            menuEmployeeServer(employee, thread);
        }
    }

    // Prompts and calls method according to users input for admin user, standalone mode
    private void menuAdmin() throws ParseException{
        System.out.println("\nPlease Select a function");
        System.out.println("A: Add New Employee");
        System.out.println("V: View Employee");
        System.out.println("E: Edit Employee");
        System.out.println("N: Add New Month to Calender");
        System.out.println("M: Set Current Month to Calender");
        System.out.println("S: Add Shift");
        System.out.println("B: Add Break");
        System.out.println("CB: Allow choose break for current month");
        System.out.println("EB: End choose break for current month");
        System.out.println("L: Break Lottery for current month");
        System.out.println("PB: Print Work Calender of this month");
        System.out.println("VB: View Break of a Employee");
        System.out.println("Q: Save and Quit");

        String s = sc.nextLine();
        switch (s.toLowerCase()) {
            case "a":
                addEmployee();
                break;
            case "v":
                viewEmployee();
                break;
            case "e":
                editEmployee();
                break;
            case "n":
                addMonth();
                break;
            case "m":
                setMonth();
                break;
            case "s":
                addShift();
                break;
            case "as":
                addShiftAuto();
                break;
            case "b":
                addBreak();
                break;
            case "cb":
                startChooseBreak();
                break;
            case "eb":
                endChooseBreak();
                break;
            case "l":
                breakLottery(parameter.getCurrentMonth());
                break;
            case "pb":
                printBreakSchedule();
                break;
            case "vb":
                viewBreak();
                break;
            case "q":
                quit();
                break;
            default:
                System.out.println("Invalid Input, Please Choose From The Selection Menu");
        }
    }

    // Prompts and calls method according to users input for admin user, server mode
    private void menuAdminServer(ThreadHandler thread) throws IOException, ParseException{
        thread.outputStream.print("\nPlease Select a function\n");
        thread.outputStream.print("A: Add New Employee\n");
        thread.outputStream.print("V: View Employee\n");
        thread.outputStream.print("E: Edit Employee\n");
        thread.outputStream.print("N: Add New Month to Calender\n");
        thread.outputStream.print("M: Set Current Month to Calender\n");
        thread.outputStream.print("S: Add Shift\n");
        thread.outputStream.print("B: Add Break\n");
        thread.outputStream.print("CB: Allow choose break for current month\n");
        thread.outputStream.print("EB: End choose break for current month\n");
        thread.outputStream.print("L: Break Lottery for current month\n");
        thread.outputStream.print("PB: Print Work Calender of this month\n");
        thread.outputStream.print("VB: View Break of a Employee\n");
        thread.outputStream.print("Q: Save and Quit\n");
        thread.outputStream.print("QS: Save and Terminate Server\n");
        thread.endAnswer();

        String s = thread.inputStream.readLine();
        switch (s.toLowerCase()) {
            case "a":
                addEmployee(thread);
                break;
            case "v":
                viewEmployee(thread);
                break;
            case "e":
                editEmployee(thread);
                break;
            case "n":
                addMonth(thread);
                break;
            case "m":
                setMonth(thread);
                break;
            case "s":
                addShift(thread);
                break;
            case "as":
                addShiftAuto(thread);
                break;
            case "b":
                addBreak(thread);
                break;
            case "cb":
                startChooseBreak(thread);
                break;
            case "eb":
                endChooseBreak(thread);
                break;
            case "l":
                breakLottery(parameter.getCurrentMonth(), thread);
                break;
            case "pb":
                printBreakSchedule(thread);
                break;
            case "vb":
                viewBreak(thread);
                break;
            case "q":
                quit(thread);
                break;
            case "qs":
                quitServer(thread);
                break;
            default:
                thread.outputStream.print("Invalid Input, Please Choose From The Selection Menu\n");
        }
    }

    // Prompts and calls method according to users input for employee user, standalone mode
    private void menuEmployee(Employee employee) throws ParseException {
        System.out.println("\nPlease Select a function");
        System.out.println("B: Add Break");
        System.out.println("VB: View Break of Current Month");
        System.out.println("Q: Save and Quit");

        String s = sc.nextLine();
        switch (s.toLowerCase()) {
            case "b":
                addBreakEmployee(employee);
                break;
            case "vb":
                System.out.println("View Break");
                viewBreakAuto(employee.getID());
                break;
            case "q":
                quit();
                break;
            default:
                System.out.println("Invalid Input, Please Choose From The Selection Menu");
        }
    }

    // Prompts and calls method according to users input for employee user, server mode
    private void menuEmployeeServer(Employee employee, ThreadHandler thread) throws ParseException, IOException {
        thread.outputStream.print("\nPlease Select a function\n");
        thread.outputStream.print("B: Add Break\n");
        thread.outputStream.print("VB: View Break of Current Month\n");
        thread.outputStream.print("Q: Save and Quit\n");
        thread.endAnswer();

        String s = thread.inputStream.readLine();
        switch (s.toLowerCase()) {
            case "b":
                addBreakEmployee(employee, thread);
                break;
            case "vb":
                thread.outputStream.print("View Break\n");
                thread.end();
                viewBreakAuto(employee.getID(), thread);
                break;
            case "q":
                quit();
                break;
            default:
                thread.outputStream.print("Invalid Input, Please Choose From The Selection Menu\n");
                thread.end();
        }
    }

    // Prompts and adds new employee, standalone mode
    private void addEmployee(){
        System.out.print("\nAdd New Employee");
        int id;
        String password;
        boolean manager;
        String name;
        String company;
        boolean isForklift;
        double wage;
        while (true) {
            String input;
            System.out.println("\n(Enter B to go back)");
            try {
                System.out.println("\n(Enter B to go back)");
                System.out.print("New Employee ID: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (employeeDB.containsKey(id)) {
                    System.out.println("ID already existed, please verify");
                    continue;
                }

                System.out.print("New Employee Password: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                password = input;

                System.out.print("New Employee is Manager (Y/N): ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                manager = (input.toLowerCase().equals("y"));

                System.out.print("New Employee Name: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                name = input;

                System.out.print("New Employee HR Company: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                company = input;

                System.out.print("New Employee Forklift (Y/N): ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                isForklift = (input.toLowerCase().equals("y"));

                if(manager) {
                    employeeDB.put(id, new Employee(id, password, manager, name, company, isForklift));
                    System.out.println("Manager added");
                }
                else {
                    System.out.print("New Employee wage ($/hour): ");
                    input = sc.nextLine();
                    if (input.toLowerCase().equals("b")) {
                        return;
                    }
                    wage = Double.parseDouble(input);
                    employeeDB.put(id, new Employee(id, password, manager, name, company, isForklift, wage));
                    System.out.println("Employee added");
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input format, please verify");
                continue;
            }
            break;
        }
    }

    // Prompts and add new employee, server mode
    private void addEmployee(ThreadHandler thread) throws IOException {
        thread.outputStream.print("\nAdd New Employee");
        thread.end();
        int id;
        String password;
        boolean manager;
        String name;
        String company;
        boolean isForklift;
        double wage;
        while (true) {
            String input;
            thread.outputStream.print("\n(Enter B to go back)\n");
            thread.end();
            try {
                thread.outputStream.print("New Employee ID: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (employeeDB.containsKey(id)) {
                    thread.outputStream.print("ID already existed, please verify\n");
                    thread.end();
                    continue;
                }

                thread.outputStream.print("New Employee Password: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                password = input;

                thread.outputStream.print("New Employee is Manager (Y/N): ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                manager = (input.toLowerCase().equals("y"));

                thread.outputStream.print("New Employee Name: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                name = input;

                thread.outputStream.print("New Employee HR Company: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                company = input;

                thread.outputStream.print("New Employee Forklift (Y/N): ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                isForklift = (input.toLowerCase().equals("y"));

                if(manager) {
                    employeeDB.put(id, new Employee(id, password, manager, name, company, isForklift));
                    thread.outputStream.print("Manager added\n");
                    thread.end();
                }
                else {
                    thread.outputStream.print("New Employee wage ($/hour): ");
                    thread.endAnswer();
                    input = thread.inputStream.readLine();
                    if (input.toLowerCase().equals("b")) {
                        return;
                    }
                    wage = Double.parseDouble(input);
                    employeeDB.put(id, new Employee(id, password, manager, name, company, isForklift, wage));
                    thread.outputStream.print("Employee added\n");
                    thread.end();
                }
            }
            catch (NumberFormatException e) {
                thread.outputStream.print("Invalid input format, please verify\n");
                thread.end();
                continue;
            }
            break;
        }
    }

    // Prompts and looks up info of certain employee, standalone mode
    private void viewEmployee() {
        System.out.print("\nView Employee");
        int id;
        while(true) {
            String input;
            System.out.println("\n(Enter B to go back)");
            try {
                System.out.print("Employee ID to Lookup: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (!employeeDB.containsKey(id)) {
                    System.out.println("Inserted ID does not exist, please verify");
                    continue;
                }
                break;
            } catch (NumberFormatException e){
                System.out.println(("Invalid input format, please verify"));
            }
        }
        System.out.println("\n" +employeeDB.get(id).getInfo());
    }

    // Prompts and looks up info of certain employee, server mode
    private void viewEmployee(ThreadHandler thread) throws IOException {
        thread.outputStream.print("\nView Employee");
        thread.end();
        int id;
        while(true) {
            String input;
            thread.outputStream.print("\n(Enter B to go back)\n");
            thread.end();
            try {
                thread.outputStream.print("Employee ID to Lookup: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (!employeeDB.containsKey(id)) {
                    thread.outputStream.print("Inserted ID does not exist, please verify\n");
                    thread.end();
                    continue;
                }
                break;
            } catch (NumberFormatException e){
                thread.outputStream.print("Invalid input format, please verify\n");
                thread.end();
            }
        }
        thread.outputStream.print("\n" + employeeDB.get(id).getInfo() + "\n");
        thread.end();
    }

    // Prompts and edits a field of certain employee, standalone mode
    private void editEmployee() {
        System.out.print("\nEdit Employee");
        int id;
        String field = "";
        while (true) {
            String input;
            System.out.println("\n(Enter B to go back)");
            try {
                System.out.print("Employee ID to Edit: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (!employeeDB.containsKey(id)) {
                    System.out.println("Inserted ID does not exist, please verify");
                    continue;
                }
                break;
            } catch (NumberFormatException e){
                System.out.println("Invalid input format, please verify");
                continue;
            }
        }

        while (true) {
            String input;
            try {
                System.out.println("\n(Enter B to go back)");
                System.out.println("Field to update:");
                System.out.println("[password, manager, name, company, isForklift]");
                input = sc.nextLine();
                if(input.toLowerCase().equals("b")) {
                    return;
                }
                field = input;
                Class<?> c = employeeDB.get(id).getClass();
                c.getDeclaredField(field); // Check entered field exist
                if(field.toLowerCase().equals("id")) {
                    System.out.println("ID is permanent and cannot be updated");
                    continue;
                }
                break;
            } catch (NoSuchFieldException e) {
                System.out.println("Employee field " + field + " does not exist, please verify");
                continue;
            }
        }

        switch (field) {
            case "password":
                System.out.print("New Password: ");
                employeeDB.get(id).setPassword(sc.nextLine());
                break;
            case "manager":
                System.out.print("\nIs Manager (Y/N): ");
                employeeDB.get(id).setManager(sc.nextLine().toLowerCase().equals("y"));
                break;
            case "name":
                System.out.print("\nNew Name: ");
                employeeDB.get(id).setName(sc.nextLine());
                break;
            case "company":
                System.out.print("\nNew Company: ");
                employeeDB.get(id).setCompany(sc.nextLine());
                break;
            case "isForklift":
                System.out.print("\nIs Forklift (Y/N): ");
                employeeDB.get(id).setIsForklift(sc.nextLine().toLowerCase().equals("y"));
                break;
        }
        System.out.println("Employee edited");
    }

    // Prompts and edits a field of certain employee, server mode
    private void editEmployee(ThreadHandler thread) throws IOException {
        thread.outputStream.print("\nEdit Employee");
        thread.end();
        int id;
        String field = "";
        while (true) {
            String input;
            thread.outputStream.print("\n(Enter B to go back)\n");
            thread.end();
            try {
                thread.outputStream.print("Employee ID to Edit: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (!employeeDB.containsKey(id)) {
                    thread.outputStream.print("Inserted ID does not exist, please verify\n");
                    thread.end();
                    continue;
                }
                break;
            } catch (NumberFormatException e){
                thread.outputStream.print("Invalid input format, please verify\n");
                thread.end();
                continue;
            }
        }

        while (true) {
            String input;
            try {
                thread.outputStream.print("\n(Enter B to go back)\n");
                thread.outputStream.print("Field to update:\n");
                thread.outputStream.print("[password, manager, name, company, isForklift]\n");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if(input.toLowerCase().equals("b")) {
                    return;
                }
                field = input;
                Class<?> c = employeeDB.get(id).getClass();
                c.getDeclaredField(field); // Check entered field exist
                if(field.toLowerCase().equals("id")) {
                    thread.outputStream.print("ID is permanent and cannot be updated\n");
                    thread.end();
                    return;
                }
                break;
            } catch (NoSuchFieldException e) {
                thread.outputStream.print("Employee field " + field + " does not exist, please verify\n");
                thread.end();
                continue;
            }
        }

        switch (field) {
            case "password":
                thread.outputStream.print("\nNew Password: ");
                thread.endAnswer();
                employeeDB.get(id).setPassword(thread.inputStream.readLine());
                break;
            case "manager":
                thread.outputStream.print("\nIs Manager (Y/N): ");
                thread.endAnswer();
                employeeDB.get(id).setManager(thread.inputStream.readLine().toLowerCase().equals("y"));
                break;
            case "name":
                thread.outputStream.print("\nNew Name: ");
                thread.endAnswer();
                employeeDB.get(id).setName(thread.inputStream.readLine());
                break;
            case "company":
                thread.outputStream.print("\nNew Company: ");
                thread.endAnswer();
                employeeDB.get(id).setCompany(thread.inputStream.readLine());
                break;
            case "isForklift":
                thread.outputStream.print("\nIs Forklift (Y/N): ");
                thread.endAnswer();
                employeeDB.get(id).setIsForklift(thread.inputStream.readLine().toLowerCase().equals("y"));
                break;
        }
        thread.outputStream.print("Employee edited\n");
        thread.end();
    }

    // Prompts and adds two consecutive months to calender, standalone mode
    private void addMonth() {
        System.out.print("\nAdd New Month");
        int month;
        while(true) {
            String input;
            System.out.println("\n(Enter B to go back)");
            System.out.print("Enter Month in YYYYMM Format: ");
            input = sc.nextLine();
            if (input.toLowerCase().equals("b")) {
                return;
            }
            try {
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                }
                else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                System.out.println("Invalid input, please verify");
                continue;
            }
            break;
        }
        if(calender.containsKey(month)) {
            System.out.println(month + " exists");
        }
        else {
            calender.addMonth(month);
            System.out.println(month + " added");
            setMonth(month);
            System.out.println("current month set to " + month);
        }
    }

    // Prompts and adds two consecutive months to calender, server mode
    private void addMonth(ThreadHandler thread) throws IOException {
        thread.outputStream.print("\nAdd New Month");
        thread.end();
        int month;
        while(true) {
            String input;
            thread.outputStream.print("\n(Enter B to go back)\n");
            thread.outputStream.print("Enter Month in YYYYMM Format: ");
            thread.endAnswer();
            input = thread.inputStream.readLine();
            if (input.toLowerCase().equals("b")) {
                return;
            }
            try {
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                }
                else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                thread.outputStream.print("Invalid input, please verify\n");
                thread.end();
                continue;
            }
            break;
        }
        if(calender.containsKey(month)) {
            thread.outputStream.print(month + " exists\n");
            thread.end();
        }
        else {
            calender.addMonth(month);
            thread.outputStream.print(month + " added\n");
            setMonth(month);
            thread.outputStream.print("current month set to " + month + "\n");
            thread.end();
        }
    }

    // Sets calender current month for operation
    private void setMonth(int month) {
        parameter.setCurrentMonth(month);
    }

    // Prompts and sets current month interactively, standalone mode
    private void setMonth() {
        System.out.print("\nSet Current Month");
        int month;
        while(true) {
            String input;
            System.out.println("\n(Enter B to go back)");
            System.out.print("Enter Month in YYYYMM Format: ");
            input = sc.nextLine();
            if (input.toLowerCase().equals("b")) {
                return;
            }
            try {
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                } else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                System.out.println("Invalid input, please verify");
                continue;
            }
            break;
        }
        parameter.setCurrentMonth(month);
        System.out.println("Current month set to " + month);
    }

    // Prompts and sets current month interactively, server mode
    private void setMonth(ThreadHandler thread) throws IOException{
        thread.outputStream.print("\nSet Current Month");
        thread.end();
        int month;
        while(true) {
            String input;
            thread.outputStream.print("\n(Enter B to go back)");
            thread.outputStream.print("Enter Month in YYYYMM Format: ");
            thread.endAnswer();
            input = thread.inputStream.readLine();
            if (input.toLowerCase().equals("b")) {
                return;
            }
            try {
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                } else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                thread.outputStream.print("Invalid input, please verify\n");
                continue;
            }
            break;
        }
        parameter.setCurrentMonth(month);
        thread.outputStream.print("Current month set to " + month + "\n");
        thread.end();
    }

    // Prompts and allows manager to add a shift to an employee on certain month, standalone mode
    private void addShift() {
        System.out.print("\nAdd Shift");
        int id;
        int month;
        int day;
        while(true) {
            String input;
            System.out.println("\n(Enter B to go back)");
            try {
                System.out.print("Employee ID to Add Shift: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if(!employeeDB.containsKey(id)) {
                    System.out.println("ID does not exist, please verify");
                    continue;
                }
                System.out.print("Month to Add Shift: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                }
                else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
                else if (!calender.containsKey(month)) {
                    System.out.println("Month does not exist in calender, please verify or add first");
                    continue;
                }
                System.out.print("Day to Add Shift: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                day = Integer.parseInt(input);
                if (day < 1 || day > YearMonth.of(month/100, month%100).lengthOfMonth()) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                System.out.println("Invalid input, please verify");
                continue;
            }
            break;
        }

        boolean isForklift = employeeDB.get(id).getIsForklift();
        if(isForklift) {
            calender.addShiftForklift(month, day, id);
        }
        else {
            calender.addShiftNormal(month, day,id);
        }
        System.out.println("Shift added");
    }

    // Prompts and allows manager to add a shift to an employee on certain month, server mode
    private void addShift(ThreadHandler thread) throws IOException {
        thread.outputStream.print("\nAdd Shift");
        thread.end();
        int id;
        int month;
        int day;
        while(true) {
            String input;
            thread.outputStream.print("\n(Enter B to go back)\n");
            thread.end();
            try {
                thread.outputStream.print("Employee ID to Add Shift: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if(!employeeDB.containsKey(id)) {
                    thread.outputStream.print("ID does not exist, please verify\n");
                    thread.end();
                    continue;
                }
                thread.outputStream.print("Month to Add Shift: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                }
                else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
                else if (!calender.containsKey(month)) {
                    thread.outputStream.print("Month does not exist in calender, please verify or add first\n");
                    thread.end();
                    continue;
                }
                thread.outputStream.print("Day to Add Shift: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                day = Integer.parseInt(input);
                if (day < 1 || day > YearMonth.of(month/100, month%100).lengthOfMonth()) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                thread.outputStream.print("Invalid input, please verify\n");
                thread.end();
                continue;
            }
            break;
        }

        boolean isForklift = employeeDB.get(id).getIsForklift();
        if(isForklift) {
            calender.addShiftForklift(month, day, id);
        }
        else {
            calender.addShiftNormal(month, day,id);
        }
        thread.outputStream.print("Shift added\n");
        thread.end();
    }

    // Prompts and add all shifts to employee according to chosen break, standalone mode
    private void addShiftAuto() throws ParseException {
        System.out.println("Adding shift automatically for " + parameter.getCurrentMonth());
        System.out.print("Confirm Operation? (Y/N): ");
        if(sc.nextLine().toLowerCase().equals("y")) {
            calender.addShiftAll(parameter.getCurrentMonth(), employeeDB);
            System.out.println("Shifts Added");
        }
        else {
            System.out.println("Canceled");
        }
    }

    // Prompts and add all shifts to employee according to chosen break, server mode
    private void addShiftAuto(ThreadHandler thread) throws ParseException, IOException {
        thread.outputStream.print("Adding shift automatically for " + parameter.getCurrentMonth() + "\n");
        thread.outputStream.print("Confirm Operation? (Y/N): ");
        thread.endAnswer();
        if(thread.inputStream.readLine().toLowerCase().equals("y")) {
            calender.addShiftAll(parameter.getCurrentMonth(), employeeDB);
            thread.outputStream.print("Shifts Added\n");
            thread.end();
        }
        else {
            thread.outputStream.print("Canceled\n");
            thread.end();
        }
    }

    // Prompts and allows manager to add a break to an employee on certain month, standalone mode
    private void addBreak() {
        System.out.print("\nAdd Break");
        int id;
        int month;
        int day;
        while(true) {
            String input;
            System.out.println("\n(Enter B to go back)");
            try {
                System.out.print("Employee ID to Add Break: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if(!employeeDB.containsKey(id)) {
                    System.out.println("ID does not exist, please verify");
                    continue;
                }
                System.out.print("Month to Add Break: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                }
                else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
                else if (!calender.containsKey(month)) {
                    System.out.println("Month does not exist in calender, please verify or add first");
                    continue;
                }
                System.out.print("Day to Add Break: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                day = Integer.parseInt(input);
                if (day < 1 || day > YearMonth.of(month/100, month%100).lengthOfMonth()) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                System.out.println("Invalid input, please verify");
                continue;
            }
            break;
        }

        boolean isForklift = employeeDB.get(id).getIsForklift();
        if(isForklift) {
            calender.addBreakForklift(month, day, id); //YYYYMM
        } else {
            calender.addBreakNormal(month, day,id);
        }
        System.out.println("Break added");
    }

    // Prompts and allows manager to add a break to an employee on certain month, server mode
    private void addBreak(ThreadHandler thread) throws IOException {
        thread.outputStream.print("\nAdd Break");
        thread.end();
        int id;
        int month;
        int day;
        while(true) {
            String input;
            thread.outputStream.print("\n(Enter B to go back)\n");
            thread.end();
            try {
                thread.outputStream.print("Employee ID to Add Break: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if(!employeeDB.containsKey(id)) {
                    thread.outputStream.print("ID does not exist, please verify\n");
                    thread.end();
                    continue;
                }
                thread.outputStream.print("Month to Add Break: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                }
                else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
                else if (!calender.containsKey(month)) {
                    thread.outputStream.print("Month does not exist in calender, please verify or add first\n");
                    thread.end();
                    continue;
                }
                thread.outputStream.print("Day to Add Break: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                day = Integer.parseInt(input);
                if (day < 1 || day > YearMonth.of(month/100, month%100).lengthOfMonth()) {
                    throw new DateFormatException();
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                thread.outputStream.print("Invalid input, please verify\n");
                continue;
            }
            break;
        }

        boolean isForklift = employeeDB.get(id).getIsForklift();
        if(isForklift) {
            calender.addBreakForklift(month, day, id); //YYYYMM
        } else {
            calender.addBreakNormal(month, day,id);
        }
        thread.outputStream.print("Break added\n");
        thread.end();
    }

    // Checks and allows employee to select break for current month, standalone mode
    private void addBreakEmployee(Employee employee) throws ParseException {
        if(employee.getIsChooseBreak()) {
            System.out.println("Already added break for " + parameter.getCurrentMonth());
        }
        else if(!parameter.isSelectBreakPeriod()) {
            System.out.println("It's not the period of selecting breaks");
        }
        else {
            calender.addBreakEmployee(employee.getID(), parameter.getCurrentMonth(), employee.getIsForklift(), sc);
            employee.setChooseBreak(true);
        }
    }

    // Checks and allows employee to select break for current month, server mode
    private void addBreakEmployee(Employee employee, ThreadHandler thread) throws ParseException, IOException{
        if(employee.getIsChooseBreak()) {
            thread.outputStream.print("Already added break for " + parameter.getCurrentMonth() + "\n");
            thread.end();
        }
        else if(!parameter.isSelectBreakPeriod()) {
            thread.outputStream.print("It's not the period of selecting breaks\n");
            thread.end();
        }
        else {
            calender.addBreakEmployee(employee.getID(), parameter.getCurrentMonth(), employee.getIsForklift(), thread);
            employee.setChooseBreak(true);
        }
    }

    // Randomly selects and reassigns exceeding break for the current month, standalone mode
    // Ends period of choosing breaks
    private void breakLottery(int currentMonth) throws ParseException{
        System.out.println("\nBreak Lottery\n");
        // Verifies all employees have chosen break
        boolean allChoose = true;
        List<Integer> notChooseID = new ArrayList<>();
        for(int id: employeeDB.keySet()) {
            if(employeeDB.get(id).getManager()) {
                continue;
            }
            else if(!employeeDB.get(id).getIsChooseBreak()) {
                notChooseID.add(id);
                allChoose = false;
            }
        }

        if(!allChoose) {
            System.out.println("Not all employees have chosen break, please choose.");
            System.out.println(notChooseID.toString());
        }
        else {
            calender.breakLottery(currentMonth);
            parameter.setSelectBreakPeriod(false);
        }
    }

    // Randomly selects and reassigns exceeding break for the current month, server mode
    // Ends period of choosing breaks
    private void breakLottery(int currentMonth, ThreadHandler thread) throws ParseException{
        thread.outputStream.print("\nBreak Lottery\n");
        thread.end();
        // Verifies all employees have chosen break
        boolean allChoose = true;
        List<Integer> notChooseID = new ArrayList<>();
        for(int id: employeeDB.keySet()) {
            if(employeeDB.get(id).getManager()) {
                continue;
            }
            else if(!employeeDB.get(id).getIsChooseBreak()) {
                notChooseID.add(id);
                allChoose = false;
            }
        }

        if(!allChoose) {
            thread.outputStream.print("Not all employees have chosen break, please choose.\n");
            thread.outputStream.print(notChooseID.toString() + "\n");
            thread.end();
        }
        else {
            calender.breakLottery(currentMonth, thread);
            parameter.setSelectBreakPeriod(false);
        }
    }

    // Prompts and starts choose break process for current month, permits employee to choose break, standalone mode
    private void startChooseBreak () {
        System.out.println("\nStart Choose Break");
        System.out.println("Allow employee to choose break for " + parameter.getCurrentMonth());
        System.out.print("Confirm operation? (Y/N): ");
        if(sc.nextLine().toLowerCase().equals("y")) {
            for (int id: employeeDB.keySet()) {
                employeeDB.get(id).setChooseBreak(false);
            }
            parameter.setSelectBreakPeriod(true);
        }
        else {
            System.out.println("Canceled");
        }
    }

    // Prompts and starts choose break process for current month, permits employee to choose break, server mode
    private void startChooseBreak (ThreadHandler thread) throws IOException {
        thread.outputStream.print("\nStart Choose Break\n");
        thread.outputStream.print("Allow employee to choose break for " + parameter.getCurrentMonth() + "\n");
        thread.outputStream.print("Confirm operation? (Y/N): ");
        thread.endAnswer();
        if(thread.inputStream.readLine().toLowerCase().equals("y")) {
            for (int id: employeeDB.keySet()) {
                employeeDB.get(id).setChooseBreak(false);
            }
            parameter.setSelectBreakPeriod(true);
        }
        else {
            thread.outputStream.print("Canceled\n");
            thread.end();
        }
    }

    // Prompts and ends choose break period for current month, prevents employee to choose break, standalone mode
    private void endChooseBreak() {
        System.out.println("\nEnd Choose Break");
        System.out.println("Disallow employee to choose break for " + parameter.getCurrentMonth());
        System.out.print("Confirm operation? (Y/N): ");
        if(sc.nextLine().toLowerCase().equals("y")) {
            parameter.setSelectBreakPeriod(false);
            System.out.println("Choose break period ended");
        }
        else {
            System.out.println("Canceled");
        }
    }

    // Prompts and ends choose break period for current month, prevents employee to choose break, server mode
    private void endChooseBreak(ThreadHandler thread) throws IOException{
        thread.outputStream.print("\nEnd Choose Break\n");
        thread.outputStream.print("Disallow employee to choose break for " + parameter.getCurrentMonth() + "\n");
        thread.outputStream.print("Confirm operation? (Y/N): ");
        thread.endAnswer();
        if(thread.inputStream.readLine().toLowerCase().equals("y")) {
            parameter.setSelectBreakPeriod(false);
            thread.outputStream.print("Choose break period ended");
            thread.end();
        }
        else {
            thread.outputStream.print("Canceled\n");
            thread.end();
        }
    }

    // Prompts and prints break schedule of certain month, standalone mode
    private void printBreakSchedule() throws ParseException {
        System.out.print("\nPrint Break Schedule");
        while(true) {
            String input;
            int month;
            System.out.println("\n(Enter B to go back)");
            System.out.print("Enter Month to Print in YYYYMM Format: ");
            input = sc.nextLine();
            if (input.toLowerCase().equals("b")) {
                return;
            }
            try {
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                } else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
                if(!calender.containsKey(month)) {
                    System.out.println("Month does not exist in database, please verify");
                    continue;
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                System.out.println("Invalid input, please verify");
                continue;
            }
            calender.printBreakCalendar(month);
            break;
        }
    }

    // Prompts and prints break schedule of certain month, server mode
    private void printBreakSchedule(ThreadHandler thread) throws ParseException, IOException {
        thread.outputStream.print("\nPrint Break Schedule");
        thread.end();
        while(true) {
            String input;
            int month;
            thread.outputStream.print("\n(Enter B to go back)\n");
            thread.outputStream.print("Enter Month to Print in YYYYMM Format: ");
            thread.endAnswer();
            input = thread.inputStream.readLine();
            if (input.toLowerCase().equals("b")) {
                return;
            }
            try {
                month = Integer.parseInt(input);
                if ((month / 100 < 1) || (month / 100 > 9999)) {
                    throw new DateFormatException();
                } else if ((month % 100 < 1 || month % 100 > 12)) {
                    throw new DateFormatException();
                }
                if(!calender.containsKey(month)) {
                    thread.outputStream.print("Month does not exist in database, please verify\n");
                    thread.end();
                    continue;
                }
            }
            catch (NumberFormatException | DateFormatException e) {
                thread.outputStream.print("Invalid input, please verify\n");
                thread.end();
                continue;
            }
            calender.printBreakCalendar(month, thread);
            break;
        }
    }

    // Prompts and prints scheduled breaks of current month for a employee, standalone mode
    private void viewBreak() throws ParseException{
        System.out.print("\nView Break");
        int id;
        while(true) {
            String input;
            try {
                System.out.println("\n(Enter B to go back)");
                System.out.print("Employee ID to Lookup breaks: ");
                input = sc.nextLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (!employeeDB.containsKey(id)) {
                    System.out.println("Inserted ID does not exist, please verify");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please verify");
            }
        }
        viewBreakAuto(id);
    }

    // Prompts and prints scheduled breaks of current month for a employee, server mode
    private void viewBreak(ThreadHandler thread) throws ParseException, IOException{
        thread.outputStream.print("\nView Break");
        thread.end();
        int id;
        while(true) {
            String input;
            try {
                thread.outputStream.print("\n(Enter B to go back)\n");
                thread.outputStream.print("Employee ID to Lookup breaks: ");
                thread.endAnswer();
                input = thread.inputStream.readLine();
                if (input.toLowerCase().equals("b")) {
                    return;
                }
                id = Integer.parseInt(input);
                if (!employeeDB.containsKey(id)) {
                    thread.outputStream.print("Inserted ID does not exist, please verify\n");
                    thread.end();
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                thread.outputStream.print("Invalid input, please verify\n");
                thread.end();
            }
        }
        viewBreakAuto(id, thread);
    }

    // Prints scheduled breaks of current month for a employee, standalone mode
    private void viewBreakAuto(int id) throws ParseException{
        if(!employeeDB.get(id).getIsChooseBreak()) {
            System.out.println(id + " not choose break yet");
        }
        else {
            List<Integer> breaks = calender.viewBreak(id, employeeDB.get(id).getIsForklift(), parameter.getCurrentMonth());
            System.out.println("Breaks: " + breaks.toString());
        }
    }

    // Prints scheduled breaks of current month for a employee, server mode
    private void viewBreakAuto(int id, ThreadHandler thread) throws ParseException{
        if(!employeeDB.get(id).getIsChooseBreak()) {
            thread.outputStream.print(id + " not choose break yet\n");
        }
        else {
            List<Integer> breaks = calender.viewBreak(id, employeeDB.get(id).getIsForklift(), parameter.getCurrentMonth());
            thread.outputStream.print("Breaks: " + breaks.toString() + "\n");
        }
    }

    // Prompts and shows (predicted) salary of certain month
    // Wait to finish
    private void salaryCalculator(int id) {
        System.out.print("\nCheck salary");
    }

    // Saves changes and quits, standalone mode
    private void quit() {
        // Save Employee Database
        try {
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

        // Save Calender Database
        try {
            FileOutputStream fileOut = new FileOutputStream(dbPath+"calender.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(calender);
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Save parameter database
        try {
            FileOutputStream fileOut = new FileOutputStream(dbPath+"parameter.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(parameter);
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nSaved");
        System.exit(0);
    }

    // Saves changes and quits, server mode
    private void quit(ThreadHandler thread) {
        // Save Employee Database
        try {
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

        // Save Calender Database
        try {
            FileOutputStream fileOut = new FileOutputStream(dbPath+"calender.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(calender);
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Save parameter database
        try {
            FileOutputStream fileOut = new FileOutputStream(dbPath+"parameter.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(parameter);
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        thread.outputStream.print("\nSaved\n");
        thread.terminate();
    }

    // Terminates server, used by admin only
    private void quitServer(ThreadHandler thread) {
        // Save Employee Database
        try {
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

        // Save Calender Database
        try {
            FileOutputStream fileOut = new FileOutputStream(dbPath+"calender.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(calender);
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Save parameter database
        try {
            FileOutputStream fileOut = new FileOutputStream(dbPath+"parameter.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(parameter);
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        thread.outputStream.print("\nSaved and terminated server\n");
        thread.end();
        try {
            thread.terminate();
            thread.quitServer();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads and returns Calender database
    private WorkCalender readCalender() throws IOException {
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
