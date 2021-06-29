package be.ac.ulb.infof307.g04.model.exceptions;


/**
 * Class DriveAPI Exception
 * **/
public class DriveAPIException extends Exception {

    /** Constructor DriveAPIException **/
    public DriveAPIException(String s) {
        super(s);
    }

    /** Constructor DriveAPIException with a message and a cause **/
    public DriveAPIException(String s, Throwable e) {
        super(s, e);
    }
}