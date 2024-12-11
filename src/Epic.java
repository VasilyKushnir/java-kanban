
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addInSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    public void removeFromSubtaskIds(Integer id) {
        subtaskIds.remove(id);
    }

    @Override
    public String toString() {
        return "Task{ " +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus='" + getStatus() + '\'' +
                ", subtaskIds='" + getSubtaskIds() +
                " }";
    }

}
