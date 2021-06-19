package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

public class IterationOverride {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int number;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long project_id;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int length;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float team_strength;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public float getTeam_strength() {
        return team_strength;
    }

    public void setTeam_strength(float team_strength) {
        this.team_strength = team_strength;
    }
}
