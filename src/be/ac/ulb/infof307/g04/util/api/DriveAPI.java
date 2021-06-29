package be.ac.ulb.infof307.g04.util.api;

import be.ac.ulb.infof307.g04.model.exceptions.DriveAPIException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;

import static be.ac.ulb.infof307.g04.util.FilesAndDirectory.createDirectory;


/** Google API code for Drive operations
 *
 * The code mainly come from the official Google Drive API's doc : https://developers.google.com/drive/api/v3/quickstart/java
 */

public final class DriveAPI {

    private DriveAPI() {}

    /** Private static final constant **/
    private static final int PORT_NUMBER = 8888;
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final String TOKENS_DIRECTORY_PATH = "data/tokens/";
    private static final String APP_CREDENTIALS = "data/credentials/credentials.json";

    /** Instance JsonFactory initialized to Default **/
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Instance List<String> initialized to singleton List **/
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);


    /** Creates an authorized Credential.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws DriveAPIException reports an exception occurred during initialization of user credentials
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport, String username) throws DriveAPIException {
        // Load client identifiers
        String userTokenPath = TOKENS_DIRECTORY_PATH + username + "/";
        createDirectory(userTokenPath);
        Credential c;

        try {
            InputStream in = new FileInputStream(APP_CREDENTIALS);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(userTokenPath)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(PORT_NUMBER).build();
            c = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (FileNotFoundException e) {
            throw new DriveAPIException("Vos identifiants Google Drive n'ont pas été trouvés.", e);
        } catch (IOException e) {
            throw new DriveAPIException("Une erreur est survenue en initialisant la communication avec le système de stockage distant.", e);
        }
        return c;
    }

    /** Create Drive object that will be used to execute operations with remote directory. When an operation uses
     * a 'Drive' object, it has been created with this method.
     *
     * @param userName : name of user who started communication with cloud
     * @throws DriveAPIException to report exception occurred while running Drive API methods */
    public static Drive startService(String userName) throws DriveAPIException {
        // Build a new authorized API client service.
        Drive service;
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            service = new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport, userName))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            throw new DriveAPIException("Une erreur est survenue en initialisant la communication avec le système de stockage distant.", e);
        }
        return service;
    }

    /** Request all files stored in user directory on his Drive
     *
     * @param service : cf startService
     * @param userDirID : numerical identifier of user directory on the drive
     * @throws DriveAPIException to report exception occurred while running Drive API methods */
    public static List<String> getListFiles(Drive service, String userDirID) throws DriveAPIException {
        List<String> availableFiles = new LinkedList<>();
        try {
            for (File f : service.files().list().setFields("files(id, name, parents, mimeType)").execute().getFiles()) {
                if (f.getMimeType().equals("application/gzip") && f.getParents().contains(userDirID)) {
                    availableFiles.add(f.getName().split("\\.")[0]);
                }
            }
        } catch (IOException e) {
            throw new DriveAPIException("Une erreur est survenue lors de la communication avec le système de stockage distant.", e);
        }
        return availableFiles;
    }

    /** Make a communication with Drive to get numerical ID of file knowing its name. This method is used in order
     * to call downloadFile which requires to know this ID.
     *
     * @param service : cf startService
     * @param userDirID : numerical identifier of user directory on Drive
     * @param fileName : name of file the ID will be searched
     * @throws DriveAPIException to report exception occurred while running Drive API methods */
    public static String getIDFromName(Drive service, String userDirID, String fileName) throws DriveAPIException {
        String res  = "";
        try {
            for (File f : service.files().list().setFields("files(id, name, parents, mimeType)").execute().getFiles()) {
                // IntelliJ suggestion for below line must NOT be taken into account because it will fail if f.getParents() is null
                boolean parentCondition = f.getParents() != null && f.getParents().contains(userDirID);
                if (f.getMimeType().equals("application/gzip") && f.getName().equals(fileName + ".tar.gz") && parentCondition) {
                    res = f.getId();
                }
            }
        } catch (IOException e) {
            throw new DriveAPIException("Une erreur est survenue lors de la communication avec le système de stockage distant.", e);
        }
        return res;
    }

    /** Downloads file from user's Drive
     *
     * @param service : cf startService
     * @param fileID digital code of remote file to download
     * @param destination path of file where downloaded file will be stored
     * @throws DriveAPIException to report an error during download */
    public static void downloadFile(Drive service, String fileID, String destination) throws DriveAPIException {
        try {
            OutputStream destinationStream = new FileOutputStream(destination);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            service.files().get(fileID).executeMediaAndDownloadTo(outputStream);
            outputStream.writeTo(destinationStream);
        } catch (IOException e) {
            throw new DriveAPIException("Une erreur est survenue lors du téléchargement du fichier.", e);
        }
    }

    /** Verifies if a directory for current user already exists in cloud. If no directory is found,
     * create one. By default consider username as name of directory
     *
     * @param userName : name of user, used for directory name
     * @return id of file (directory) that will be used to upload & download files
     * @throws DriveAPIException to report an error during directory management */
    public static String manageRemoteUserDirectory(Drive service, String userName) throws DriveAPIException {
        String pageToken = null;
        File userDir = null;
        // Search if directory with username already exists
        do {
            FileList result;
            try {
                result = service.files().list()
                        .setQ("mimeType='application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                throw new DriveAPIException("Une erreur est survenue lors de la recherche du dossier utilisateur sur le cloud.", e);
            }
            for (File file : result.getFiles()) {
                if (file.getName().equals(userName)) {
                    userDir = file;
                }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        // no directory found, create one
        if (userDir == null) {
            File fileMetadata = new File();
            fileMetadata.setName(userName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            try {
                userDir = service.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
            } catch (IOException e) {
                throw new DriveAPIException("Une erreur est survenue lors de la création d'un dossier utilisateur sur le cloud.", e);
            }
        }
        return userDir.getId();
    }


    /**
     * Method used to upload a file on Drive's user. The MIME type of the file must be specified.
     *
     * @param driveService : cf startService
     * @param fileName : name of file that will be given to exported project
     * @param filePath : path of file to be upload
     * @param userDirID : numeric code representing user ID on Google Drive
     * @throws DriveAPIException to report the occurrence of an exception
     */
    public static void uploadFile(Drive driveService, String fileName, String filePath, String userDirID) throws DriveAPIException {
        String mimeTypeTargz = "application/gzip";
        try {
            File fileMetadata = new File();
            fileMetadata.setName(fileName + ".tar.gz");
            fileMetadata.setParents(Collections.singletonList(userDirID));
            java.io.File filePathDrive = new java.io.File(filePath);
            FileContent mediaContent = new FileContent(mimeTypeTargz, filePathDrive);
            driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } catch (IOException e) {
            throw new DriveAPIException("Une erreur est survenue lors de l'exportation du fichier sur le cloud.", e);
        }
    }

}