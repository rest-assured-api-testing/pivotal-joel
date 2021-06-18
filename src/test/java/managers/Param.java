package managers;

public enum Param {
    PROJECT_ID("projectId");

    private String name;

    Param(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
