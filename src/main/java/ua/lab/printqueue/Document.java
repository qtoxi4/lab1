package ua.lab.printqueue;

public final class Document {
    private final int id;
    private final String content;

    public Document(int id, String content) {
        this.id = id;
        this.content = content == null ? "" : content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Document{id=" + id + ", content='" + content + "'}";
    }
}
