package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

public class Iteration {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int number;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float project_id;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int length;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int team_strength;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ArrayList <Story> stories = new ArrayList();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String start;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String finish;

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

    public Float getProject_id() {
        return project_id;
    }

    public void setProject_id(Float project_id) {
        this.project_id = project_id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getTeam_strength() {
        return team_strength;
    }

    public void setTeam_strength(int team_strength) {
        this.team_strength = team_strength;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }
}
