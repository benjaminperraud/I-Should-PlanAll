package be.ac.ulb.infof307.g04.util.exceptions;

/**
 * Class Compress Exception
 * **/
public class CompressException extends Exception {

    /** Constructor CompressException Throwable Exception**/
    public CompressException(Throwable e) {
        super(e);
    }

    /** Constructor CompressException **/
    public CompressException(String s) {
        super(s);
    }

    /** Constructor CompressException with a message and a cause **/
    public CompressException(String s, Throwable e) {
        super(s, e);
    }
}
