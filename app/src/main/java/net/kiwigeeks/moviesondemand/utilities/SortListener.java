package net.kiwigeeks.moviesondemand.utilities;



public interface SortListener {

    void onSortTitle();

    void onSortByDate();

    void onSortByRating();
    void onRefresh();
}