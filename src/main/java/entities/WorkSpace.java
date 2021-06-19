package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

public class WorkSpace {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long person_id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ArrayList<Long> project_ids = new ArrayList();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public ArrayList<Long> getProject_ids() {
        return project_ids;
    }

    public void setProject_ids(ArrayList<Long> project_ids) {
        this.project_ids = project_ids;
    }
}
