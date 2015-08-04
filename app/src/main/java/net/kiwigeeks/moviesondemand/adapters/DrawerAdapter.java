package net.kiwigeeks.moviesondemand.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.kiwigeeks.moviesondemand.DrawerClass;
import net.kiwigeeks.moviesondemand.MainActivity;
import net.kiwigeeks.moviesondemand.R;
import net.kiwigeeks.moviesondemand.activities.SettingsActivity;

import java.util.Collections;
import java.util.List;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyViewHolder> {
    private final LayoutInflater inflater;

    List<DrawerClass> data = Collections.emptyList();
    Context context;


    public DrawerAdapter(Context context, List<DrawerClass> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);

    }

    //Let us add a method to delete an item
    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.custom_raw, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final DrawerClass currentObject = data.get(position);

        viewHolder.title.setText(currentObject.title);

        viewHolder.image.setImageResource(currentObject.iconId);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(context, MainActivity.class);
                Intent settingsIntent = new Intent(context, SettingsActivity.class);

                //String about=context.getString(R.string.about);

                switch (currentObject.title) {
                    case "Home":
                        context.startActivity(homeIntent);
                        break;

                    case "Settings":
                        context.startActivity(settingsIntent);
                        break;

                    case "About this App":
                        showAboutPage();
                        break;
                }


            }
        });

    }

    private void showAboutPage() {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.about));
        alertDialog.setMessage(context.getString(R.string.about_text));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.listicon);
            title = (TextView) itemView.findViewById(R.id.title);




        }
    }
}
