package entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

public class Story {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long project_id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String story_type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String current_state;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float estimate;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float requested_by_id;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float owned_by_id;
    ArrayList <Integer> owner_ids = new ArrayList();
    ArrayList <String> labels = new ArrayList();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String created_at;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updated_at;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String url;

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

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStory_type() {
        return story_type;
    }

    public void setStory_type(String story_type) {
        this.story_type = story_type;
    }

    public String getCurrent_state() {
        return current_state;
    }

    public void setCurrent_state(String current_state) {
        this.current_state = current_state;
    }

    public float getEstimate() {
        return estimate;
    }

    public void setEstimate(float estimate) {
        this.estimate = estimate;
    }

    public float getRequested_by_id() {
        return requested_by_id;
    }

    public void setRequested_by_id(float requested_by_id) {
        this.requested_by_id = requested_by_id;
    }

    public float getOwned_by_id() {
        return owned_by_id;
    }

    public void setOwned_by_id(float owned_by_id) {
        this.owned_by_id = owned_by_id;
    }

    public ArrayList<Integer> getOwner_ids() {
        return owner_ids;
    }

    public void setOwner_ids(ArrayList<Integer> owner_ids) {
        this.owner_ids = owner_ids;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
