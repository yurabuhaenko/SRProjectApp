package denver.srprojectapp.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Denver on 11.07.2015.
 */
public class Project {

    public final static int STATUS_DONE = 1;
    public final static int STATUS_UNDONE = 0;
    public final static String STATUS_UNDONE_WORD = "Not completed";
    public final static String STATUS_DONE_WORD = "Finished";

    private int id;
    private String title;
    private int status;
    private String description;
    private String datetime;
    private int createdByID;

    private List<TaskWithUsersSetted> taskWithUsersSettedList;

    public Project(int id, String title, int status, String description, String datetime, int createdByID){
        this.id = id;
        this.title = title;
        if (status == STATUS_DONE || status == STATUS_UNDONE){
            this.status = status;
        }
        this.description = description;

        this.datetime = datetime;

        this.createdByID = createdByID;

        this.taskWithUsersSettedList = new ArrayList<>();
    }

    public Project(int id, String title, int status, String description, String datetime, int createdByID, List<TaskWithUsersSetted> taskList){
        this.id = id;
        this.title = title;
        if (status == STATUS_DONE || status == STATUS_UNDONE){
            this.status = status;
        }
        this.description = description;

        this.datetime = datetime;

        this.createdByID = createdByID;

        this.taskWithUsersSettedList = new ArrayList<>();

        this.taskWithUsersSettedList = taskList;
    }


    public int getId(){ return id; }
    public int getStatus(){ return status; }
    public String getStatusWord(){
        if (status == 0){
            return STATUS_UNDONE_WORD;
        }else{
            return STATUS_DONE_WORD;
        }
    }
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }

    public Date getDatetimeInDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        Date date = new Date();
        try {
            date = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public void setDateTime(Date datetime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        this.datetime = formatter.format(datetime);
    }


    public boolean isEmptyDateTime(){
        if(getDatetime().equals("0001-01-01 00:00:00")){return true;}
        return false;
    }
    public String getDatetime(){ return datetime; }

    public int getCreatedByID(){return createdByID;}

    public TaskWithUsersSetted getTaskById(int id){ return taskWithUsersSettedList.get(id); }
    public List<TaskWithUsersSetted> getTaskWithUsersSettedList(){ return taskWithUsersSettedList; }


    public void setTitle(String title){ this.title = title; }
    public void setStatus(int status){
        if (status == STATUS_DONE || status == STATUS_UNDONE){
            this.status = status;
        }
    }
    public void setDescription(String description){ this.description = description; }
    public void setDatetime(String dt){
         this.datetime = dt;
    }


    public void setTaskList(List<TaskWithUsersSetted> tl){ taskWithUsersSettedList = tl; }

    public void addTaskToList(TaskWithUsersSetted task){ taskWithUsersSettedList.add(task); }
    public int getNumberOfTasks(){return taskWithUsersSettedList.size();}


}
