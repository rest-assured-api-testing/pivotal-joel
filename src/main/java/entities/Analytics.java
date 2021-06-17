package entities;

public class Analytics {
    private String kind;
    private int stories_accepted;
    private int bugs_created;
    private int cycle_time;
    private double rejection_rate;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getStories_accepted() {
        return stories_accepted;
    }

    public void setStories_accepted(int stories_accepted) {
        this.stories_accepted = stories_accepted;
    }

    public int getBugs_created() {
        return bugs_created;
    }

    public void setBugs_created(int bugs_created) {
        this.bugs_created = bugs_created;
    }

    public int getCycle_time() {
        return cycle_time;
    }

    public void setCycle_time(int cycle_time) {
        this.cycle_time = cycle_time;
    }

    public double getRejection_rate() {
        return rejection_rate;
    }

    public void setRejection_rate(double rejection_rate) {
        this.rejection_rate = rejection_rate;
    }
}
