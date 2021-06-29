package be.ac.ulb.infof307.g04.util.exceptions;

/**
 * Class Extract Exception
 * **/
public class ExtractException extends Exception {

    /** Constructor ExtractException Throwable Exception**/
    public ExtractException(Throwable e) {
        super(e);
    }

    /** Constructor ExtractException **/
    public ExtractException(String s) {
        super(s);
    }

    /** Constructor ExtractException with a message and a cause **/
    public ExtractException(String s, Throwable e) {
        super(s, e);
    }

}
