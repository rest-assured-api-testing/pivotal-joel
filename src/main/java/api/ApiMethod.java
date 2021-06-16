package api;

public enum ApiMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT");

    private String name;

    ApiMethod(String name) {
        this.name = name;
    }

    public String toName() {
        return name;
    }
}
