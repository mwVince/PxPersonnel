import java.io.Serializable;

// Used as configuration for the application
public class Parameter implements Serializable {
    private int adminID;
    private String adminPassword;
    private int currentMonth;
    private boolean selectBreakPeriod;

    // Constructor with default values
    public Parameter() {
        this.adminID = 999999;
        this.adminPassword = "999999";
        this.currentMonth = 202104;
        this.selectBreakPeriod = false;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public boolean isSelectBreakPeriod() {
        return selectBreakPeriod;
    }

    public void setSelectBreakPeriod(boolean selectBreakPeriod) {
        this.selectBreakPeriod = selectBreakPeriod;
    }
}
