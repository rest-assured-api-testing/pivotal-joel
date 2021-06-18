package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

public class EpicComment {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long epic_id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String text;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long person_id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String created_at;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updated_at;

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

    public Long getEpic_id() {
        return epic_id;
    }

    public void setEpic_id(Long epic_id) {
        this.epic_id = epic_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
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
}
