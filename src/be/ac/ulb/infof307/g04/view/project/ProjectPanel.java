package be.ac.ulb.infof307.g04.view.project;

import be.ac.ulb.infof307.g04.util.ToolDate;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

/**
 * Implementation of class ProjectPanel.
 * This class implements structure to display a Javafx Panel corresponding to a Project
 */
public class ProjectPanel {

    private final TitledPane t1 ;
    private final Button modifyButton;
    private final Button selectButton;
    private final Button statsButton;
    private final VBox vbox;

    /**
     * Sets up a project panel
     *
     */
    public ProjectPanel(String title, String description, LocalDate dueDate, List<String> labels){
        vbox = new VBox();
        t1 = new TitledPane(title, vbox);
        String dateText = dueDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        TextFlow endDateTextFlow = new TextFlow() ;
        if (ToolDate.isEarlierThanDate(LocalDate.now(), dueDate) ){
            endDateTextFlow.getChildren().addAll(new Text("Fin : "), new Text(dateText));
        }
        else{
            Text textDate = new Text(dateText);
            textDate.setFill(Color.RED);
            endDateTextFlow.getChildren().addAll(new Text("Échue : "), textDate);
        }
        Label etiquetteLabel = new Label("Etiquette(s) : " + labels);
        Label descriptionLabel = new Label("Description : " + description);
        modifyButton = new Button("Modifier");
        selectButton = new Button("Sélectionner");
        statsButton = new Button("Statistiques");
        modifyButton.setAccessibleText(title);
        selectButton.setAccessibleText(title);
        statsButton.setAccessibleText(title);
        vbox.getChildren().addAll(etiquetteLabel, descriptionLabel, endDateTextFlow, modifyButton, selectButton, statsButton);
    }

    /**
     * Set the project name on the accessible text of both buttons of the panel
     *
     * @param projectName : name of the project
     */
    public void setText(String projectName){
        modifyButton.setAccessibleText(projectName);
        selectButton.setAccessibleText(projectName);
    }

    public Button getModifyButton(){
        return modifyButton;
    }

    public Button getSelectButton(){return selectButton;}

    public TitledPane getPanel() {
        return t1;
    }

    public VBox getVbox(){
        return vbox;
    }

    public ButtonBase getStatsButton() { return statsButton; }
}
