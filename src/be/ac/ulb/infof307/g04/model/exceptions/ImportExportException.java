package be.ac.ulb.infof307.g04.model.exceptions;


/**
 * Class ImportExport Exception
 * **/
public class ImportExportException extends Exception {

    /** Constructor ImportExportException **/
    public ImportExportException(String s) {
        super(s);
    }

    /** Constructor ImportExportException with a message and a cause **/
    public ImportExportException(String s, Throwable e) {
        super(s, e);
    }
}
