import java.util.ArrayList;

public class Task {
    private int taskBuffer;
    private String wkType;
    private boolean critical;
    private int pageSize;

    public Task(){
        this.taskBuffer = 0;
        this.wkType = null;
        this.pageSize = 0;
    }

    public Task(int buffer, String work){
        this.taskBuffer = buffer;
        this.wkType = work;
    }

    public int getTaskBuffer(){return this.taskBuffer;}

    public String getWkType(){return this.wkType;}

    public void setTaskBuffer(int x){this.taskBuffer = x;}

    public void setPageSize(int x){this.pageSize = x;}

    public int getPageSize(){return this.pageSize;}

    public void setWkType(String work){this.wkType = work;}

    public void setCritical(boolean critical){this.critical = critical;}

    public boolean getCritical(){return this.critical;}


}
