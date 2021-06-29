package be.ac.ulb.infof307.g04.model;

import be.ac.ulb.infof307.g04.controller.Main;
import be.ac.ulb.infof307.g04.model.exceptions.AlertExceptions;
import be.ac.ulb.infof307.g04.model.exceptions.UserExceptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Map;


/** Class Database Model
 *
 *
 * **/
public class Database {

    /** Instance final User **/
    private final User user;

    /** String Static final Path of Database User directory **/
    public static final String USER_DIRECTORY = "data/users/" ;

    /** String Static final Constant **/
    private static final String JSON = ".json";
    private static final String USER_FILE_NOT_FOUND = "Le fichier de l'utilisateur n'a pas été trouvé";
    private static final String FILE_NOT_FOUND = "Fichier non trouvé";
    private static final String WRITE_ERROR = "Erreur lors de l'écriture du fichier";


    /** Constructor Database **/
    public Database(User user) {
        this.user = user;
    }

    /**
     * Get the Instance attribut of Object User
     *
     * @return the Object User
     */
    public User getUser(){return user;}

    /**
     * Create instance gson Builder
     *
     * @return a Gson Builder
     */
    public static Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Get Instance User from the Json Database
     *
     * @param userName : the username of the searched user
     * @return a User matching the username
     *
     */
    public static User getUserFromDatabase(String userName) throws UserExceptions {
        Gson gson = createGson();
        File file = new File(USER_DIRECTORY + userName + JSON);
        try (FileReader reader = new FileReader(file)){
            User myUser =  gson.fromJson(reader, User.class);
            return myUser;
        } catch (FileNotFoundException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(FILE_NOT_FOUND, USER_FILE_NOT_FOUND);
            throw new UserExceptions(e);
        } catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(WRITE_ERROR, "Le fichier n'a pas pu être modifié");
            throw new UserExceptions(e);
        }
    }


    /**
     * Update Json User Information from EditProfileView
     *
     * @param name : the name of the searched user
     * @param userName : the username of the searched user
     * @param lastName : the lastname of the searched user
     * @param email : the email of the searched user
     * @param password : the password of the searched user
     * @param listProject : the Project list of the searched user
     *
     */
    public void editUserInformations(String name, String userName, String lastName, String email, String password, Map<String, Project> listProject) throws UserExceptions{
        String oldUsername = user.getUsername();
        user.set(name, userName, lastName, email, password, listProject);
        Gson gson = createGson();
        try (FileWriter fileWriter = new FileWriter(USER_DIRECTORY + userName + JSON)){
            fileWriter.write(gson.toJson(user));
            if (!oldUsername.equals(user.getUsername())) {
                File obsoleteUserFile = new File(USER_DIRECTORY + oldUsername + JSON);
                obsoleteUserFile.delete();
            }
            UserExceptions.setSuccess(true);
        }
        catch (FileNotFoundException e){
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(FILE_NOT_FOUND, USER_FILE_NOT_FOUND);
            throw new UserExceptions(e);
        } catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(WRITE_ERROR, "Le fichier n'a pas pu être modifié");
            throw new UserExceptions(e);
        }
    }

    /**
     * Register the User on the Welcome Screen Application
     *
     * @param firstName : the name of the register user
     * @param userName : the username of the register user
     * @param lastName : the lastname of the register user
     * @param mail : the email of the register user
     * @param password : the password of the register user
     *
     */
    public static void register(String firstName, String userName, String lastName, String mail, String password) throws UserExceptions{
        try {
            Gson gson = createGson();
            File userFile = new File(USER_DIRECTORY + userName + JSON);
            if (!userFile.exists() || userFile.isDirectory()) {
                try(FileWriter fileWriter = new FileWriter(userFile)) {
                    User user = new User(firstName, userName, lastName, mail, password);
                    fileWriter.write(gson.toJson(user));
                    UserExceptions.setSuccess(true);
                }
            } else {
                UserExceptions.setUserExistErr(true);
            }
        }
        catch (FileNotFoundException e){
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(FILE_NOT_FOUND, USER_FILE_NOT_FOUND);
            throw new UserExceptions(e);
        } catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(WRITE_ERROR, "Le fichier n'a pas pu être créée");
            throw new UserExceptions(e);
        }
    }


    /**
     * Check if Object User exist in database, if it does log him in
     *
     * @param userName : user name
     * @param password : user password
     * */
    public void login(String userName, String password) throws UserExceptions{
        try {
            Gson gson = createGson();
            File userFile = new File(USER_DIRECTORY + userName + JSON);

            if (userFile.exists() && !userFile.isDirectory()) {
                try(FileReader read = new FileReader(userFile)){
                    User user = gson.fromJson(read, User.class);
                    if (password.equals(user.getPassword())) {
                        user.set(user.getName(), user.getUsername(), user.getUsername(), user.getEmail(), user.getPassword(), user.getListProject());
                        Main.setConnectedUser(user);
                        UserExceptions.setSuccess(true);
                    } else {
                        UserExceptions.setPasswordErr(true);
                    }
                }
            }else {
                UserExceptions.setUserExistErr(true);
            }
        }
        catch (FileNotFoundException e){
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(FILE_NOT_FOUND, USER_FILE_NOT_FOUND);
            throw new UserExceptions(e);
        } catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert("Erreur lors de la connexion", "La connexion n'a pas pu être effectuée");
            throw new UserExceptions(e);
        }
    }


    /**
     * Update the current Object User into the Json database
     *
     * */
    public void updateDetails() throws UserExceptions {
        String oldUsername = this.getUser().getUsername();
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try(FileWriter fileWriter = new FileWriter(USER_DIRECTORY + this.getUser().getUsername() + JSON)) {
                fileWriter.write(gson.toJson(this.getUser()));
                if (!oldUsername.equals(this.getUser().getUsername())) {
                    File obsoleteUserFile = new File(USER_DIRECTORY + oldUsername + JSON);
                    obsoleteUserFile.delete();
                }
            }
        }
        catch (FileNotFoundException e){
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(FILE_NOT_FOUND, USER_FILE_NOT_FOUND);
            throw new UserExceptions(e);
        } catch (IOException e) {
            AlertExceptions alert = new AlertExceptions();
            alert.throwAlert(WRITE_ERROR, "Le fichier n'a pas pu être modifié");
            throw new UserExceptions(e);
        }
    }


    /**
     * Return true if User Exist in the Database
     * Check if user exist in database
     *
     * @param userName : the user name
     * @return : true if user exist in database, false otherwise
     */
    public static boolean userExists(String userName) {
        File userFile = new File(USER_DIRECTORY + userName + JSON);
        return (userFile.exists() && !userFile.isDirectory());
    }

}
