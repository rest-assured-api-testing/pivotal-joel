package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Account {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String plan;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int days_left;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean over_the_limit;
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

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDays_left() {
        return days_left;
    }

    public void setDays_left(int days_left) {
        this.days_left = days_left;
    }

    public Boolean getOver_the_limit() {
        return over_the_limit;
    }

    public void setOver_the_limit(Boolean over_the_limit) {
        this.over_the_limit = over_the_limit;
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
