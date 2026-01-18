package com.example.clickanddrive.dtosample;

// Only a sample class for route in favorite routes
// This will be used for simulating and designing how the fragment will look like
public class FavoriteRouteSampleDTO {
    private String from;
    private String to;
    private String distance;
    private String duration;
    private int timesUsed;
    private boolean favorite;


    public FavoriteRouteSampleDTO(String from, String to, String distance, String duration, int timesUsed, boolean favorite) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.duration = duration;
        this.timesUsed = timesUsed;
        this.favorite = favorite;
    }

    public String getRouteTitle() {
        return from + " - " + to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {return to;}

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
