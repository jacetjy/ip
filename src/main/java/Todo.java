public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    protected Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}