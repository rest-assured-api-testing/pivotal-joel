package entities;

public class Membership {
    private String kind;
    private Long id;
    private String created_at;
    private String updated_at;
    Person PersonObject;
    private Long project_id;
    private String role;
    private String project_color;
    private boolean favorite;
    private String last_viewed_at;
    private boolean wants_comment_notification_emails;
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

    public Person getPersonObject() {
        return PersonObject;
    }

    public void setPersonObject(Person personObject) {
        PersonObject = personObject;
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
