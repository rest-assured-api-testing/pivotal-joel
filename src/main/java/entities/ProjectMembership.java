package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ProjectMembership {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long project_id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String initials;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String project_color;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean favorite;

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getProject_color() {
        return project_color;
    }

    public void setProject_color(String project_color) {
        this.project_color = project_color;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
