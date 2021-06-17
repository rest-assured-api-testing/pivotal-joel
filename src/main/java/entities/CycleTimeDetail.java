package entities;

public class CycleTimeDetail {
    private String kind;
    private int total_cycle_time;
    private int started_time;
    private int started_count;
    private int finished_time;
    private int finished_count;
    private int delivered_time;
    private int delivered_count;
    private int rejected_time;
    private int rejected_count;
    private Long story_id;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getTotal_cycle_time() {
        return total_cycle_time;
    }

    public void setTotal_cycle_time(int total_cycle_time) {
        this.total_cycle_time = total_cycle_time;
    }

    public int getStarted_time() {
        return started_time;
    }

    public void setStarted_time(int started_time) {
        this.started_time = started_time;
    }

    public int getStarted_count() {
        return started_count;
    }

    public void setStarted_count(int started_count) {
        this.started_count = started_count;
    }

    public int getFinished_time() {
        return finished_time;
    }

    public void setFinished_time(int finished_time) {
        this.finished_time = finished_time;
    }

    public int getFinished_count() {
        return finished_count;
    }

    public void setFinished_count(int finished_count) {
        this.finished_count = finished_count;
    }

    public int getDelivered_time() {
        return delivered_time;
    }

    public void setDelivered_time(int delivered_time) {
        this.delivered_time = delivered_time;
    }

    public int getDelivered_count() {
        return delivered_count;
    }

    public void setDelivered_count(int delivered_count) {
        this.delivered_count = delivered_count;
    }

    public int getRejected_time() {
        return rejected_time;
    }

    public void setRejected_time(int rejected_time) {
        this.rejected_time = rejected_time;
    }

    public int getRejected_count() {
        return rejected_count;
    }

    public void setRejected_count(int rejected_count) {
        this.rejected_count = rejected_count;
    }

    public Long getStory_id() {
        return story_id;
    }

    public void setStory_id(Long story_id) {
        this.story_id = story_id;
    }
}
