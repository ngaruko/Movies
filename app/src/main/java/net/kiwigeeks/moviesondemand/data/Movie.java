package net.kiwigeeks.moviesondemand.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {



    public static final Creator<Movie> CREATOR
            = new Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    public String id;
    public String releaseDate;
    public String title;
    public String rated;
    public String plot;
    public String urlPoster;
    public String rating;
    public String runtime;
    public String genres;
    public String trailerUrl;
    public String urlIMDB;

    public Movie() {
    }

    public Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        urlPoster = in.readString();
        rated = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        runtime = in.readString();
        genres = in.readString();
        trailerUrl = in.readString();
        urlIMDB = in.readString();
        plot = in.readString();


    }

    public Movie(String id, String releaseDate, String title, String rated, String plot, String urlPoster, String rating, String runtime, String genres, String trailerUrl, String urlIMDB) {
        this.id = id;
        this.releaseDate = releaseDate;
        this.title = title;
        this.rated = rated;

        this.urlPoster = urlPoster;
        this.rating = rating;
        this.runtime = runtime;
        this.genres = genres;
        this.trailerUrl = trailerUrl;
        this.urlIMDB = urlIMDB;
        this.plot = plot;
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

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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
        dest.writeString(urlIMDB);
        dest.writeString(plot);



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

    public String getUrlIMDB() {
        return urlIMDB;
    }

    public void setUrlIMDB(String urlIMDB) {
        this.urlIMDB = urlIMDB;
    }
}
