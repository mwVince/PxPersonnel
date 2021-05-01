import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;

public class WorkCalender extends HashMap<Integer, List<Integer>[][]> implements java.io.Serializable{
    // Key: YYYYMM
    // Value: [days]{forklift_shift, normal_shift, forklift_break, normal_break}
    public WorkCalender(){
        this.addMonth(202104); // default month to initiate
    }

    // Adds and initiates a new month to calender
    public void addMonth(int month) {
        YearMonth ym = YearMonth.of(month/100, month%100);
        if(!this.containsKey(month)) {
            this.put(month, new List[ym.lengthOfMonth()+1][5]);
            // initialize
            for(int i=0; i< ym.lengthOfMonth()+1; i++){
                for (int j=0; j<5;j++) {
                    this.get(month)[i][j] = new ArrayList<Integer>();
                }
            }
        }

        // put in next month in case
        YearMonth ymNext = ym.plusMonths(1);
        int monthNext = (ymNext.getYear() * 100) + ymNext.getMonthValue();
        if(!this.containsKey(ymNext.getYear()*100 + ymNext.getMonthValue())) {
            this.put(monthNext, new List[ymNext.lengthOfMonth()+1][5]);
            // initialize
            for(int i=0; i< ymNext.lengthOfMonth()+1; i++){
                for (int j=0; j<5;j++) {
                    this.get(monthNext)[i][j] = new ArrayList<Integer>();
                }
            }
        }
    }

    // Adds a shift and removes a break for forklift employee
    public void addShiftForklift(int month, int day, int id) {
        List<Integer>[][] m = this.get(month);
        m[day][2].remove(new Integer(id));
        m[day][0].add(new Integer(id));
    }

    // Adds a shift and removes a break for non_forklift employee
    public void addShiftNormal(int month, int day, int id) {
        List<Integer>[][] m = this.get(month);
        m[day][3].remove(new Integer(id));
        m[day][1].add(new Integer(id));
    }

    // Adds all shifts to employees according to chosen breaks
    public void addShiftAll(int month, HashMap<Integer, Employee> employeeDB) throws ParseException {
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        System.out.println("Adding shifts automatically for " + ym.getMonth());

        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        int date;
        if (startDayTemp <= 2) {
            date = 1 + (2 - startDayTemp);
        }
        else {
            date = 1 + (9 - startDayTemp);
        }

        List<Integer> employeeForklift = new ArrayList<>();
        List<Integer> employeeNormal = new ArrayList<>();
        for (int id: employeeDB.keySet()) {
            if(employeeDB.get(id).getManager()) {
                continue;
            }
            if(employeeDB.get(id).getIsForklift()) {
                employeeForklift.add(id);
            }
            else {
                employeeNormal.add(id);
            }
        }

        while(date <= ym.lengthOfMonth()) {
            c.setTime(new SimpleDateFormat("yyyyMMdd").parse(Integer.toString(month *100 + date)));
            if (c.get(Calendar.DAY_OF_WEEK) != 1) {
                List<Integer> shiftForklift = this.get(month)[date][0];
                List<Integer> shiftNormal = this.get(month)[date][1];
                List<Integer> breakForklift = this.get(month)[date][2];
                List<Integer> breakNormal = this.get(month)[date][3];
                for(int id: employeeForklift) {
                    if(!breakForklift.contains(id)) {
                        shiftForklift.add(new Integer(id));
                    }
                }
                for(int id: employeeNormal) {
                    if(!breakNormal.contains(id)) {
                        shiftNormal.add(new Integer(id));
                    }
                }
            }
            date ++;
        }
        // the last day of month is not Saturday or Sunday
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (weekday > 1 && weekday < 6) {
            int leftoverDays = 7 - weekday;
            date = 1;
            YearMonth ymNext = ym.plusMonths(1);
            int monthNext = (ymNext.getYear() * 100) + ymNext.getMonthValue();
            List<Integer> shiftForklift = this.get(monthNext)[date][0];
            List<Integer> shiftNormal = this.get(monthNext)[date][1];
            List<Integer> breakForklift = this.get(monthNext)[date][2];
            List<Integer> breakNormal = this.get(monthNext)[date][3];

            while(date <= leftoverDays) {
                for(int id: employeeForklift) {
                    if(!breakForklift.contains(id)) {
                        shiftForklift.add(new Integer(id));
                    }
                }
                for(int id: employeeNormal) {
                    if(!breakNormal.contains(id)) {
                        shiftNormal.add(new Integer(id));
                    }
                }
                date ++;
            }
        }
    }

    // Adds a break and removes a shift to forklift employee
    public boolean addBreakForklift(int month, int day, int id) {
        List<Integer>[][] m = this.get(month);
        if(day > m.length+1 || day < 1) {
            System.out.println("Illegal Input, Please Enter Valid Day");
            return false;
        }
        if(m[day][2].contains(id)) {
            System.out.println("Break Exist, Please Select Another Day");
            return false;
        }
        else {
            m[day][0].remove(new Integer(id));
            m[day][2].add(new Integer(id));
            return true;
        }
    }

    // Adds a break and removes a shift to non_forklift employee
    public boolean addBreakNormal(int month, int day, int id) {
        List<Integer>[][] m = this.get(month);
        if(day > m.length+1 || day < 1) {
            System.out.println("Illegal Input, Please Enter Valid Day");
            return false;
        }
        if(m[day][3].contains(id)) {
            System.out.println("Break Exist, Please Select Another Day");
            return false;
        }
        else {
            m[day][1].remove(new Integer(id));
            m[day][3].add(new Integer(id));
            return true;
        }
    }

    // Exam and randomly select n-2 people who can't break
    // Modify existing break, and return list of needed to re-select
    public List<Integer> selectBreakForklift (int month, int day) {
        int forkliftBreak = 2;
        List<Integer> noBreak = new ArrayList<>();
        List<Integer> m = this.get(month)[day][2]; // break for forklift
        Random random = new Random();
        while(m.size() > forkliftBreak) {
            int randomIndex = random.nextInt(m.size());
            noBreak.add(m.get(randomIndex));
            m.remove(randomIndex);
        }
        return noBreak;
    }

    // Exam and randomly select n-4 people who can't break
    // Modify existing break, and return list of needed to re-select
    public List<Integer> selectBreakNormal(int month, int day) {
        int normalBreak = 4;
        List<Integer> noBreak = new ArrayList<>();
        List<Integer> m = this.get(month)[day][3]; // break for normal
        Random random = new Random();
        while(m.size() > normalBreak) {
            int randomIndex = random.nextInt(m.size());
            noBreak.add(m.get(randomIndex));
            m.remove(randomIndex);
        }
        return noBreak;
    }

    // Run break lottery in weekly base, and re-assign break in available day in the week, standalone mode
    public void breakLottery(int month) throws ParseException {
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        System.out.println("\nBreak lottery for " + ym.getMonth());

        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        int firstDay;
        if (startDayTemp <= 2) {
            firstDay = 1 + (2 - startDayTemp);
        }
        else {
            firstDay = 1 + (9 - startDayTemp);
        }

        // Lottery process
        while(firstDay <= ym.lengthOfMonth()) {
            System.out.println("Break Lottery: Week of " + m + " " + firstDay);
            // Process if week doesn't change month
            if(ym.lengthOfMonth() - firstDay > 6) {
                List<Integer> noBreakForklift = new ArrayList<>();
                List<Integer> noBreakNormal = new ArrayList<>();
                List<Integer> possibleBreakForklift = new ArrayList<>();
                List<Integer> possibleBreakNormal = new ArrayList<>();

                for(int date=firstDay; date<firstDay + 6; date++) {
                    noBreakForklift.addAll(selectBreakForklift(month, date));
                    noBreakNormal.addAll(selectBreakNormal(month, date));

                    // Adding break candidate
                    int leftBreakForklift = 2 - this.get(month)[date][2].size();
                    while(leftBreakForklift > 0) {
                        possibleBreakForklift.add(date);
                        leftBreakForklift --;
                    }
                    int leftBreakNormal = 4 - this.get(month)[date][3].size();
                    while(leftBreakNormal > 0) {
                        possibleBreakNormal.add(date);
                        leftBreakNormal --;
                    }
                }
                System.out.println("Re-assigned Breaks:");
                System.out.println("Forklift: " + noBreakForklift.toString());
                System.out.println("Normal: " + noBreakNormal.toString());

                // Re-assign break
                Random random = new Random();
                for(Integer id: noBreakForklift) {
                    int index = random.nextInt(possibleBreakForklift.size());
                    int day = possibleBreakForklift.get(index);
                    addBreakForklift(month, day, id);
                    possibleBreakForklift.remove(index);
                }
                for(Integer id: noBreakNormal) {
                    int index = random.nextInt(possibleBreakNormal.size());
                    int day = possibleBreakNormal.get(index);
                    addBreakNormal(month, day, id);
                    possibleBreakNormal.remove(index);
                }
                firstDay += 7;
            }
            // Process if week cross month
            else {
                int leftoverDays = 6 - (ym.lengthOfMonth() - firstDay + 1);
                int date = firstDay;
                List<Integer> noBreakForklift = new ArrayList<>();
                List<Integer> noBreakNormal = new ArrayList<>();
                List<int[]> possibleBreakForklift = new ArrayList<>(); //{YYYYMM, day} to indicate month
                List<int[]> possibleBreakNormal = new ArrayList<>();//{YYYYMM, day} to indicate month

                while (date <= ym.lengthOfMonth()) {
                    noBreakForklift.addAll(selectBreakForklift(month, date));
                    noBreakNormal.addAll(selectBreakNormal(month, date));

                    int leftBreakForklift = 2 - this.get(month)[date][2].size();
                    while(leftBreakForklift > 0) {
                        possibleBreakForklift.add(new int[]{month, date});
                        leftBreakForklift --;
                    }
                    int leftBreakNormal = 4 - this.get(month)[date][3].size();
                    while(leftBreakNormal > 0) {
                        possibleBreakNormal.add(new int[]{month, date});
                        leftBreakNormal --;
                    }
                    date ++;
                }
                date = 1;
                YearMonth ymNext = ym.plusMonths(1);
                int monthNext = (ym.getYear() * 100) + ymNext.getMonthValue();
                while (date <= leftoverDays) {
                    noBreakForklift.addAll(selectBreakForklift(monthNext, date));
                    noBreakNormal.addAll(selectBreakNormal(monthNext, date));

                    int leftBreakForklift = 2 - this.get(monthNext)[date][2].size();
                    while(leftBreakForklift > 0) {
                        possibleBreakForklift.add(new int[]{monthNext, date});
                        leftBreakForklift --;
                    }
                    int leftBreakNormal = 4 - this.get(monthNext)[date][3].size();
                    while(leftBreakNormal > 0) {
                        possibleBreakNormal.add(new int[]{monthNext, date});
                        leftBreakNormal --;
                    }
                    date ++;
                }
                System.out.println("Re-assigned Breaks:");
                System.out.println("Forklift: " + noBreakForklift.toString());
                System.out.print("Normal: " + noBreakNormal.toString());

                // Reassign break randomly
                Random random = new Random();
                for(Integer id: noBreakForklift) {
                    int index = random.nextInt(possibleBreakForklift.size());
                    int[] monthDay = possibleBreakForklift.get(index);
                    addBreakForklift(monthDay[0], monthDay[1], id);
                    possibleBreakForklift.remove(index);
                }
                for(Integer id: noBreakNormal) {
                    int index = random.nextInt(possibleBreakNormal.size());
                    int[] monthDay = possibleBreakNormal.get(index);
                    addBreakNormal(monthDay[0], monthDay[1], id);
                    possibleBreakNormal.remove(index);
                }
                break;
            }
        }
    }

    // Run break lottery in weekly base, and re-assign break in available day in the week, server mode
    public void breakLottery(int month, ThreadHandler thread) throws ParseException {
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        thread.outputStream.print("\nBreak lottery for " + ym.getMonth() + "\n");
        thread.end();

        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        int firstDay;
        if (startDayTemp <= 2) {
            firstDay = 1 + (2 - startDayTemp);
        }
        else {
            firstDay = 1 + (9 - startDayTemp);
        }

        // Lottery process
        while(firstDay <= ym.lengthOfMonth()) {
            thread.outputStream.print("Break Lottery: Week of " + m + " " + firstDay + "\n");
            thread.end();
            // Process if week doesn't change month
            if(ym.lengthOfMonth() - firstDay > 6) {
                List<Integer> noBreakForklift = new ArrayList<>();
                List<Integer> noBreakNormal = new ArrayList<>();
                List<Integer> possibleBreakForklift = new ArrayList<>();
                List<Integer> possibleBreakNormal = new ArrayList<>();

                for(int date=firstDay; date<firstDay + 6; date++) {
                    noBreakForklift.addAll(selectBreakForklift(month, date));
                    noBreakNormal.addAll(selectBreakNormal(month, date));

                    // Adding break candidate
                    int leftBreakForklift = 2 - this.get(month)[date][2].size();
                    while(leftBreakForklift > 0) {
                        possibleBreakForklift.add(date);
                        leftBreakForklift --;
                    }
                    int leftBreakNormal = 4 - this.get(month)[date][3].size();
                    while(leftBreakNormal > 0) {
                        possibleBreakNormal.add(date);
                        leftBreakNormal --;
                    }
                }
                thread.outputStream.print("Re-assigned Breaks:\n");
                thread.outputStream.print("Forklift: " + noBreakForklift.toString() + "\n");
                thread.outputStream.print("Normal: " + noBreakNormal.toString() + "\n");
                thread.end();

                // Re-assign break
                Random random = new Random();
                for(Integer id: noBreakForklift) {
                    int index = random.nextInt(possibleBreakForklift.size());
                    int day = possibleBreakForklift.get(index);
                    addBreakForklift(month, day, id);
                    possibleBreakForklift.remove(index);
                }
                for(Integer id: noBreakNormal) {
                    int index = random.nextInt(possibleBreakNormal.size());
                    int day = possibleBreakNormal.get(index);
                    addBreakNormal(month, day, id);
                    possibleBreakNormal.remove(index);
                }
                firstDay += 7;
            }
            // Process if week cross month
            else {
                int leftoverDays = 6 - (ym.lengthOfMonth() - firstDay + 1);
                int date = firstDay;
                List<Integer> noBreakForklift = new ArrayList<>();
                List<Integer> noBreakNormal = new ArrayList<>();
                List<int[]> possibleBreakForklift = new ArrayList<>(); //{YYYYMM, day} to indicate month
                List<int[]> possibleBreakNormal = new ArrayList<>();//{YYYYMM, day} to indicate month

                while (date <= ym.lengthOfMonth()) {
                    noBreakForklift.addAll(selectBreakForklift(month, date));
                    noBreakNormal.addAll(selectBreakNormal(month, date));

                    int leftBreakForklift = 2 - this.get(month)[date][2].size();
                    while(leftBreakForklift > 0) {
                        possibleBreakForklift.add(new int[]{month, date});
                        leftBreakForklift --;
                    }
                    int leftBreakNormal = 4 - this.get(month)[date][3].size();
                    while(leftBreakNormal > 0) {
                        possibleBreakNormal.add(new int[]{month, date});
                        leftBreakNormal --;
                    }
                    date ++;
                }
                date = 1;
                YearMonth ymNext = ym.plusMonths(1);
                int monthNext = (ym.getYear() * 100) + ymNext.getMonthValue();
                while (date <= leftoverDays) {
                    noBreakForklift.addAll(selectBreakForklift(monthNext, date));
                    noBreakNormal.addAll(selectBreakNormal(monthNext, date));

                    int leftBreakForklift = 2 - this.get(monthNext)[date][2].size();
                    while(leftBreakForklift > 0) {
                        possibleBreakForklift.add(new int[]{monthNext, date});
                        leftBreakForklift --;
                    }
                    int leftBreakNormal = 4 - this.get(monthNext)[date][3].size();
                    while(leftBreakNormal > 0) {
                        possibleBreakNormal.add(new int[]{monthNext, date});
                        leftBreakNormal --;
                    }
                    date ++;
                }
                thread.outputStream.print("Re-assigned Breaks:\n");
                thread.outputStream.print("Forklift: " + noBreakForklift.toString() + "\n");
                thread.outputStream.print("Normal: " + noBreakNormal + "\n");
                thread.end();

                // Reassign break randomly
                Random random = new Random();
                for(Integer id: noBreakForklift) {
                    int index = random.nextInt(possibleBreakForklift.size());
                    int[] monthDay = possibleBreakForklift.get(index);
                    this.addBreakForklift(monthDay[0], monthDay[1], id);
                    possibleBreakForklift.remove(index);
                }
                for(Integer id: noBreakNormal) {
                    int index = random.nextInt(possibleBreakNormal.size());
                    int[] monthDay = possibleBreakNormal.get(index);
                    this.addBreakNormal(monthDay[0], monthDay[1], id);
                    possibleBreakNormal.remove(index);
                }
                break;
            }
        }
    }

    // Prompt break selecting interface for employee, standalone mode
    public void addBreakEmployee(int id, int month, boolean isForklift, Scanner sc) throws ParseException {
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        System.out.println("\nSelecting break for " + ym.getMonth());
        System.out.println("\n(Enter B to go back)");
        System.out.println("Enter intended break day in integer");

        Queue<int[]> selectedBreak = new LinkedList<>();
        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        // get first day of working month
        int firstDay;
        if (startDayTemp <= 2) {
            firstDay = 1 + (2 - startDayTemp);
        }
        else {
            firstDay = 1 + (9 - startDayTemp);
        }

        while(firstDay <= ym.lengthOfMonth()) {
            if(ym.lengthOfMonth() - firstDay > 6) {
                System.out.print("Please select 1 break day from " + firstDay + " to " + (firstDay + 5) + ": ");
                int breakDay;
                String input;
                try {
                    input = sc.nextLine();
                    if(input.toLowerCase().equals("b")) {
                        return;
                    }
                    breakDay = Integer.parseInt(input);

                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input, please enter in Integer");
                    continue;
                }
                if (!(breakDay >= firstDay && breakDay < (firstDay + 6))) {
                    System.out.println("Invalid day selection, please choose from the given range");
                    continue;
                }
                else {
                    selectedBreak.add(new int[]{month, breakDay, id});
                    firstDay += 7;
                }
            }
            else {
                int leftoverDays = 6 - (ym.lengthOfMonth() - firstDay + 1);
                System.out.print("Please select 1 break day from " + firstDay + " to (" + (ym.plusMonths(1).getMonth()) + ") " + leftoverDays + ": ");
                int breakDay;
                String input;
                try {
                    input = sc.nextLine();
                    if(input.toLowerCase().equals("b")) {
                        return;
                    }
                    breakDay = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input, please enter in Integer");
                    continue;
                }
                if (!(breakDay >= firstDay && breakDay <= ym.lengthOfMonth()) && !(breakDay > 0 && breakDay <=leftoverDays)) {
                    System.out.println("Invalid day selection, please choose from the given range");
                }
                else {
                    if(breakDay < firstDay) {
                        YearMonth ymNext = ym.plusMonths(1);
                        month = (ymNext.getYear() * 100) + ymNext.getMonthValue();
                    }
                    selectedBreak.add(new int[]{month, breakDay, id});
                    break;
                }
            }
        }
        System.out.println("Selected break:");
        for(int[] breakDay: selectedBreak) {
            System.out.println((breakDay[0]*100 + breakDay[1]));
        }
        System.out.println("Confirm (Y/N)");
        String input = sc.nextLine().toLowerCase();
        if(!input.equals("y")) {
            System.out.println("Canceled");
            return;
        }
        else {
            if(isForklift) {
                while(!selectedBreak.isEmpty()) {
                    int[] array = selectedBreak.remove();
                    addBreakForklift(array[0], array[1], array[2]);
                }
            }
            else {
                while(!selectedBreak.isEmpty()) {
                    int[] array = selectedBreak.remove();
                    addBreakNormal(array[0], array[1], array[2]);
                }
            }
        }
        System.out.println("Breaks added");
    }

    // Prompt break selecting interface for employee, server mode
    public void addBreakEmployee(int id, int month, boolean isForklift, ThreadHandler thread) throws ParseException, IOException {
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        thread.outputStream.print("\nSelecting break for " + ym.getMonth() + "\n");
        thread.outputStream.print("Enter intended break day in integer\n");
        thread.end();

        Queue<int[]> selectedBreak = new LinkedList<>(); // temporary break holder: [month, day, id]
        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        // get first day of working month
        int firstDay;
        if (startDayTemp <= 2) {
            firstDay = 1 + (2 - startDayTemp);
        }
        else {
            firstDay = 1 + (9 - startDayTemp);
        }

        thread.outputStream.print("\n(Enter B to go back)\n");
        thread.end();
        while(firstDay <= ym.lengthOfMonth()) {
            String input;
            if(ym.lengthOfMonth() - firstDay > 6) {
                thread.outputStream.print("Please select 1 break day from " + firstDay + " to " + (firstDay + 5) + ": ");
                thread.endAnswer();
                int breakDay;
                try {
                    input = thread.inputStream.readLine();
                    if(input.toLowerCase().equals("b")) {
                        return;
                    }
                    breakDay = Integer.parseInt(input);
                }
                catch (NumberFormatException e) {
                    thread.outputStream.print("Invalid Input, please enter in Integer\n");
                    thread.end();
                    continue;
                }
                if (!(breakDay >= firstDay && breakDay < (firstDay + 6))) {
                    thread.outputStream.print("Invalid day selection, please choose from the given range\n");
                    thread.end();
                    continue;
                }
                else {
                    selectedBreak.add(new int[]{month, breakDay, id});
                    firstDay += 7;
                }
            }
            else {
                int leftoverDays = 6 - (ym.lengthOfMonth() - firstDay + 1);
                thread.outputStream.print("Please select 1 break day from " + firstDay + " to (" + (ym.plusMonths(1).getMonth()) + ")" + leftoverDays + ": ");
                thread.endAnswer();
                int breakDay;
                try {
                    input = thread.inputStream.readLine();
                    if(input.toLowerCase().equals("b")) {
                        return;
                    }
                    breakDay = Integer.parseInt(input);
                }
                catch (NumberFormatException e) {
                    thread.outputStream.print("Invalid Input, please enter in Integer\n");
                    thread.end();
                    continue;
                }
                if (!(breakDay >= firstDay && breakDay <= ym.lengthOfMonth()) && !(breakDay > 0 && breakDay <=leftoverDays)) {
                    thread.outputStream.print("Invalid day selection, please choose from the given range\n");
                    thread.end();
                    continue;
                }
                else {
                    if(breakDay < firstDay) {
                        YearMonth ymNext = ym.plusMonths(1);
                        month = (ymNext.getYear() * 100) + ymNext.getMonthValue();
                    }
                    selectedBreak.add(new int[]{month, breakDay, id});
                    break;
                }
            }
        }
        thread.outputStream.print("Selected break:\n");
        for(int[] breakDay: selectedBreak) {
            thread.outputStream.print((breakDay[0]*100 + breakDay[1]) + "\n");
        }
        System.out.println("Confirm (Y/N)");
        thread.endAnswer();
        String input = thread.inputStream.readLine().toLowerCase();
        if(!input.equals("y")) {
            thread.outputStream.print("Canceled\n");
            thread.end();
            return;
        }
        else {
            if(isForklift) {
                while(!selectedBreak.isEmpty()) {
                    int[] array = selectedBreak.remove();
                    addBreakForklift(array[0], array[1], array[2]);
                }
            }
            else {
                while(!selectedBreak.isEmpty()) {
                    int[] array = selectedBreak.remove();
                    addBreakNormal(array[0], array[1], array[2]);
                }
            }
        }
        thread.outputStream.print("Breaks added");
        thread.end();
    }

    // Print break schedule of certain month, standalone mode
    public void printBreakCalendar (int month) throws ParseException{
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        System.out.println("Printing break for " + ym.getYear() + " " + ym.getMonth() + "\n");

        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        int firstDay;
        if (startDayTemp <= 2) {
            firstDay = 1 + (2 - startDayTemp);
        }
        else {
            firstDay = 1 + (9 - startDayTemp);
        }

        while(firstDay <= ym.lengthOfMonth()) {
            if(ym.lengthOfMonth() - firstDay > 6) {
                int date = firstDay;
                int endDate = date + 6;
                while (date < endDate) {
                    System.out.println(m + " " + date);
                    System.out.println("Forklift: " + this.get(month)[date][2].toString());
                    System.out.println("Normal: " + this.get(month)[date][3].toString());
                    System.out.println();
                    date ++;
                }
                firstDay += 7;
            }
            else {
                int leftoverDays = 6 - (ym.lengthOfMonth() - firstDay + 1);
                int date = firstDay;
                while (date <= ym.lengthOfMonth()) {
                    System.out.println(m + " " + date);
                    System.out.println("Forklift: " + this.get(month)[date][2].toString());
                    System.out.println("Normal: " + this.get(month)[date][3].toString());
                    System.out.println();
                    date ++;
                }
                date = 1;
                YearMonth ymNext = ym.plusMonths(1);
                int monthNext = (ymNext.getYear() * 100) + ymNext.getMonthValue();
                while (date <= leftoverDays) {
                    System.out.println((ymNext.getYear()*100 + ymNext.getMonthValue()) + " " + date);
                    System.out.println("Forklift: " + this.get(monthNext)[date][2].toString());
                    System.out.println("Normal: " + this.get(monthNext)[date][3].toString());
                    System.out.println();
                    date ++;
                }
                break;
            }
        }
    }

    // Print break schedule of certain month, server mode
    public void printBreakCalendar (int month, ThreadHandler thread) throws ParseException {
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        thread.outputStream.print("Printing break for " + ym.getYear() + " " + ym.getMonth() + "\n\n");
        thread.end();

        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        int firstDay;
        if (startDayTemp <= 2) {
            firstDay = 1 + (2 - startDayTemp);
        }
        else {
            firstDay = 1 + (9 - startDayTemp);
        }

        while(firstDay <= ym.lengthOfMonth()) {
            if(ym.lengthOfMonth() - firstDay > 6) {
                int date = firstDay;
                int endDate = date + 6;
                while (date < endDate) {
                    thread.outputStream.print(m + " " + date + "\n");
                    thread.outputStream.print("Forklift: " + this.get(month)[date][2].toString() + "\n");
                    thread.outputStream.print("Normal: " + this.get(month)[date][3].toString() + "\n");
                    thread.outputStream.print("\n");
                    thread.end();
                    date ++;
                }
                firstDay += 7;
            }
            else {
                int leftoverDays = 6 - (ym.lengthOfMonth() - firstDay + 1);
                int date = firstDay;
                while (date <= ym.lengthOfMonth()) {
                    thread.outputStream.print(m + " " + date + "\n");
                    thread.outputStream.print("Forklift: " + this.get(month)[date][2].toString() + "\n");
                    thread.outputStream.print("Normal: " + this.get(month)[date][3].toString() + "\n");
                    thread.outputStream.print("\n");
                    thread.end();
                    date ++;
                }
                date = 1;
                YearMonth ymNext = ym.plusMonths(1);
                int monthNext = (ymNext.getYear() * 100) + ymNext.getMonthValue();
                while (date <= leftoverDays) {
                    thread.outputStream.print((ymNext.getYear()*100 + ymNext.getMonthValue()) + " " + date + "\n");
                    thread.outputStream.print("Forklift: " + this.get(month)[monthNext][2].toString() + "\n");
                    thread.outputStream.print("Normal: " + this.get(month)[monthNext][3].toString() + "\n");
                    thread.outputStream.print("\n");
                    thread.end();
                    date ++;
                }
                break;
            }
        }
    }

    // Return List<Integer> of break days for a employee in YYYYMMDD of current month
    public List<Integer> viewBreak (int id, boolean isForklift, int month) throws ParseException{
        List<Integer> breaks = new ArrayList<>();
        YearMonth ym = YearMonth.of(month/100, month%100); // initial month
        Calendar c = Calendar.getInstance();
        String m = Integer.toString(month);
        c.setTime(new SimpleDateFormat("yyyyMMdd").parse(m + "01"));
        int startDayTemp = c.get(Calendar.DAY_OF_WEEK);

        int firstDay;
        if (startDayTemp <= 2) {
            firstDay = 1 + (2 - startDayTemp);
        }
        else {
            firstDay = 1 + (9 - startDayTemp);
        }

        while(firstDay <= ym.lengthOfMonth()) {
            if(ym.lengthOfMonth() - firstDay > 6) {
                int date = firstDay;
                int endDate = date + 6;
                while (date < endDate) {
                    if(isForklift) {
                        if(this.get(month)[date][2].contains(id)) {
                            breaks.add((month * 100) + date);
                        }
                    }
                    else {
                        if(this.get(month)[date][3].contains(id)) {
                            breaks.add((month * 100) + date);
                        }
                    }
                    date ++;
                }
                firstDay += 7;
            }
            else {
                int leftoverDays = 6 - (ym.lengthOfMonth() - firstDay + 1);
                int date = firstDay;
                while (date <= ym.lengthOfMonth()) {
                    if(isForklift) {
                        if(this.get(month)[date][2].contains(id)) {
                            breaks.add((month * 100) + date);
                        }
                    }
                    else {
                        if(this.get(month)[date][3].contains(id)) {
                            breaks.add((month * 100) + date);
                        }
                    }
                    date ++;
                }
                date = 1;
                YearMonth ymNext = ym.plusMonths(1);
                int monthNext = (ymNext.getYear() * 100) + ymNext.getMonthValue();
                while (date <= leftoverDays) {
                    if(isForklift) {
                        if(this.get(monthNext)[date][2].contains(id)) {
                            breaks.add((monthNext * 100) + date);
                        }
                    }
                    else {
                        if(this.get(monthNext)[date][3].contains(id)) {
                            breaks.add((monthNext * 100) + date);
                        }
                    }
                    date ++;
                }
                break;
            }
        }
        return breaks;
    }

}
