package be.ac.ulb.infof307.g04.util.api;

import be.ac.ulb.infof307.g04.model.exceptions.DropboxAPIException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.DbxRequestConfig;
import javafx.scene.control.TextInputDialog;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Vector;
import static be.ac.ulb.infof307.g04.util.FilesAndDirectory.createDirectory;

/**
 * Class based on the official java dropbox's API.
 * from : https://github.com/dropbox/dropbox-sdk-java
 */
public class DropboxAPI {

    /** Private static final constant **/
    private static String ACCESS_TOKEN;
    private static final String TOKEN_PATH = "data/dropbox_token/";

    /** Static instance DbxClientV2 **/
    private static DbxClientV2 client;

    /**
     * Constructor
     * Create Dropbox client, based on the user's token. If no token is registered, then a popup will ask for it.
     * @param username : the username of the user
     */
    public DropboxAPI(String username) throws IOException, DropboxAPIException {
        getToken(username);
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    /**
     * Get the token of the user, registered in the data/dropbox_token/username.dbx.
     * If the token isn't know yet, a dialogue window will ask the user to give it.
     */
    public final void getToken(String username) throws IOException, DropboxAPIException {
        File dropBoxData = new File(TOKEN_PATH + username + ".dbx");
        boolean emptyFile = !dropBoxData.exists();
        if (!emptyFile) {
            Scanner scanner = new Scanner(dropBoxData);
            emptyFile = !scanner.hasNext();
            if (!emptyFile) {
                ACCESS_TOKEN = scanner.nextLine(); //Si le token existe déjà
            }
        }
        if (emptyFile) {
            TextInputDialog tokenInputDialog = new TextInputDialog(); //Si le token n'existe pas encore
            tokenInputDialog.setTitle("Token");
            tokenInputDialog.setHeaderText("Veuillez copier votre token ici");
            Optional<String> userToken = tokenInputDialog.showAndWait();

            if (!userToken.isPresent()) { // Clique sur "Annuler"
                throw new DropboxAPIException("Vous n'avez inséré aucun token pour accéder à Dropbox !");
            } else {
                ACCESS_TOKEN = userToken.get();
                if(isTokenValid()){
                    saveInfo(username);
                }
            }
        }
    }

    /**
     * Checks if token is valid through a simple call to the API
     * @return true if the token is valid, otherwise throw a DropboxAPIException
     * @throws DropboxAPIException to report an error occurred while the API call is being executed
     */
    private boolean isTokenValid() throws DropboxAPIException {
        try {
            DbxRequestConfig tempConfig = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 tempClient = new DbxClientV2(tempConfig, ACCESS_TOKEN);
            tempClient.users().getCurrentAccount();
        } catch (DbxException e) {
            throw new DropboxAPIException("Le token entré n'est pas valide !", e);
        }
        return true;
    }

    /**
     * This method save the token given by the user into the user's file for token.
     * @param username : the user name
     */
    public final void saveInfo(String username) throws IOException {
        createDirectory(TOKEN_PATH);
        File dropBoxData = new File(TOKEN_PATH + username + ".dbx"); //create the file
        FileWriter writer = new FileWriter(dropBoxData);
        writer.write(ACCESS_TOKEN); //write the token
        writer.close();
    }

    /** Original method from Dropbox API.
     * Upload the file on Dropbox */
    public void uploadFile(String filename, String fileNameOnCloud) throws DropboxAPIException {
        try (InputStream in = new FileInputStream(filename)) {
            client.files().uploadBuilder(fileNameOnCloud).uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            throw new DropboxAPIException("Une erreur est survenue lors de l'exportation du fichier sur Dropbox.", e);
        }
    }

    /**Original method from Dropbox API.
     * Is used to get the list of the files in the given repertory from the client's dropbox account*/
    public List<String> getFiles(String directoryName) throws DbxException {
        List<String> fileNamesList = new Vector<>();
        ListFolderResult result = client.files().listFolder(directoryName);
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                fileNamesList.add(metadata.getPathLower());
            }
            if (!result.getHasMore()) {
                break;
            }
            result = client.files().listFolderContinue(result.getCursor());
        }
        return fileNamesList;
    }

    /**Original method from Dropbox API.
     * Is used to download the file with the given name from the client's dropbox account*/
    public void downloadFile(String fileName, String fileNameOutput)throws DbxException, IOException {
        OutputStream outputStream = new FileOutputStream(fileNameOutput);
        client.files().downloadBuilder(fileName).download(outputStream);
        outputStream.close();
    }

}