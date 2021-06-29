package be.ac.ulb.infof307.g04.model.exceptions;

/**
 * Class DropboxAPI Exception
 * **/
public class DropboxAPIException extends Exception {

    /** Constructor DropboxAPIException **/
    public DropboxAPIException(String s) {
        super(s);
    }

    /** Constructor DropboxAPIException with a message and a cause **/
    public DropboxAPIException(String s, Throwable e) {
        super(s, e);
    }
}
