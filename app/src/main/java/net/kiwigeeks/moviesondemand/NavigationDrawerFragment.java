package net.kiwigeeks.moviesondemand;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kiwigeeks.moviesondemand.adapters.DrawerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {


    private static final String PREF_FILE_NAME = "testPref";
    private static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    DrawerLayout mDrawerLayout;
    private DrawerAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mUserLearnDrawer;
    private boolean mFromSavedInstance;
    private RecyclerView recycleView;
    private View containerView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();

    }

    public static String readFromSharedPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnDrawer = Boolean.valueOf(readFromSharedPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        if (savedInstanceState != null) mFromSavedInstance = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        recycleView = (RecyclerView) layout.findViewById(R.id.recycler_view);

        //set adapter
//
//        TextView status= (TextView) layout.findViewById(R.id.sign_status);
//       // status.setText("Signed in as :" + );


        adapter = new DrawerAdapter(getActivity(), getData());

        recycleView.setAdapter(adapter);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public List<DrawerClass> getData() {
        List<DrawerClass> data = new ArrayList<>();

        String[] titles = getResources().getStringArray(R.array.drawerItems);

        int[] icons = {R.drawable.home, R.drawable.button_focused, R.drawable.button_focused};

        for (int i = 0; i < titles.length  && i < icons.length; i++) {
            DrawerClass current = new DrawerClass();

            current.title = titles[i];

            current.iconId = icons[i];

            data.add(current);
        }


        return data;


    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {

        containerView = getView().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {


            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);


                if (slideOffset < 0.6) toolbar.setAlpha(1 - slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mUserLearnDrawer) {
                    mUserLearnDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnDrawer + "");
                }
                getActivity().invalidateOptionsMenu();

            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);


                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnDrawer && mFromSavedInstance) {

            mDrawerLayout.openDrawer(containerView);

        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();

            }
        });
    }
}
