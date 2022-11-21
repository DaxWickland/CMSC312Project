import java.util.ArrayList;
import java.util.Scanner;

public class CPUScheduler {
    public static final int MAX_MEM = 512000;
    public static int SYS_MEM = 512000;
    public static String[][] pageTable = new String[10][2];
    public static int pageLoop = 0;


    public static final ArrayList<Integer> PCB = new ArrayList<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        initPageTable(pageTable);
        int processNum;
        int buffer = 20;
        boolean cont = true;
        ArrayList<CProcess> processList = new ArrayList<>();
        ArrayList<CProcess> processList1 = new ArrayList<>();
        while (cont) {
            System.out.println("Enter a number to test function");
            System.out.println("Press (1) to create n processes.");
            System.out.println("Press (2) to view all processes");
            System.out.println("Press (3) to compute processes as first come first serve");
            System.out.println("Press (4) to compute processes as Round Robin");
            System.out.println("Press (5) to view current available system memory");
            System.out.println("Press (6) to terminate the program");
            System.out.println("Press (7) to view current page table");
            int userIn = in.nextInt();
            switch (userIn) {
                case 1:
                    System.out.println("Enter number of processes to create (n)");
                    processNum = in.nextInt();
                    processList = CProcess.createProcessList(processNum);
                    for(int x = 0; x < processList.size(); x++){
                        if(SYS_MEM - processList.get(x).getMemory() >= 0) {
                            processList.get(x).setState("READY");
                            SYS_MEM -= processList.get(x).getMemory();
                        }
                        else{processList.get(x).setState("NEW");}
                    }
                    processList1 = processList;
                    System.out.println();
                    break;
                case 2:
                    System.out.println("Listing all processes.");
                    System.out.println();
                    printProcesses(processList);
                    System.out.println();
                    break;

                case 3:
                    System.out.println("computing processes as first come first serve");
                    firstComeFirstServed(processList1, buffer);
                    System.out.println();
                    printPageTable(pageTable);
                    System.out.println();
                    PCB.clear();
                    break;

                case 4:
                    System.out.println("computing processes as round-robin");
                    RoundRobin(processList1, buffer);
                    System.out.println();
                    printPageTable(pageTable);
                    System.out.println();
                    PCB.clear();
                    break;

                case 5:
                    System.out.println("Showing Current system available memory");
                    System.out.println(SYS_MEM);
                    System.out.println();
                    break;

                case 6:
                    cont = false;
                    break;

                case 7:
                    printPageTable(pageTable);
                    break;
            }

        }
    }

    public static void printProcesses(ArrayList<CProcess> processList) {
        for (int x = 0; x < processList.size(); x++) {
            System.out.println(processList.get(x).getID());
            System.out.println("Memory Requirement: " + processList.get(x).getMemory() + " KB");
            processList.get(x).getAllTasks();
            System.out.println();
        }
    }

    public static void initPageTable(String[][] pageTable){
        for(int x = 0; x <= pageTable.length - 1; x++){
            for(int y = 0; y <= pageTable[x].length - 1; y++){
                if(y == 0){
                    pageTable[x][y] = "NONE";
                }
                else {
                    pageTable[x][y] = "0";
                }
            }
        }
    }

    public static boolean checkTable(String[][] pageTable) {
        boolean result = true;
        for (int x = 0; x <= pageTable.length - 1; x++) {
            if(!(pageTable[x][0].equals("NONE"))){
                result = false;
            }
            else{
                result = true;
                return result;
            }
        }
        return result;
    }

    public static void allocatePage(String[][] pageTable, Task task, CProcess process){
        for(int x = 0; x <= pageTable.length - 1; x++){
                if(pageTable[x][0].equals("NONE")){
                    pageTable[x][0] = process.getID() + " " + task.getWkType();
                    pageTable[x][1] = "1";
                    break;
                }
        }
    }

    public static void markPage(String[][] pageTable, Task task, CProcess process){
        for(int x = 0; x <= pageTable.length - 1; x++){
            if(pageTable[x][0].equals(process.getID() + " " + task.getWkType()) && pageTable[x][1] != "0"){
                pageTable[x][1] = "0";
                break;
            }
        }
    }

    public static boolean pageExists(String[][] pageTable, Task task, CProcess process){
        for(int x = 0; x <= pageTable.length - 1; x++){
            if(pageTable[x][0].equals(process.getID() + " " + task.getWkType()) && !(pageTable[x][1].equals("0"))){
                return true;
            }
        }
        return false;
    }

    public static void victimSelectPageTable(String[][] pageTable, Task task, CProcess process){
        if(pageLoop >= pageTable.length){
            pageLoop = pageLoop % pageTable.length;
        }
        for(int x = pageLoop; x <= pageTable.length - 1; x++){
            if(pageLoop == pageTable.length){
                pageLoop = pageLoop % pageTable.length;
            }
            if(pageTable[x][1].equals("0")){
                pageTable[x][0] = process.getID() + " " + task.getWkType();
                pageTable[x][1] = "1";
                pageLoop++;
                break;
            }
        }
    }
    public static void printPageTable(String[][] pageTable){
        for(int x = 0; x <= pageTable.length - 1; x++){
            for(int y = 0; y <= pageTable[x].length - 1; y++){
                System.out.print(pageTable[x][y] + " ");
            }
            System.out.println();
        }
    }

    public static void firstComeFirstServed(ArrayList<CProcess> processes, int bufferTimer) {
        for (int x = 0; x <= processes.size() - 1; x++) {

            if(processes.get(x).getState() == "READY") {
                System.out.println(processes.get(x).getMemory() + " KB of memory allocated");
                processes.get(x).setState("RUN");

            System.out.println(processes.get(x).getID() + " " +  "set to run");
            while (processes.get(x).getTasks().size() > PCB.get(x)) {
                System.out.println("Process:   Task:   Status:");
                printPageTable(pageTable);
                System.out.println();
                Task task = processes.get(x).getTasks().get(PCB.get(x));
                if(checkTable(pageTable) == true){
                    allocatePage(pageTable, task, processes.get(x));
                }
                else{
                    victimSelectPageTable(pageTable, task, processes.get(x));
                }
                while (task.getTaskBuffer() > 0) {
                    if(processes.get(x).getTasks().get(PCB.get(x)).getWkType() == "FORK"){
                        System.out.println(processes.get(x).getID() + " " + "forked");
                        System.out.println();
                        processes.add(CProcess.createSingleProcess());
                        PCB.add(0);
                    }
                    if(processes.get(x).getTasks().get(PCB.get(x)).getWkType() == "IO"){
                        System.out.println(processes.get(x).getID() + " waiting for IO");
                        processes.get(x).setState("WAIT");
                        System.out.println(processes.get(x).getID() + " state set to WAIT");
                    }
                    processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() - bufferTimer);
                }
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " " + "completed");
                markPage(pageTable, task, processes.get(x));
                System.out.println();
                PCB.set(x, PCB.get(x) + 1);
            }
            processes.get(x).setState("Terminated");
            if(SYS_MEM < MAX_MEM) {
                SYS_MEM += processes.get(x).getMemory();
            }
            else{SYS_MEM = MAX_MEM;}
            System.out.println();
            System.out.println(processes.get(x).getID() + " " + "terminated");
            System.out.println(processes.get(x).getMemory() + " KB of memory freed");
            System.out.println();
            if (processes.size() == 0) {
                System.out.println("All processes completed");
            }
        }
            else {
                if(processes.get(x).getMemory() <= SYS_MEM){
                    processes.get(x).setState("READY");
                    SYS_MEM -= processes.get(x).getMemory();
                    x--;
                }
                else{
                    System.out.println("Error: SYS_MEM error, not enough memory. Don't know how you got here since its first-come-first-serve");
                }

            }
        }

    }

    public static void RoundRobin(ArrayList<CProcess> processes, int buffer){
        int PCBLoc = 0;
        Task critcalTask = null;
        int finishedP = 0;
        for (int x = 0; x <= processes.size(); x++) {
            if(x > processes.size() - 1){
                for(int y = 0; y <= PCB.size() - 1 ; y++){
                    if((PCB.get(y) <= processes.get(y).getTasks().size())){
                        x = x % processes.size();
                    }
                    else if(PCB.get(y) > 149){
                        if(finishedP == PCB.size()){
                            System.out.println("All processes completed.");
                            return;
                        }
                    }
                }
            }
            if(processes.get(x).getTasks().size() <= PCB.get(x)){
                continue;
            }
            if((processes.get(x).getState().equals("NEW"))) {
                if (SYS_MEM >= processes.get(x).getMemory()) {
                    System.out.println(processes.get(x).getMemory() + " KB of memory allocated");
                    SYS_MEM -= processes.get(x).getMemory();
                    processes.get(x).setState("READY");
                }
                else{continue;}
                }
            if(!(processes.get(x).getState().equals("NEW")) && !(pageExists(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x)))){
                victimSelectPageTable(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x));
            }
            processes.get(x).setState("RUN");
            System.out.println("Process:   Task:   Status:");
            printPageTable(pageTable);
            System.out.println(processes.get(x).getID() + " " +  "set to run");
            System.out.println();
            if(processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true && critcalTask == null && pageExists(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x))){
                if(processes.get(x).getTasks().get(PCB.get(x)).getWkType().matches("FORK")){
                    System.out.println(processes.get(x).getID() + " " + "forked");
                    System.out.println();
                    processes.add(CProcess.createSingleProcess());
                    PCB.add(0);
                    processes.get(processes.size()-1).setState("NEW");
                }
                critcalTask = processes.get(x).getTasks().get(PCB.get(x));
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " " + "set as critcal");
                processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() - buffer);
            }
            else if(critcalTask == processes.get(x).getTasks().get(PCB.get(x)) && pageExists(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x))){
                processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() - buffer);
            }
            else if (processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true && (critcalTask != processes.get(x).getTasks().get(PCB.get(x)) || critcalTask != null)){
                continue;
            }
            else{

                if(processes.get(x).getTasks().get(PCB.get(x)).getWkType().matches("FORK")){
                    System.out.println(processes.get(x).getID() + " " + "forked");
                    System.out.println();
                    processes.add(CProcess.createSingleProcess());
                    PCB.add(0);
                    processes.get(processes.size()-1).setState("NEW");
                }
                processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() - buffer);
            }
            if(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() <= 0){
                if(processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true){
                    critcalTask = null;
                }
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " for " + processes.get(x).getID() + " completed");
                markPage(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x));
                if (PCB.get(x) < processes.get(x).getTasks().size()) {
                    PCB.set(x, PCB.get(x) + 1);
                }
                else{
                    System.out.println(processes.get(x).getID() + " completed");
                    finishedP++;
                    PCB.set(x, 150);
                    if(SYS_MEM < MAX_MEM) {
                        SYS_MEM += processes.get(x).getMemory();
                    }
                    else if(SYS_MEM >= MAX_MEM){
                        SYS_MEM = MAX_MEM;
                    }
                }
            }

            if(PCB.get(x) < processes.get(x).getTasks().size()){
                if(SYS_MEM >= processes.get(x).getMemory() && !(processes.get(x).getState().equals("READY") || processes.get(x).getState().equals("RUN"))) {
                    processes.get(x).setState("READY");
                    System.out.println(processes.get(x).getID() + " " +  "set to READY");
                }
                else{continue;}
            }
            else if(processes.get(x).getTasks().size() <= PCB.get(x)){
                System.out.println(processes.get(x).getID() + " completed");
                finishedP++;
                PCB.set(x, 150);
                if(SYS_MEM < MAX_MEM) {
                    SYS_MEM += processes.get(x).getMemory();
                }
                else if(SYS_MEM >= MAX_MEM){
                    SYS_MEM = MAX_MEM;
                }
            }

        }

    }


}
