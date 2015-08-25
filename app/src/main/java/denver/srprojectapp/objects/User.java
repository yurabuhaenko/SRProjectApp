package denver.srprojectapp.objects;

import denver.srprojectapp.service.UserRights;

/**
 * Created by Denver on 11.07.2015.
 */
public class User extends GeneralUser{

   // private int id;
    private String email;
    private String apiKey;
   // private String name;
    private UserRights rights;

    public User(int id, String email, String apiKey, String name, String rights){
        super(id,name);
       // this.id = id;
        this.email = email;
        this.apiKey = apiKey;
      //  this.name = name;
        this.rights = new UserRights(rights);
    }


   // public int getUserId(){return id;}

    public String getUserEmail(){return email;}

    public String getUserApiKey(){return apiKey;}

    //public String getUserName(){return name;}

    public String getUserRights(){return rights.getRights(); }



}
