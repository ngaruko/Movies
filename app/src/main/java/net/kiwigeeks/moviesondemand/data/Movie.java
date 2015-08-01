package net.kiwigeeks.moviesondemand.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by itl on 2/07/2015.
 */
public class Movie implements Parcelable {
//Or use this library here:https://github.com/johncarl81/parceler


    private String id;
    private String releaseDate;
    private String title;
    private String rated;
    private String plot;
    private String urlPoster;

    private String rating;
    private String runtime;
    private String genres;
    private String trailerUrl;


    public Movie() {
    }

    public Movie(Parcel in) {
id=in.readString();
        title=in.readString();
        urlPoster=in.readString();
        rated=in.readString();
        rating=in.readString();
        releaseDate=in.readString();
        runtime=in.toString();
        genres=in.readString();
        trailerUrl=in.toString();


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReleaseDate() {
        return releaseDate;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getUrlPoster() {
        return urlPoster;
    }

    public void setUrlPoster(String urlPoster) {
        this.urlPoster = urlPoster;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Movie(String id, String releaseDate, String title, String rated, String plot, String urlPoster, String rating, String runtime, String genres, String trailerUrl) {
        this.id = id;
        this.releaseDate = releaseDate;
        this.title = title;
        this.rated = rated;
        this.plot = plot;
        this.urlPoster = urlPoster;
        this.rating = rating;
        this.runtime=runtime;
        this.genres=genres;
        this.trailerUrl=trailerUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(urlPoster);
        dest.writeString(rated);
        dest.writeString(rating);
        dest.writeString(releaseDate);
        dest.writeString(runtime);
        dest.writeString(genres);
        dest.writeString(trailerUrl);


    }


    public static final Creator<Movie> CREATOR
            = new Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }
}
