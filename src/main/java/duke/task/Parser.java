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

        String string;
        try {
            if (command.equals("bye")) {
                storage.upload(tasks);
                string = ui.showExit();
                return string;
            }

            if (command.equals("list")) {
                if (tasks.length() > 0) {
                    string = "Here's your list!\n" + ui.showTaskList(tasks);
                } else {
                    assert tasks.length() == 0;
                    throw new YooException("You have no tasks!");
                }
            } else {
                String[] cmd = command.split(" ", 2);

                switch (cmd[0]) {
                case "done":
                    string = markAsDone(tasks, cmd, ui);
                    break;
                case "delete":
                    string = deleteTask(tasks, cmd, ui);
                    break;
                case "todo":
                    string = addTodo(tasks, cmd, ui);
                    break;
                case "deadline":
                    string = addDeadline(tasks, cmd, ui);
                    break;
                case "event":
                    string = addEvent(tasks, cmd, ui);
                    break;
                case "find":
                    string = findKeyword(tasks, cmd, ui);
                    break;
                default:
                    throw new YooException("Sorry, I didn't get that (\u3063*\u00B4\u25A1`)\u3063");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new YooException("Please add a task description (>_<)");
        }
        return string;
    }

    private static String findKeyword(TaskList tasks, String[] cmd, Ui ui) {
        TaskList tasksWithKeyword = new TaskList();
        if(tasks.length() == 0) {
            return ui.showCannotFindKeyword();
        }
        for (int i = 0; i < tasks.length(); i++) {
            Task t = tasks.get(i);
            if (t.getDescription().contains(cmd[1])) {
                tasksWithKeyword.add(t);
            }
        }
        if (tasksWithKeyword.length() > 0) {
            return ui.showFoundKeyword() + "\n" + ui.showTaskList(tasksWithKeyword);
        } else {
            assert tasksWithKeyword.length() == 0;
            return ui.showCannotFindKeyword();
        }
    }

    private static String markAsDone(TaskList tasks, String[] temp, Ui ui) throws YooException {
        String result = "";
        try {
            int index = Integer.parseInt(temp[1]);
            if (index <= tasks.length() && index > 0) {
                Task t = tasks.get(index - 1);
                t.markAsDone();
                result = ui.congratulate() + "\n" + index + ". " + t;
            }
        } catch (IndexOutOfBoundsException e) {
            throw new YooException("No such task (>_<)");
        } catch (NumberFormatException e) {
            throw new YooException("Please enter a valid task index (>_<)");
        }
        return result;
    }

    private static String deleteTask(TaskList tasks, String[] temp, Ui ui) throws YooException {
        String result = "";
        try {
            int index = Integer.parseInt(temp[1]);
            if (index <= tasks.length() && index > 0) {
                Task t = tasks.delete(index - 1);
                result = ui.confirmDelete(t, tasks);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new YooException("No such task (>_<)");
        } catch (NumberFormatException e) {
            throw new YooException("Please enter a valid task index (>_<)");
        }
        return result;
    }

    private static String addTodo(TaskList tasks, String[] temp, Ui ui) {
        Todo td = new Todo(temp[1]);
        tasks.add(td);
        return ui.confirmAdd(td, tasks);
    }

    private static String addDeadline(TaskList tasks, String[] temp, Ui ui) throws YooException {
        try {
            String[] a = temp[1].split("/by ", 2);
            LocalDate by = LocalDate.parse(a[1]);
            Deadline dl = new Deadline(a[0], by);
            tasks.add(dl);
            return ui.confirmAdd(dl, tasks);

        } catch (IndexOutOfBoundsException e) {
            throw new YooException("Sorry, your deadline time is missing (\u3063*\u00B4\u25A1`)\u3063");
        } catch (DateTimeParseException e) {
            throw new YooException("Invalid date! Please try again (>_<)");
        }
    }

    private static String addEvent(TaskList tasks, String[] temp, Ui ui) throws YooException {
        try {
            String[] a = temp[1].split("/at ", 2);
            LocalDate at = LocalDate.parse(a[1]);
            Event e = new Event(a[0], at);
            tasks.add(e);
            return ui.confirmAdd(e, tasks);

        } catch (IndexOutOfBoundsException e) {
            throw new YooException("Sorry, your event time is missing (\u3063*\u00B4\u25A1`)\u3063");
        } catch (DateTimeParseException e) {
            throw new YooException("Invalid date! Please try again (>_<)");
        }
    }
}
