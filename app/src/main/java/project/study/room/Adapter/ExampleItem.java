package project.study.room.Adapter;

public class ExampleItem {

    private int id;
    private int userId;
    private String title;
    private String text;

    public ExampleItem(int id, int userId, String title, String text) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
