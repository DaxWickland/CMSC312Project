import java.util.ArrayList;
import java.util.Random;

public class CProcess {
    /**************************************
     * Creation of the  private
     instance variables
     */
    private String processID;
    private ArrayList<Task> tasks;
    private String state;
    /***********************************
     * Creation of the default and
     * parameterized objects
     */
    public CProcess(){
        this.processID = null;
        this.tasks = new ArrayList<>();
        this.state = "new";
    }

    public CProcess(String x){
        setID(x);
    }

    public void setID(String ID){
        this.processID = ID;
    }

    public void addTask(Task task){this.tasks.add(task);}

    public void removeTask(Task task){this.tasks.remove(task);}

    public String getID(){
        return this.processID;
    }

    public void setState(String newState){this.state = newState;}

    public String getState(){return this.state;}

    public ArrayList<Task> getTasks(){return this.tasks;}

    public int totProcesses = 0;

    public void getAllTasks() {
        for (int x = 0; x < this.tasks.size(); x++) {
            System.out.println(this.tasks.get(x).getWkType() + " " + this.tasks.get(x).getTaskBuffer() + " " + this.tasks.get(x).getCritical());
        }
    }

    public static ArrayList<CProcess> createProcessList(int y) {
        ArrayList<CProcess> processList = new ArrayList<>();
        for (int w = 0; w < y; w++) {
            CPUScheduler.PCB.add(w, 0);
            processList.add(w, createSingleProcess());
        }
        return processList;
    }
    public static CProcess createSingleProcess(){
        CProcess newProcess = new CProcess();
        int randID = (int) (Math.random() * 10000);
        newProcess.setState("NEW");
        newProcess.setID("Process" + randID);
        Random rand = new Random();
        int randTasks = (int) (Math.floor(Math.random() * 15) + 1);

        for (int x = 0; x < randTasks; x++) {
            Task newTask = new Task();
            newProcess.addTask(newTask);
            int workChoice = rand.nextInt(10);

            if (workChoice <= 5) {
                newTask.setWkType("CALCULATE");
            } else if (workChoice == 6 || workChoice == 7 || workChoice == 8) {
                newTask.setWkType("IO");
            } else {
                newTask.setWkType("FORK");
                newTask.setTaskBuffer(1);
                break;
            }

            newTask.setTaskBuffer((int) (Math.floor(Math.random() * 80) + 1));
            if (Math.random() * 10 > 7 || x == 0) {
                newTask.setCritical(true);

            }
        }

        return newProcess;
    }


}
