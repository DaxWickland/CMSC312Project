import java.util.ArrayList;
import java.util.Scanner;

public class CPUScheduler {

    public static final ArrayList<Integer> PCB = new ArrayList<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int processNum = 0;
        int buffer = 20;
        boolean cont = true;
        ArrayList<CProcess> processList = new ArrayList<>();
        ArrayList<CProcess> processList1 = new ArrayList<>();
        ArrayList<CProcess> processList2 = new ArrayList<>();
        while (cont) {
            System.out.println("Enter a number to test function");
            System.out.println("Press (1) to create n processes.");
            System.out.println("Press (2) to view all processes");
            System.out.println("Press (3) to compute processes as first come first serve");
            System.out.println("Press (4) to compute processes as Round Robin");
            System.out.println("Press (5) to terminate program");
            int userIn = in.nextInt();
            switch (userIn) {
                case 1:
                    System.out.println("Enter number of processes to create (n)");
                    processNum = in.nextInt();
                    processList = CProcess.createProcessList(processNum);
                    for(int x = 0; x < processList.size(); x++){
                        processList.get(x).setState("READY");
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
                    break;

                case 4:
                    System.out.println("computing processes as round-robin");
                    RoundRobin(processList1, buffer);
                    System.out.println();
                    break;

                case 5:
                    cont = false;
            }
        }
    }

    public static void printProcesses(ArrayList<CProcess> processList) {
        for (int x = 0; x < processList.size(); x++) {
            System.out.println(processList.get(x).getID());
            processList.get(x).getAllTasks();
            System.out.println();
        }
    }

    public static void firstComeFirstServed(ArrayList<CProcess> processes, int bufferTimer) {
        for (int x = 0; x <= processes.size(); x++) {
            processes.get(x).setState("RUN");
            System.out.println(processes.get(x).getID() + " " +  "set to run");
            while (processes.get(x).getTasks().size() > PCB.get(x)) {
                System.out.println();
                Task task = processes.get(x).getTasks().get(PCB.get(x));
                while (task.getTaskBuffer() > 0) {
                    if(processes.get(x).getTasks().get(PCB.get(x)).getWkType() == "FORK"){
                        System.out.println(processes.get(x).getID() + " " + "forked");
                        System.out.println();
                        processes.add(CProcess.createSingleProcess());
                        PCB.add(0);
                        processes.get(processes.size()-1).setState("READY");
                    }
                    if(processes.get(x).getTasks().get(PCB.get(x)).getWkType() == "IO"){
                        System.out.println(processes.get(x).getID() + " waiting for IO");
                        processes.get(x).setState("WAIT");
                        System.out.println(processes.get(x).getID() + " state set to WAIT");
                    }
                    processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() - bufferTimer);
                }
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " " + "completed");
                System.out.println();
                PCB.set(x, PCB.get(x) + 1);
            }
            processes.get(x).setState("Terminated");
            System.out.println();
            System.out.println(processes.get(x).getID() + " " + "terminated");
            System.out.println();
            if (processes.size() == 0) {
                System.out.println("All processes completed");
            }
        }

    }

    public static void RoundRobin(ArrayList<CProcess> processes, int buffer){
        System.out.println("Printing all processes currently in queue.");
        printProcesses(processes);
        Task critcalTask = null;
        int finishedP = 0;
        for (int x = 0; x <= processes.size(); x++) {
            if(x >= processes.size()){
                for(int y = 0; y < PCB.size(); y++){
                    if(PCB.get(y) <= processes.get(y).getTasks().size()-1){
                        x = x % processes.size();
                    }
                    else if(PCB.get(y) > processes.get(y).getTasks().size() - 1 && PCB.get(y) < 149){
                        PCB.set(y, 150);
                        finishedP++;
                    }
                }
                if(finishedP == PCB.size()){
                    System.out.println("All processes completed.");
                    return;}
            }
            if(processes.get(x).getTasks().size() <= PCB.get(x)){
                continue;
            }
            processes.get(x).setState("RUN");
            System.out.println(processes.get(x).getID() + " " +  "set to run");
            System.out.println();
            if(processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true && critcalTask == null){
                if(processes.get(x).getTasks().get(PCB.get(x)).getWkType().matches("FORK")){
                    System.out.println(processes.get(x).getID() + " " + "forked");
                    System.out.println();
                    processes.add(CProcess.createSingleProcess());
                    PCB.add(0);
                    processes.get(processes.size()-1).setState("READY");
                }
                critcalTask = processes.get(x).getTasks().get(PCB.get(x));
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " " + "set as critcal");
                processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() - buffer);
            }
            else if(critcalTask == processes.get(x).getTasks().get(PCB.get(x))){
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
                    processes.get(processes.size()-1).setState("READY");
                }
                processes.get(x).getTasks().get(PCB.get(x)).setTaskBuffer(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() - buffer);
            }
            if(processes.get(x).getTasks().get(PCB.get(x)).getTaskBuffer() <= 0){
                if(processes.get(x).getTasks().get(PCB.get(x)).getCritical() == true){
                    critcalTask = null;
                }
                System.out.println("Task" + " " + processes.get(x).getTasks().get(PCB.get(x)).getWkType() + " for " + processes.get(x).getID() + " completed");
                if (PCB.get(x) < processes.get(x).getTasks().size()) {
                    PCB.set(x, PCB.get(x) + 1);
                }
                else{
                    System.out.println(processes.get(x).getID() + " completed");
                }
            }

            if(PCB.get(x) < processes.get(x).getTasks().size()){
                processes.get(x).setState("READY");
                System.out.println(processes.get(x).getID() + " " +  "set to READY");
            }
            else if(processes.get(x).getTasks().size() <= PCB.get(x)){
                System.out.println(processes.get(x).getID() + " completed");
                continue;
            }

        }

    }


}
