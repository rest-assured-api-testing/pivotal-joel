package managers;

public enum Param {
//    PROJECT_ID("projectId"),
    STORY_ID("storyId"),
    EPIC_ID("epicId");

    private String text;

    private Param(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
