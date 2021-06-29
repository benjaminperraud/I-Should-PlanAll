package be.ac.ulb.infof307.g04.util;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.regex.Pattern;

/**
 * Utility class for performing operations on text
 */
public final class TextFormat {

    /* Attributes */
    private static final String COMPULSORY_FIELD = "Ce champ est obligatoire.";

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;

    /* Constructors */
    private TextFormat() {
    }

    /**
     * Deletes extra space at the end of a string
     * @param text : the text on which the deletion in made
     * @return the string without spaces at the end
     */
    public static String deleteSpace(String text) {
        return text.replaceAll("\\s+$", "");
    }

    /**
     * Checks if data are valid
     * @param fieldArray : an array containing the fields
     * @param errorArray : an array containing the errors
     * @return true if data are valid, if not return false
     */
    public static boolean isInputValid(TextField[] fieldArray, Text[] errorArray) {
        Pattern mailPattern = Pattern.compile("^[A-Za-z0-9._\\-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,3}$");
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"); // password must contain at least: one digit, one lowercase letter and one uppercase letter and must be at least 8 characters long
        boolean validInput = true;
        boolean password = false;

        if (!fieldArray[ZERO].getText().chars().allMatch(Character::isLetter)) { // First name
            validInput = false;
            errorArray[ZERO].setText("Ce prénom n'est pas valide");
        } else if (fieldArray[ZERO].getText() == null || fieldArray[ZERO].getText().length() == 0) {
            validInput = false;
            errorArray[ZERO].setText(COMPULSORY_FIELD);
        } else {
            errorArray[ZERO].setText("");
        }
        if (!fieldArray[ONE].getText().chars().allMatch(Character::isLetter)) { // Last Name
            validInput = false;
            errorArray[ONE].setText("Nom de famille invalide");
        } else {
            errorArray[ONE].setText("");
        }
        if (fieldArray[TWO].getText() == null || fieldArray[TWO].getText().length() == 0) { // mail
            validInput = false;
            errorArray[TWO].setText(COMPULSORY_FIELD);
        } else if (!mailPattern.matcher(fieldArray[TWO].getText()).matches()) {
            validInput = false;
            errorArray[TWO].setText("Cette adresse email n'est pas valide");
        } else {
            errorArray[TWO].setText("");
        }
        if (!fieldArray[THREE].getText().chars().allMatch(Character::isLetter)) { // username
            validInput = false;
            errorArray[THREE].setText("Ce nom d'utilisateur n'est pas valide");
        } else if (fieldArray[THREE].getText() == null || fieldArray[THREE].getText().length() == 0) {
            validInput = false;
            errorArray[THREE].setText(COMPULSORY_FIELD);
        } else {
            errorArray[THREE].setText("");
        }
        if (fieldArray[FOUR].getText() == null || fieldArray[FOUR].getText().length() == 0) { // password
            validInput = false;
            password = false;
            errorArray[FOUR].setText(COMPULSORY_FIELD);
        } else if (!passwordPattern.matcher(fieldArray[FOUR].getText()).matches()) {
            validInput = false;
            password = true;
            errorArray[FOUR].setText("Mot de passe invalide: minimum 1 minuscule, majuscule, chiffre et 8 caractères");
        } else {
            errorArray[FOUR].setText("");
        }
        if (fieldArray[FIVE].getText() == null || fieldArray[FIVE].getText().length() == 0 & password) { // password confirmation
            validInput = false;
            errorArray[FIVE].setText("Veuillez confirmer le mot de passe.");
        } else if (!fieldArray[FIVE].getText().equals(fieldArray[FOUR].getText())) {
            validInput = false;
            errorArray[FIVE].setText("La vérification et le mot de passe sont différents.");
        } else {
            errorArray[FIVE].setText("");
        }
        return validInput;
    }
}
