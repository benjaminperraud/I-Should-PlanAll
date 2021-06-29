package be.ac.ulb.infof307.g04.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    LocalDate dummyStartDate = LocalDate.now();
    LocalDate dummyEndDate = dummyStartDate.plusDays(3);
    String dummyTitle = "This task";
    String dummyDescription = "That description";
    Task task = new Task(dummyTitle, dummyDescription, dummyStartDate, dummyEndDate);
    Task kast = new Task("This kast", dummyDescription, dummyStartDate, dummyEndDate);
    Task task2 = new Task ("This task2", dummyDescription, dummyStartDate, dummyStartDate.plusDays(1));


    @Test
    void testNeqCompareTo() {
        assertNotEquals(task, kast);
    }

    @Test
    void testEqCompareTo() {
        Task task = new Task(dummyTitle, "new description", dummyStartDate, dummyEndDate);
        assertEquals(task, task);
    }

    @Test
    void testEqEquals() {
        LinkedList<Task> tasks = new LinkedList<>();
        tasks.add(task);
        tasks.add(kast);
        assertTrue(tasks.contains(task));
    }

    @Test
    void testNeqEquals() {
        LinkedList<Task> tasks = new LinkedList<>();
        Task unFound = new Task("Looker", "no des", dummyStartDate, dummyEndDate);
        tasks.add(task);
        tasks.add(kast);
        assertFalse(tasks.contains(unFound));
    }

    @org.junit.jupiter.api.Test
    void reportTaskReminder(){
        Task urgent = new Task("urgent", dummyDescription, dummyStartDate, LocalDate.now().plusDays(18));
        Task veryUrgent = new Task("veryUrgent", dummyDescription, dummyStartDate, LocalDate.now().plusDays(1));
        assertEquals(Duration.ofDays(0), urgent.getReport());
        urgent.report();
        assertEquals(Duration.ofDays(-15), urgent.getReport());
        assertEquals(Duration.ofDays(0), veryUrgent.getReport());
        veryUrgent.report();
        assertEquals(Duration.ofDays(2), veryUrgent.getReport());
    }

}