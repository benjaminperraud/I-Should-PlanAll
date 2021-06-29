package be.ac.ulb.infof307.g04.view.project;

import be.ac.ulb.infof307.g04.util.ToolDate;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public final class SubProjectView {

    private SubProjectView() {}

    /**
     * refreshes the consulted subproject's information
     */
    public static void refreshSubProject(ProjectPanel projectPanel, String title, String description, LocalDate endDate, List<String> labels) {
        projectPanel.getPanel().setText(title);
        VBox vbox = projectPanel.getVbox();
        Label descriptionLabel = (Label) vbox.getChildren().get(0);
        descriptionLabel.setText("Description : " + description);
        Label labelLabels = (Label) vbox.getChildren().get(1);
        labelLabels.setText("Étiquette(s) : " + labels);
        displayEndDate(endDate, (TextFlow) vbox.getChildren().get(2));
    }

    /**
     * Shows end date on the window (in red if the subproject is over)
     */
    public static void displayEndDate(LocalDate endDate, TextFlow endDateTextFlow){
        displayEndDateText(endDate, endDateTextFlow);
    }

    public static void displayEndDateText(LocalDate endDate, TextFlow endDateTextFlow) {
        if (ToolDate.isEarlierThanDate(endDate,LocalDate.now())){
            Text text = new Text(endDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            text.setFill(Color.RED);
            endDateTextFlow.getChildren().setAll(new Text("Échue : " ), text);
        }
        else{
            endDateTextFlow.getChildren().setAll(new Text("Fin : " ), new Text(endDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))));
        }
    }
}
