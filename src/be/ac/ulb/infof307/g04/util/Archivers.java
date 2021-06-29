package be.ac.ulb.infof307.g04.util;

import be.ac.ulb.infof307.g04.util.exceptions.CompressException;
import be.ac.ulb.infof307.g04.util.exceptions.ExtractException;
import org.rauschig.jarchivelib.*;
import java.io.*;

/**
 *
 * Class final Archivers
 *
 * **/
public final class Archivers {

    /** default Constructor Archivers **/
    private Archivers() {}

    /** Create a tar.gz file of a source repertory to a destination repertory
     *
     * @param projectName : the Name of the Project
     * @param source : the file source to compress
     * @param destination : the file destination where we store the compress
     * @throws CompressException to report an error occurred while compressing the file
     * */
    public static void compress(String projectName, File source, File destination) throws CompressException {
        try {
            Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            archiver.create(projectName, destination, source);
        } catch (IOException e) {
            throw new CompressException("Veuillez vérifier d'avoir bien choisi une destination ou relancer l'application", e);
        }
    }

    /** Extract a source tar.gz file to a destination repertory
     *
     * @param source : the file source to extract
     * @param destination : the file destination where we extract the compress file
     * @throws ExtractException to report an error occurred while extracting the file
     * */
    public static void extract(File source, File destination) throws ExtractException {
        try {
            Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            archiver.extract(source, destination);
            // Add project extract to listProject of the User
        } catch (IOException e) {
            throw new ExtractException("Veuillez vérifier d'avoir bien choisi un fichier tar.gz ou relancer l'application", e);
        }
    }
}
