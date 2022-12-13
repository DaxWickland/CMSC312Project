import java.util.ArrayList;

public class NewThread extends Thread{
    private int ID;
    private CProcess process;
    private int PCB;
    private Task Task = null;
    private int x = 0;
    private int y = 0;
    private int q = 0;
    private int completedProcessesA = 0;
    private int completedProcessesB = 0;
    public static ArrayList<Integer> thisPCB1 = new ArrayList<>();
    public static ArrayList<Integer> thisPCB2 = new ArrayList<>();
    public NewThread(){

    }

    public NewThread(int ID, CProcess process){
        this.ID = ID;
        this.process = process;
    }

    public void setID(int newID){this.ID = newID;}

    public void setProcess(CProcess newProcess){this.process = newProcess;}

    public CProcess getProcess(){return this.process;}

    public int getID(){return this.ID;}

    public void setPCB(int newPCB){this.PCB = newPCB;}

    public int getPCB(){return this.PCB;}

    public void setTask(Task newTask){this.Task = newTask;}

    public Task getTask(){return this.Task;}

    @Override
    public void run() {
        if(this.process.getRunType() == 0 && !(this.process.getState().equals("Terminated"))){
        while (this.process.getTasks().size() > this.PCB) {
            Task task = this.process.getTasks().get(this.PCB);
            if(CPUScheduler.checkTable(CPUScheduler.pageTable) == true){
                CPUScheduler.allocatePage(CPUScheduler.pageTable, task, this.process);
            }
            else{
                CPUScheduler.victimSelectPageTable(CPUScheduler.pageTable, task, this.process);
            }
            while (task.getTaskBuffer() > 0) {
                if(task.getWkType() == "FORK"){
                    CProcess newProcess = CProcess.createSingleProcess();
                    newProcess.setRunType(0);
                    System.out.println(this.process.getID() + " " + "forked on thread" + this.getID());
                    System.out.println();
                    if(this.process.getCPU() == 1){
                    CPUScheduler.CPU1ProcessList.add(newProcess);
                    }
                    if(this.process.getCPU() != 1){
                    CPUScheduler.CPU2ProcessList.add(newProcess);
                    }
                }
                if(task.getWkType() == "IO"){
                    System.out.println(this.process.getID() + " waiting for IO");
                    this.process.setState("WAIT");
                    System.out.println(this.process.getID() + " state set to WAIT");
                    task.setTaskBuffer(20);
                }
                task.setTaskBuffer(task.getTaskBuffer() - CPUScheduler.buffer);
            }
            System.out.println("Task" + " " + task.getWkType() + " completed on thread" + this.getID());
                    CPUScheduler.markPage(CPUScheduler.pageTable, task, this.process);
                    System.out.println();
                    this.PCB++;
        }
        this.process.setState("Terminated");
            if(CPUScheduler.SYS_MEM < CPUScheduler.MAX_MEM) {
                CPUScheduler.SYS_MEM += this.process.getMemory();
            }
            else{CPUScheduler.SYS_MEM = CPUScheduler.MAX_MEM;}
            System.out.println();
            System.out.println(this.process.getID() + " " + "terminated");
            System.out.println(this.process.getMemory() + " KB of memory freed");
            System.out.println();
            if(this.process.getCPU() == 1 && CPUScheduler.CPU1ProcessList.size() > 1){
                if(CPUScheduler.CPU1ProcessList.lastIndexOf(this.process) != -1){
                CPUScheduler.CPU1ProcessList.remove(CPUScheduler.CPU1ProcessList.lastIndexOf(this.process));
                }
                this.process = CPUScheduler.CPU1ProcessList.get(0);
                this.process.setRunType(0);
                this.PCB = thisPCB1.get(x);
                this.run();
            }
            else if(this.process.getCPU() != 1 && CPUScheduler.CPU2ProcessList.size() > 1){
                if(CPUScheduler.CPU2ProcessList.lastIndexOf(this.process) != -1){
                CPUScheduler.CPU2ProcessList.remove(CPUScheduler.CPU2ProcessList.lastIndexOf(this.process));
                }
                if(CPUScheduler.CPU1ProcessList.size() - 2 > CPUScheduler.CPU2ProcessList.size()){
                    CPUScheduler.CPU1ProcessList.add(CPUScheduler.CPU2ProcessList.get(CPUScheduler.CPU2ProcessList.size() - 1));
                    CPUScheduler.CPU2ProcessList.remove(CPUScheduler.CPU2ProcessList.get(CPUScheduler.CPU2ProcessList.size() - 1));
                }
                else if(CPUScheduler.CPU1ProcessList.size() - 2 < CPUScheduler.CPU2ProcessList.size()){
                    CPUScheduler.CPU2ProcessList.add(CPUScheduler.CPU1ProcessList.get(CPUScheduler.CPU1ProcessList.size() - 1));
                    CPUScheduler.CPU1ProcessList.remove(CPUScheduler.CPU1ProcessList.get(CPUScheduler.CPU1ProcessList.size() - 1));
                }
                this.process = CPUScheduler.CPU2ProcessList.get(0);
                this.process.setRunType(0);
                this.PCB = thisPCB2.get(y);
                this.run();
            }
            else{
            if(this.process.getCPU() == 1 && CPUScheduler.CPU1ProcessList.size() == 0 ){
                    CPUScheduler.completedThreads1++;
                }
                else if(this.process.getCPU() != 1 && CPUScheduler.CPU2ProcessList.size() == 0){
                    CPUScheduler.completedThreads2++;
                }
                this.interrupt();


            }
        }
        else if(this.process.getRunType() == 2 && !(this.process.getState().equals("Terminated"))){
            System.out.println(this.process.getID() + " is running on thread" + this.getID());
            this.process.getTasks().get(this.PCB).setTaskBuffer(this.process.getTasks().get(this.PCB).getTaskBuffer() - CPUScheduler.buffer);
        }
        else if(this.process.getRunType() == 2 && this.process.getTasks().size() <= this.PCB || this.process.getState().equals("Terminated")){
                this.PCB = 999;
            }
    }
}