package be.ac.ulb.infof307.g04.model.exceptions;


/**
 * Class User Exception
 * **/
public class UserExceptions extends Exception{

    /** Static Boolean Variable for the connection Success **/
    private static Boolean success = true;

    /** Static Boolean Variable for to check if User exist into the database **/
    private static Boolean userExistErr;

    /** Static Boolean Variable for to check if Password is correct **/
    private static Boolean passwordErr;

    /** Constructor UserExceptions Throwable Exception **/
    public UserExceptions(Throwable e){
        super(e);
    }


    /** Getters and Setters **/
    public static Boolean getSuccess(){
        return success;
    }
    public static Boolean getPasswordErr(){
        return passwordErr;
    }
    public static Boolean getUserExistErr(){
        return userExistErr;
    }

    public static void setSuccess(Boolean b){
        success = b;
        userExistErr =false;
        passwordErr =false;
    }

    public static void setUserExistErr(Boolean b){
        userExistErr = b;
        success =false;
        passwordErr =false;
    }

    public static void setPasswordErr(Boolean b){
        passwordErr = b;
        userExistErr =false;
        success =false;
    }

}
