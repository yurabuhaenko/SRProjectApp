package denver.srprojectapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denver on 20.07.2015.
 */
public class TaskWithUsersSetted{
    Task task;
    List<GeneralUser> listUsers;

    TaskWithUsersSetted(){
        task.setText("");
        task.setStatus(0);
        listUsers = new ArrayList<>();
    }

    public String getTaskText(){return task.getText();}

    public Task getTask(){return task;}
    public List<GeneralUser> getListUsers(){return listUsers;}



    public void setIdTask(int id){this.task.setId(id);}
    public void setTaskText(String taskText){this.task.setText(taskText); }
    public void setListUsers(List<GeneralUser> list){this.listUsers = list;}

}
