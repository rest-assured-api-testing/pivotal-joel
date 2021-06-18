package managers;

public enum Endpoint {
    PROJECT_EPICS("ENDPOINT_PROJECT_EPICS"),
    PROJECT_EPIC("ENDPOINT_PROJECT_EPIC"),
    EPIC("ENDPOINT_EPIC");

    private String text;

    Endpoint(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
