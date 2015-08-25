package denver.srprojectapp.objects;

/**
 * Created by Denver on 11.07.2015.
 */
public class ProjectTask extends Task{


    private String projectTitle;


    public ProjectTask(int id, String text, int status, int createdById, int projectId, String projectTitle){
        super(id, text, status, createdById, projectId);
        this.projectTitle = projectTitle;
    }

    public String getProjectTitle(){return projectTitle;}

}
