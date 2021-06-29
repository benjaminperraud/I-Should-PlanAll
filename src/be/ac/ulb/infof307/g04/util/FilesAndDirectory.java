package be.ac.ulb.infof307.g04.util;

import be.ac.ulb.infof307.g04.model.exceptions.AlertExceptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for performing operations on files and directories
 */
public final class FilesAndDirectory {

    /* Constructor */
    private FilesAndDirectory() {
    }

    /**
     * Deletes directory and all the files it contains
     * @param path : the path of the directory to be deleted
     */
    public static void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        path.delete();
    }

    /**
     * Creates a new directory source to save temporary the json project
     * @param directory : the name of the directory to be created
     * @return a File which is the created directory
     */
    public static File createDirectory(String directory) {
        try {
            Files.createDirectories(Paths.get(directory));
            return new File(directory);
        } catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Erreur lors de la création du dossier", "Veuillez vérifier d'avoir bien choisi une destination ou relancer l'application");
            return null;
        }
    }

    /**
     * Copy file from a location to another
     * @param sourceFile : the source file location
     * @param destFile : the destination file location
     */
    public static void copyFile(File sourceFile, File destFile)  {
         try {
             FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile).getChannel();
             if (source != null) {
                 destination.transferFrom(source, 0, source.size());
                 source.close();
             }
             destination.close();
         }
         catch (IOException e){
             AlertExceptions alert = new AlertExceptions();
             alert.throwAlert("Erreur lors de la copie du fichier", "Veuillez relancer l'application");
         }
    }

    /**
     * Checks if a file is an archive .targz
     * @param name : the name of the file
     * @return true if the file is an archive .targz
     */
    public static boolean isFileTargz(String name) {
        return name.endsWith(".tar.gz");
    }
}
