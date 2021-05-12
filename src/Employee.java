public class Employee implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private int ID;
    private String password;
    private boolean manager;
    private String name;
    private String company;
    private boolean isForklift;
    private boolean isChooseBreak;
    private double wage;

    // Constructor for employee
    public Employee(int ID, String password, boolean manager, String name, String company, boolean isForklift, double wage) {
        this.ID = ID;
        this.password = password;
        this.manager = manager;
        this.name = name;
        this.company = company;
        this.isForklift = isForklift;
        this.isChooseBreak = false;
        this.wage = wage;
    }

    // Constructor for manager
    public Employee(int ID, String password, boolean manager, String name, String company, boolean isForklift) {
        this.ID = ID;
        this.password = password;
        this.manager = manager;
        this.name = name;
        this.company = company;
        this.isForklift = isForklift;
        this.isChooseBreak = false;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public boolean getIsForklift() {
        return this.isForklift;
    }

    public void setIsForklift(boolean isForklift) {
        this.isForklift = isForklift;
    }

    public boolean getIsChooseBreak() {
        return this.isChooseBreak;
    }

    public void setChooseBreak(boolean isChooseBreak) {
        this.isChooseBreak = isChooseBreak;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public  String getInfo() {
        String info = "";
        info += "Manager: " + this.manager + "\n";
        info += "Name: " + this.name + "\n";
        info += "ID: " + this.ID + "\n";
        info += "Company: " + this.company + "\n";
        info += "Forklift: " + this.isForklift + "\n";
        info += "Wage: " + this.wage;

        return info;
    }
}
