import manager.Managers;
import manager.tasks.TaskManager;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws ManagerSaveException {
        TaskManager manager = Managers.getDefault();

        //Создание
        Task task1 = new Task("Task #1", "Task1 description", TaskStatus.NEW);
        Task task2 = new Task("Task #2", "Task2 description", TaskStatus.IN_PROGRESS);
        final int taskId1 = manager.addNewTask(task1);
        final int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Epic #1", "Epic1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic #2", "Epic2 description", TaskStatus.NEW);
        final int epicId1 = manager.addNewEpic(epic1);
        final int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask #1-1", "Subtask1 description", TaskStatus.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask #2-1", "Subtask2 description", TaskStatus.NEW, epicId2);
        Subtask subtask3 = new Subtask("Subtask #2-2", "Subtask3 description", TaskStatus.NEW, epicId2);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2);
        final Integer subtaskId3 = manager.addNewSubtask(subtask3);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        //Обновление
        System.out.println("ИЗМЕНЕНИЕ ФАЙЛОВ!!!");
        Task taskStatus1 = manager.getTask(taskId1);
        taskStatus1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(taskStatus1);
        System.out.println("Смена статуса Task1: Было:NEW  Стало:IN_PROGRESS");

        Subtask subtaskStatus1 = manager.getSubtask(subtaskId1);
        Subtask subtaskStatus2 = manager.getSubtask(subtaskId2);
        Subtask subtaskStatus3 = manager.getSubtask(subtaskId3);

        manager.updateSubtask(subtaskStatus1);
        subtaskStatus1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateEpic(epic1);
        System.out.println("Смена статуса Subtask1-1: Было:NEW  Стало:IN_PROGRESS");
        subtaskStatus2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtaskStatus2);
        manager.updateEpic(epic2);
        System.out.println("Смена статуса Subtask2-1: Было:NEW  Стало:DONE");
        subtaskStatus3.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtaskStatus3);
        manager.updateEpic(epic2);
        System.out.println("Смена статуса Subtask2-2: Было:NEW  Стало:IN_PROGRESS");


        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getEpicSubtasks(epicId1));
        System.out.println(manager.getEpicSubtasks(epicId2));
        manager.getTask(taskId2);
        manager.getTask(taskId1);
        manager.getEpic(epicId2);
        manager.getSubtask(subtaskId3);


        // Удаление
        System.out.println("УДАЛЕНИЕ ФАЙЛОВ!!!");
        manager.deleteSubtask(subtaskId2);
        System.out.println("Удалена задача Subtask 2-1");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        manager.deleteAllEpics();
        System.out.println("Удалены все задачи типа Epic");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());


        System.out.println("ВЫВОД ИСТОРИИ!!!");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }
}
