import taskmanager.Managers;
import taskmanager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Month.*;


public class Main {
    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();

        ArrayList<Task> tasks = tm.getTaskList();
        for (Task t : tasks) {
            System.out.println(t);
        }

        ArrayList<Epic> epics = tm.getEpicList();
        for (Task t : epics) {
            System.out.println(t);
        }

        ArrayList<Subtask> subtasks = tm.getSubtaskList();
        for (Task t : subtasks) {
            System.out.println(t);
        }

        Task task1 = new Task("Task2", "Description task2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.of(2025, 5, 26, 7, 20));

        Subtask subtask1 = new Subtask(2, "Subtask3", "Description subtask3",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(27),
                LocalDateTime.of(2025, 5,25, 11,18));

        tm.createTask(task1);
        tm.createSubtask(subtask1);


//        Task task1 = new Task("Повернуть", "Направо", TaskStatus.DONE, Duration.ofMinutes(2),
//                LocalDateTime.of(2025, JUNE, 2, 12, 0));
//        tm.createTask(task1);
//
//        Task task2 = new Task("Пройти вперёд", "300 метров по бульвару", TaskStatus.IN_PROGRESS,
//                Duration.ofMinutes(6), LocalDateTime.of(2025, JUNE, 2, 12, 7));
//        tm.createTask(task2);
//
//        Epic epic1 = new Epic("Найти нужный дом", "9-ти этажный");
//        tm.createEpic(epic1);
//
//        Subtask subtask1 = new Subtask(3, "Достать смартфон", "Свериться с картой",
//                TaskStatus.NEW, Duration.ofMinutes(8),
//                LocalDateTime.of(2025, JUNE, 2, 12, 18));
//        tm.createSubtask(subtask1);
//
//        Epic epic2 = new Epic("Забрать посылку", "Найти нужную квартиру");
//        tm.createEpic(epic2);
//
//        Subtask subtask2 = new Subtask(5, "Узнать этаж", "Проверить по почтовым ящикам",
//                TaskStatus.NEW, Duration.ofMinutes(11),
//                LocalDateTime.of(2025, JUNE, 3, 16, 28));
//        tm.createSubtask(subtask2);
//
//        Subtask subtask3 = new Subtask(5, "Подняться к нужной квартире", "Использовать лифт",
//                TaskStatus.NEW, Duration.ofMinutes(2),
//                LocalDateTime.of(2025, JUNE, 3, 16, 43));
//        tm.createSubtask(subtask3);
//
//        System.out.println(tm.getTask(1));
//        System.out.println(tm.getTask(2));
//        System.out.println(tm.getEpic(3));
//        System.out.println(tm.getEpic(5));
//
//        Task task1Updated = new Task("Повернуть", "Налево", TaskStatus.DONE, Duration.ofMinutes(2),
//                LocalDateTime.of(2025, AUGUST, 7, 9, 37));
//        task1Updated.setId(1);
//        tm.updateTask(task1Updated);
//
//        Task task2Updated = new Task("Пройти вперёд", "500 метров по грунтовке", TaskStatus.DONE,
//                Duration.ofMinutes(8), LocalDateTime.of(2025, AUGUST, 7, 9, 40));
//        task2Updated.setId(2);
//        tm.updateTask(task2Updated);
//
//        Epic epic1Updated = new Epic("Найти нужный дом", "5-ти этажный");
//        epic1Updated.setId(3);
//        epic1Updated.addInSubtaskIds(4);
//        tm.updateEpic(epic1Updated);
//
//        Subtask subtask1Updated = new Subtask(3, "Достать телефон", "Открыть приложение",
//                TaskStatus.DONE, Duration.ofMinutes(7),
//                LocalDateTime.of(2025, AUGUST, 7, 9, 50));
//        subtask1Updated.setId(4);
//        tm.updateSubtask(subtask1Updated);
//
//        Epic epic2Updated = new Epic("Забрать посылку", "Найти нужную квартиру");
//        epic2Updated.setId(5);
//        epic2Updated.addInSubtaskIds(6);
//        epic2Updated.addInSubtaskIds(7);
//        tm.updateEpic(epic2Updated);
//
//        Subtask subtask2Updated = new Subtask(5, "Узнать этаж", "Проверить по почтовым ящикам",
//                TaskStatus.IN_PROGRESS, Duration.ofMinutes(8),
//                LocalDateTime.of(2025, AUGUST, 9, 17, 21));
//        subtask2Updated.setId(6);
//        tm.updateSubtask(subtask2Updated);
//
//        tm.removeSubtask(7);
//
//        System.out.println(tm.getTask(1));
//        System.out.println(tm.getTask(2));
//        System.out.println(tm.getEpic(3));
//        System.out.println(tm.getEpic(5));
    }
}