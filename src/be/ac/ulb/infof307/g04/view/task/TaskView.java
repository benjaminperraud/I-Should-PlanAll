package be.ac.ulb.infof307.g04.view.task;

import be.ac.ulb.infof307.g04.view.project.SubProjectView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Set;

public final class TaskView {

    private TaskView() {}

    /**
     * Refresh a task after it was modified
     * */
    public static void refreshTask(TaskPanel taskPanel, String title, String description, LocalDate startDate, LocalDate endDate, Set<String> collaborator) {
        VBox vbox = taskPanel.getVbox();
        Label descriptionLabel = (Label) vbox.getChildren().get(0);
        descriptionLabel.setText("Description : " + description);
        Label startDateLabel = (Label) vbox.getChildren().get(1);
        startDateLabel.setText("Début : " + startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        displayEndDate(endDate, (TextFlow) vbox.getChildren().get(2));
        taskPanel.getPanel().setText(collaborator.contains(null) | collaborator.isEmpty() ?  title : "<" + title + "> attribuée à " + collaborator);
    }

    public static void displayEndDate(LocalDate endDate, TextFlow endDateTextFlow){
        SubProjectView.displayEndDateText(endDate, endDateTextFlow);
    }
}
