package com.nanodegree.mahmoud.movies.Main.enteties;

import java.io.Serializable;

/**
 * Created by Mahmoud on 04/03/2017.
 */

public class Movie implements Serializable {

    public Movie(String title, String poster_path, String release_date, String id, String vote_average, String overview) {
        this.title = title;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.id = id;
        this.vote_average = vote_average;
        this.overview = overview;
    }

    public Movie() {
    }

    ;
    String title;
    String poster_path;
    String release_date;
    String id;

    String vote_average;
    String overview;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getId() {
        return id;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }


}
