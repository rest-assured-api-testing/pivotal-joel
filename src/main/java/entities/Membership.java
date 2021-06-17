package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Membership {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String created_at;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updated_at;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Person person;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long project_id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String project_color;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean favorite;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String last_viewed_at;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean wants_comment_notification_emails;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean will_receive_mention_notifications_or_emails;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

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

    public String getLast_viewed_at() {
        return last_viewed_at;
    }

    public void setLast_viewed_at(String last_viewed_at) {
        this.last_viewed_at = last_viewed_at;
    }

    public boolean isWants_comment_notification_emails() {
        return wants_comment_notification_emails;
    }

    public void setWants_comment_notification_emails(boolean wants_comment_notification_emails) {
        this.wants_comment_notification_emails = wants_comment_notification_emails;
    }

    public boolean isWill_receive_mention_notifications_or_emails() {
        return will_receive_mention_notifications_or_emails;
    }

    public void setWill_receive_mention_notifications_or_emails(boolean will_receive_mention_notifications_or_emails) {
        this.will_receive_mention_notifications_or_emails = will_receive_mention_notifications_or_emails;
    }
}
