package duke.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Encapsulates a parser that parses and executes commands.
 */
public class Parser {

    /**
     * Returns a boolean after parsing and executing the command.
     * @param command User's command.
     * @param tasks List of tasks.
     * @param ui Ui object that interacts with user.
     * @param storage Storage object to store the list of tasks.
     * @return true if it is a bye command, false otherwise.
     * @throws YooException if incorrect or wrongly formatted command.
     */
    protected static String parse(String command, TaskList tasks, Ui ui, Storage storage) throws YooException {

        String result;

        if (command.equals("bye")) {
            storage.upload(tasks);
            result = ui.showExit();
            return result;
        }

        if (command.equals("list")) {
            if (tasks.length() > 0) {
                result = "Here's your list!\n" + ui.showTaskList(tasks);
            } else {
                result = "You have no tasks!";
            }
            return result;
        }

        try {
            //string array cmd contains keyword command in [0] and rest of the information in [1]
            String[] cmd = command.split(" ", 2);
            switch (cmd[0]) {
            case "done":
                result = markAsDone(tasks, cmd, ui);
                break;
            case "delete":
                result = deleteTask(tasks, cmd, ui);
                break;
            case "todo":
                result = addTodo(tasks, cmd, ui);
                break;
            case "deadline":
                result = addDeadline(tasks, cmd, ui);
                break;
            case "event":
                result = addEvent(tasks, cmd, ui);
                break;
            case "find":
                result = findKeyword(tasks, cmd, ui);
                break;
            default:
                throw new YooException("Sorry, I didn't get that (\u3063*\u00B4\u25A1`)\u3063");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new YooException("Please add a task description (>_<)");
        }
        return result;
    }

    private static String findKeyword(TaskList tasks, String[] cmd, Ui ui) {
        TaskList tasksWithKeyword = new TaskList();

        //if original task list is empty
        if (tasks.length() == 0) {
           return ui.showCannotFindKeyword();
        }

        //searching for matching keywords
        for (int i = 0; i < tasks.length(); i++) {
            Task t = tasks.get(i);
            if (t.getDescription().contains(cmd[1])) {
                tasksWithKeyword.add(t);
            }
        }

        //show tasks with matching keywords
        if (tasksWithKeyword.length() > 0) {
            return ui.showFoundKeyword() + "\n" + ui.showTaskList(tasksWithKeyword);
        } else {
            return ui.showCannotFindKeyword();
        }
    }

    private static String markAsDone(TaskList tasks, String[] cmd, Ui ui) throws YooException {
        try {
            //retrieve the index of task to be marked as done
            int index = Integer.parseInt(cmd[1]);

            //if index is within range of task list
            if (index <= tasks.length() && index > 0) {
                Task t = tasks.get(index - 1);
                t.markAsDone();
                return ui.congratulate() + "\n" + index + ". " + t;
            } else {
                assert index <= 0 || index > tasks.length();
                throw new YooException("No such task (>_<)");
            }
        } catch (NumberFormatException e) {
            throw new YooException("Please enter a valid task index (>_<)");
        }
    }

    private static String deleteTask(TaskList tasks, String[] cmd, Ui ui) throws YooException {
        try {
            //retrieve the index of task to be deleted
            int index = Integer.parseInt(cmd[1]);

            //if index is within range of task list
            if (index <= tasks.length() && index > 0) {
                Task t = tasks.delete(index - 1);
                return ui.confirmDelete(t, tasks);
            } else {
                assert index <= 0 || index > tasks.length();
                throw new YooException("No such task (>_<)");
            }
        } catch (NumberFormatException e) {
            throw new YooException("Please enter a valid task index (>_<)");
        }
    }

    private static String addTodo(TaskList tasks, String[] cmd, Ui ui) {
        Todo td = new Todo(cmd[1]);
        tasks.add(td);
        return ui.confirmAdd(td, tasks);
    }

    private static String addDeadline(TaskList tasks, String[] cmd, Ui ui) throws YooException {
        try {
            //string array contains name in [0] and time in [1]
            String[] dlNameAndTime = cmd[1].split("/by ", 2);
            LocalDate by = LocalDate.parse(dlNameAndTime[1]);

            Deadline dl = new Deadline(dlNameAndTime[0], by);
            tasks.add(dl);
            return ui.confirmAdd(dl, tasks);

        } catch (IndexOutOfBoundsException e) {
            throw new YooException("Sorry, your deadline time is missing (\u3063*\u00B4\u25A1`)\u3063");
        } catch (DateTimeParseException e) {
            throw new YooException("Date format should be yyyy-mm-dd! Please try again (>_<)");
        }
    }

    private static String addEvent(TaskList tasks, String[] cmd, Ui ui) throws YooException {
        try {
            //string array contains name in [0] and time in [1]
            String[] eNameAndTime = cmd[1].split("/at ", 2);
            LocalDate at = LocalDate.parse(eNameAndTime[1]);

            Event e = new Event(eNameAndTime[0], at);
            tasks.add(e);
            return ui.confirmAdd(e, tasks);

        } catch (IndexOutOfBoundsException e) {
            throw new YooException("Sorry, your event time is missing (\u3063*\u00B4\u25A1`)\u3063");
        } catch (DateTimeParseException e) {
            throw new YooException("Date format should be yyyy-mm-dd! Please try again (>_<)");
        }
    }
}
