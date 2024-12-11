
public class Main {
    public static void main(String[] args) {

        TaskManager tm = new TaskManager();

        Task task1 = new Task("Повернуть", "Направо", TaskStatus.DONE);
        tm.createTask(task1);

        Task task2 = new Task("Пройти вперёд", "300 метров по бульвару", TaskStatus.IN_PROGRESS);
        tm.createTask(task2);

        Epic epic1 = new Epic("Найти нужный дом", "9-ти этажный");
        tm.createEpic(epic1);

        Subtask subtask1 = new Subtask(3, "Достать смартфон", "Свериться с картой",
                TaskStatus.NEW);
        tm.createSubtask(subtask1);

        Epic epic2 = new Epic("Забрать посылку", "Найти нужную квартиру");
        tm.createEpic(epic2);

        Subtask subtask2 = new Subtask(5, "Узнать этаж", "Проверить по почтовым ящикам",
                TaskStatus.NEW);
        tm.createSubtask(subtask2);

        Subtask subtask3 = new Subtask(5, "Подняться к нужной квартире", "Использовать лифт",
                TaskStatus.NEW);
        tm.createSubtask(subtask3);

        System.out.println(tm.getTask(1));
        System.out.println(tm.getTask(2));
        System.out.println(tm.getEpic(3));
        System.out.println(tm.getEpic(5));

        Task task1Updated = new Task("Повернуть", "Налево", TaskStatus.DONE);
        tm.updateTask(1, task1Updated);

        Task task2Updated = new Task("Пройти вперёд", "500 метров по грунтовке", TaskStatus.DONE);
        tm.updateTask(2, task2Updated);

        Epic epic1Updated = new Epic("Найти нужный дом", "5-ти этажный");
        tm.updateEpic(3, epic1Updated);

        Subtask subtask1Updated = new Subtask(3, "Достать телефон", "Открыть приложение",
                TaskStatus.DONE);
        tm.updateSubtask(4, subtask1Updated);

        Epic epic2Updated = new Epic("Забрать посылку", "Найти нужную квартиру");
        tm.updateEpic(5, epic2Updated);

        Subtask subtask2Updated = new Subtask(5, "Узнать этаж", "Проверить по почтовым ящикам",
                TaskStatus.IN_PROGRESS);
        tm.updateSubtask(6, subtask2Updated);

        tm.removeSubtask(7);

        System.out.println(tm.getTask(1));
        System.out.println(tm.getTask(2));
        System.out.println(tm.getEpic(3));
        System.out.println(tm.getEpic(5));

    }
}
