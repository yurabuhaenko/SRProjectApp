package denver.srprojectapp;

/**
 * Created by Denver on 11.07.2015.
 */
public class GeneralUser {

    private int id;
    private String name;


    GeneralUser(int id, String name){
        this.id = id;
        this.name = name;
    }

    GeneralUser(GeneralUser gu){
        this.id = gu.getUserId();
        this.name = gu.getUserName();
    }

    public int getUserId(){return id;}

    public String getUserName(){return name;}


}
