package pw.monkeys.paul.taskjar;

/**
 * Created by Paul on 8/14/2014.
 */
public class TaskItem{
    private String id;
    private String name;
    private String hours;
    private String hoursComplete;
    private String assigned;
    private String description;
    private String creator;

    public TaskItem(String id,String name, String description,String creator,String assigned,String hours,String hoursComplete) {
        this.id = id;
        this.name = name;
        this.hours = hours;
        this.description = description;
        this.creator = creator;
        this.assigned = assigned;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCreator(String creator){this.creator = creator;}
    public String getCreator(){return creator;}
    public void setAssigned(String creator){this.assigned = assigned;}
    public String getAssigned(){return assigned;}
    public void setId(String id){this.id = id;}
    public String getId(){return id;}
    @Override
    public String toString() {
        return name;
    }
}
