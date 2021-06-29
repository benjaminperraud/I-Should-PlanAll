package be.ac.ulb.infof307.g04.util;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

/**
 * Utility class for performing operations on date
 */
public final class ToolDate {

    /* Constructor */
    private ToolDate() {}

    /**
     * Check if the first date is earlier than the second one
     * @param earlierDate : the earlier date
     * @param laterDate : the later date
     * @return true if it is earlier, false if else
     */
    public static boolean isEarlierThanDate(LocalDate earlierDate, LocalDate laterDate) {
        return earlierDate.isBefore(laterDate) || earlierDate.isEqual(laterDate);
    }

    /**
     * Disable past dates of the DatePicker
     * @param date javafx DatePicker
     */
    public static void disablePastDates(DatePicker date) {
        date.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

}
