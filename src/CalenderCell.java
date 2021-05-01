// Might used to integrate hourly record for over time to be stored in WorkCalender

import java.io.Serializable;
import java.util.HashMap;

public class CalenderCell implements Serializable {
    private Integer id; // List of ID for particular record
    private HashMap<Integer, Double> overtime; // List to record overtime hours for each employee

    // Constructor of CalenderCell for ID
    public CalenderCell(int id) {
        this.id = id;
    }

    // Constructor of CalenderCell for overtime map
    public CalenderCell(int id, double overtimeHour) {
        this.overtime = new HashMap<>();
        this.overtime.put(id, overtimeHour);
    }


    // Returns ID list
    public Integer getID() {
        return this.id;
    }

    // Returns overtime map
    public HashMap<Integer, Double> getOvertime() {
        return this.overtime;
    }

    // Overridden toString method
    public String toString() {
        if(id != null) {
            return Integer.toString(id);
        }
        else {
            String s = "";
            // Will only contain one key/value
            for(int id: overtime.keySet()) {
                s += id + ": " + overtime.get(id);
            }
            return s;
        }
    }

    // Overridden equals method
    public boolean equals(Object o) {
        CalenderCell obj = (CalenderCell) o;
        return obj.getID() == this.id && obj.getOvertime() == this.overtime;
    }
}
