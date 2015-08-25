package denver.srprojectapp.service;

/**
 * Created by Denver on 11.07.2015.
 */
public class UserRights {

    public static String FULL_RIGHTS = "full";
    public static String BASE_RIGHTS = "base";
    public static String VIEW_RIGHTS = "view";

    private final String rights;

    public UserRights(String rights){
        if(rights.equals(FULL_RIGHTS) || rights.equals(BASE_RIGHTS) || rights.equals(VIEW_RIGHTS) ){
            this.rights = rights;
        }else{
            this.rights = VIEW_RIGHTS;
        }
    }

    public static boolean isValidStrRights(String rights){
        if(rights.equals(FULL_RIGHTS) || rights.equals(BASE_RIGHTS) || rights.equals(VIEW_RIGHTS) ){
            return true;
        }
        return false;
    }

    public String getRights(){return rights;}

}
