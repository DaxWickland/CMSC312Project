
public class Task {
    private int taskBuffer;
    private String wkType;
    private boolean critical;

    public Task(){
        this.taskBuffer = 0;
        this.wkType = null;
    }

    public Task(int buffer, String work){
        this.taskBuffer = buffer;
        this.wkType = work;
    }

    public int getTaskBuffer(){return this.taskBuffer;}

    public String getWkType(){return this.wkType;}

    public void setTaskBuffer(int x){this.taskBuffer = x;}

    public void setWkType(String work){this.wkType = work;}

    public void setCritical(boolean critical){this.critical = critical;}

    public boolean getCritical(){return this.critical;}


}
