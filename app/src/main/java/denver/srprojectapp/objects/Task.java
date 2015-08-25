package denver.srprojectapp.objects;

import android.content.Intent;

/**
 * Created by Denver on 11.07.2015.
 */
public class Task {

    public final static int STATUS_DONE = 1;
    public final static int STATUS_UNDONE = 0;


    private int id;
    private String text;
    private int status;
    private int createdById;
    private int projectId;

    public Task(int id, String text, int status, int createdById, int projectId){
        this.id = id;
        this.text = text;
        if (status == STATUS_DONE || status == STATUS_UNDONE){
            this.status = status;
        }
        this.createdById = createdById;
        this.projectId = projectId;
    }

    public int getId(){return id;}
    public String getText(){return text;}
    public int getStatus(){return status;}
    public int getCreatedById(){return createdById;}
    public int getProjectId(){return projectId;}

    public void setId(int id){this.id= id;}
    public void setText(String text){this.text= text;}
    public void setStatus(int status){
        if (status == STATUS_DONE || status == STATUS_UNDONE){
            this.status = status;
         }
    }

}
