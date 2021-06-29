package be.ac.ulb.infof307.g04.view.task;

import be.ac.ulb.infof307.g04.util.ToolDate;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Set;

/**
 * Implementation of class ProjectPanel.
 * This class implements structure to display a Javafx Panel corresponding to a Project
 */
public class TaskPanel {

    private final TitledPane t1 ;
    private final Button modifyButton;
    private final VBox vbox;

    /**
     * Sets up a project panel
     *
     */
    public TaskPanel(String title, String description, LocalDate startDate, LocalDate endDate, Set<String> collaborator){
        modifyButton = new Button("Modifier");
        modifyButton.setAccessibleText(title);
        vbox = new VBox();
        t1 = new TitledPane(collaborator.contains(null) | collaborator.isEmpty() ?  title : "<" + title + "> attribuée à " + collaborator, vbox);
        String dateText = endDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        TextFlow endDateTextFlow = new TextFlow() ;
        if (ToolDate.isEarlierThanDate(LocalDate.now(), endDate) ){
            Text textDate = new Text(dateText);
            endDateTextFlow.getChildren().addAll(new Text("Fin : "), textDate);
        }
        else{
            Text textDate = new Text(dateText);
            textDate.setFill(Color.RED);
            endDateTextFlow.getChildren().addAll(new Text("Échue : "), textDate);
        }
        Label descriptionLabel = new Label("Description : " + description);
        Label startLabel = new Label("Début : " + startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        vbox.getChildren().addAll(descriptionLabel, startLabel, endDateTextFlow, modifyButton);
    }

    /**
     * Set the project name on the accessible text of both buttons of the panel
     *
     * @param taskName : name of the task
     */
    public void setText(String taskName){
        modifyButton.setAccessibleText(taskName);
    }

    public Button getModifyButton(){
        return modifyButton;
    }

    public TitledPane getPanel() {
        return t1;
    }

    public VBox getVbox(){
        return vbox;
    }
}
