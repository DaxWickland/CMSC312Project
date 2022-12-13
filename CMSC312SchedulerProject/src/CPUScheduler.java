import java.util.ArrayList;
import java.util.Scanner;

public class CPUScheduler {
    public static final int MAX_MEM = 512000;
    public static int SYS_MEM = 512000;
    public static String[][] pageTable = new String[10][2];
    public static int pageLoop = 0;
    public static int buffer = 20;
    public static int threadcount = 0;
    public static NewThread Thread1 = new NewThread(1, null);
    public static NewThread Thread2 = new NewThread(2, null);
    public static NewThread Thread3 = new NewThread(3, null);
    public static NewThread Thread4 = new NewThread(4, null);
    public static NewThread Thread5 = new NewThread(5, null);
    public static NewThread Thread6 = new NewThread(6, null);
    public static NewThread Thread7 = new NewThread(7, null);
    public static NewThread Thread8 = new NewThread(8, null);
    public static int completedThreads1 = 0;
    public static int completedThreads2 = 0;

    public static ArrayList<Integer> PCB = new ArrayList<>();
    public static final ArrayList<NewThread> CPU1 = new ArrayList<>(4);
    public static final ArrayList<NewThread> CPU2 = new ArrayList<>(4);
    public static final ArrayList<CProcess> CPU1ProcessList = new ArrayList<>();
    public static final ArrayList<CProcess> CPU2ProcessList = new ArrayList<>();
    

    public static void main(String[] args) {
        CPU1.add(Thread1); CPU1.add(Thread2); CPU1.add(Thread3); CPU1.add(Thread4);
        CPU2.add(Thread5); CPU2.add(Thread6); CPU2.add(Thread7); CPU2.add(Thread8);
        Scanner in = new Scanner(System.in);
        initPageTable(pageTable);
        int processNum;
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
            System.out.println("Press (6) to compute processes as multi-thread and multi-CPU first come first serve");
            System.out.println("Press (7) to compute processes as multi-thread and multi-CPU round-robin");
            System.out.println("Press (8) to view the system page table");
            System.out.println("Press (9) to terminate the program");
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
                    firstComeFirstServed(processList, buffer);
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
                System.out.println("computing processes as multi-thread and multi-CPU first come first serve");
                splitProcesses(processList1);
                firstComeFirstServed2(CPU1ProcessList, buffer, CPU1);
                firstComeFirstServed2(CPU2ProcessList, buffer, CPU2);

                case 7:
                System.out.println("computing processes as multi-thread and multi-CPU round-robin");
                    splitProcesses(processList);
                    RoundRobin2(CPU1ProcessList, buffer, CPU1);
                    RoundRobin2(CPU2ProcessList, buffer, CPU2);
                case 8:
                    printPageTable(pageTable);
                    break;

                case 9:
                    cont = false;
                    in.close();
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

    public static void splitProcesses(ArrayList<CProcess> processes){
        for(int i = 0; i <= processes.size() - 1; i++ ){
            if(processes.get(i).getCPU() == 1){
                CPU1ProcessList.add(processes.get(i));
            }
            else{
                CPU2ProcessList.add(processes.get(i));
            }
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

    public static Thread checkThread(ArrayList<NewThread> threads){
        for (int i = 0; i <= threads.size() - 1; i++){
            if (!(threads.get(i).isAlive())){
                return threads.get(i);
            }
        }
        return null;
    }

    public static void firstComeFirstServed(ArrayList<CProcess> processes, int bufferTimer) {
        for (int x = 0; x <= processes.size() - 1; x++) {

            if(processes.get(x).getState() == "READY") {
                System.out.println(processes.get(x).getMemory() + " KB of memory allocated");
                processes.get(x).setState("RUN");
                System.out.println(processes.get(x).getID() + " " +  "set to run");

                while (processes.get(x).getTasks().size() > PCB.get(x)) {
                    System.out.println("Process:   Task:   Status:");
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
                        task.setTaskBuffer(task.getTaskBuffer() - bufferTimer);
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
                    continue;
                }
                else{
                    System.out.println("Error: SYS_MEM error, not enough memory. Don't know how you got here since its first-come-first-serve");
                }

            }
        }

    }

    public static void firstComeFirstServed2(ArrayList<CProcess> processes, int bufferTimer, ArrayList<NewThread> CPUThreads){
        int currThread = 0;
        int started = 0;
        while(processes.size() > 0 && completedThreads1 < 4 && completedThreads2 < 4){
            if(processes.get(0).getState() == "READY") {
                System.out.println(processes.get(0).getMemory() + " KB of memory allocated");
                processes.get(0).setState("RUN");
                System.out.println(processes.get(0).getID() + " " +  "set to run");
                if(currThread > 3){
                    currThread %= 4;
                }
                if(currThread <= 3){
                    processes.get(0).setRunType(0);
                    CPUThreads.get(currThread).setProcess(processes.get(0));
                    processes.remove(0);
                    if(started <= 3){
                    CPUThreads.get(currThread).start();
                    started++;
                    }
                    currThread++;
                }
            }
            else {
                if(processes.get(0).getMemory() <= SYS_MEM){
                    processes.get(0).setState("READY");
                    SYS_MEM -= processes.get(0).getMemory();
                    continue;
                }
        }
        for(int i =0; i <= CPUThreads.size() - 1 ; i++){
            try {
                CPUThreads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    }

    public static void RoundRobin(ArrayList<CProcess> processes, int buffer){
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

    public static void RoundRobin2(ArrayList<CProcess> processes, int bufferTimer, ArrayList<NewThread> CPUThreads){
        for(int q = 0; q <= processes.size() -1; q++){
            processes.get(q).setRunType(2);
        }
        Task critcalTask = null;
        int finishedP = 0;
        int currThread = 0;
        int started = 0;
        PCB = new ArrayList<>();
        for(int u = 0; u <= processes.size(); u++){
            PCB.add(0);
        }
        for (int x = 0; x <= processes.size() - 1; x++) {
            if(x > processes.size() - 2){
                for(int y = 0; y < PCB.size() - 1 ; y++){
                    if((PCB.get(y) <= processes.get(y).getTasks().size() - 1)){
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
            if(CPUThreads.size() <= 0){
                return;
            }
            for(int q = 0; q <= processes.size() -1; q++){
                processes.get(q).setRunType(2);
            }
            if (currThread >= 3){
                currThread %= 3;
            }
            if(processes.get(x).getTasks().size() <= PCB.get(x) && PCB.size() > 0){
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
            System.out.println(processes.get(x).getID() + " " +  "set to run");
            System.out.println();
            if(processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true && critcalTask == null && pageExists(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x))){
                if(processes.get(x).getTasks().get(PCB.get(x)).getWkType().matches("FORK")){
                    System.out.println(processes.get(x).getID() + " " + "forked");
                    System.out.println();
                    processes.add(CProcess.createSingleProcess());
                    PCB.add(0);
                    processes.get(processes.size()-1).setState("NEW");
                    processes.get(processes.size()-1).setRunType(2);
                }
                critcalTask = processes.get(x).getTasks().get(PCB.get(x));
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " " + "set as critcal");
                    int ID = CPUThreads.get(currThread).getID();
                    CPUThreads.set(currThread, new NewThread(ID, processes.get(x)));
                    CPUThreads.get(currThread).setPCB(PCB.get(x));
                    CPUThreads.get(currThread).run();
                
            }
            else if(critcalTask == processes.get(x).getTasks().get(PCB.get(x)) && pageExists(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x)) ){
                if(CPUThreads.get(currThread).isInterrupted() || CPUThreads.get(currThread).getProcess() == null){
                    if(started <= 3){
                        if(CPUThreads.get(started).getID() <= 3){
                        PCB.set(x, CPUThreads.get(x).getPCB());
                        CPUThreads.get(started).setProcess(processes.get(x));
                        CPUThreads.get(started).setPCB(PCB.get(x));
                        CPUThreads.get(started).run();
                        started++;
                        currThread++;
                        }
                    }
                        PCB.set(x, CPUThreads.get(x).getPCB());
                        CPUThreads.get(currThread).setPCB(PCB.get(x));
                        CPUThreads.get(currThread).run();
                        currThread++;
                }
                
            }
            else if (processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true && (critcalTask != processes.get(x).getTasks().get(PCB.get(x)) || critcalTask != null)){
                continue;
            }
            else{
                if(CPUThreads.size() <= 0){
                    return;
                }

                if(processes.get(x).getTasks().get(PCB.get(x)).getWkType().matches("FORK")){
                    System.out.println(processes.get(x).getID() + " " + "forked");
                    System.out.println();
                    processes.add(CProcess.createSingleProcess());
                    PCB.add(0);
                    processes.get(processes.size()-1).setState("NEW");
                }

                        PCB.set(x, CPUThreads.get(currThread).getPCB());
                        CPUThreads.get(currThread).setProcess(processes.get(x));
                        CPUThreads.get(currThread).setPCB(PCB.get(x));
                        CPUThreads.get(currThread).run();

            }
            processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(CPUThreads.get(currThread).getPCB());
            currThread++;
            if(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() <= 0){
                if(processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true){
                    critcalTask = null;
                }
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " for " + processes.get(x).getID() + " completed");
                markPage(pageTable, processes.get(x).getTasks().get(PCB.get(x)), processes.get(x));
                if (PCB.get(x) < processes.get(x).getTasks().size()) {
                    PCB.set(x, PCB.get(x) + 1);
                }
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
        }
    }

