package net.kiwigeeks.moviesondemand.utilities;



/**
 * Created by Windows on 18-02-2015.
 */
public interface SortListener {

    public void onSortTitle();
    public void onSortByDate();
    public void onSortByRating();

    void onRefresh();
}