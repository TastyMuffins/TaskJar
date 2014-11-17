package pw.monkeys.paul.taskjar;

/**
 * Created by Paul on 8/14/2014.
 */
public class ProjectItem{
    private String id;
    private String name;
    private String hours;
    private String hoursComplete;
    private String creator;

    public ProjectItem(String id,String name,String creator,String hours,String hoursComplete) {
        this.id = id;
        this.name = name;
        this.hours = hours;
        this.creator = creator;
        this.hoursComplete = hoursComplete;
    }
    public String getHoursComplete() {
        return hoursComplete;
    }
    public void setHoursComplete(String hoursComplete) {
        this.hoursComplete = hoursComplete;
    }
    public String getHours() {
        return hours;
    }
    public void setHours(String hours) {
        this.hours = hours;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCreator(String creator){this.creator = creator;}
    public String getCreator(){return creator;}
    public void setId(String id){this.id = id;}
    public String getId(){return id;}
    @Override
    public String toString() {
        return name;
    }
}
