package util;

import be.ac.ulb.infof307.g04.util.FilesAndDirectory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FilesAndDirectoryTest {

    @Test
    void testCreateDirectory() {
        File dummyDir = new File("test/dummyDir");
        assertFalse(dummyDir.exists());
        FilesAndDirectory.createDirectory("test/dummyDir");
        assertTrue(dummyDir.exists());
        dummyDir.delete();
    }

    @Test
    void testDeleteDirectory() {
        File dummyDir = new File("test/dummyDir");
        dummyDir.mkdir();
        assertTrue(dummyDir.exists());
        FilesAndDirectory.deleteDirectory(dummyDir);
        assertFalse(dummyDir.exists());
    }

    @Test
    void testCopyFile() {
        try {
            File source = new File("dummy1.txt");
            File destination = new File("dummy2.txt");
            FileWriter writer = new FileWriter(source);
            writer.write("test");
            assertTrue(source.exists() && !destination.exists());
            FilesAndDirectory.copyFile(source, destination);
            assertNotEquals(Files.readAllBytes(source.toPath()), Files.readAllBytes(destination.toPath()));
            source.delete();
            destination.delete();
        } catch (IOException e) {
            System.out.println("Erreur fichier");
        }
    }

}
