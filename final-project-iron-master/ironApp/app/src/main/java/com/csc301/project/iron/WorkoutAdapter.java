package com.csc301.project.iron;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ExerciseViewHolder> {


    List<Exercise> exercises;
    private Activity activity;
    public static String globalName;

    WorkoutAdapter(Activity activity, List<Exercise> persons){
        this.activity = activity;
        this.exercises = persons;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.card_view_workout, viewGroup, false);
        ExerciseViewHolder viewHolder = new ExerciseViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WorkoutAdapter.ExerciseViewHolder holder, int position) {
        //setting data to view holder elements
        holder.name.setText(exercises.get(position).getName());



        //set on click listener for each element
        holder.container.setOnClickListener(onClickListener(position));
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.card_view_workout);
                dialog.setTitle("Position " + position);
                dialog.setCancelable(true); // dismiss when touching outside Dialog

                // set the custom dialog components - texts and image
                TextView name = (TextView) dialog.findViewById(R.id.exercise_name);

                setDataToView(name, position);

                dialog.show();
            }
        };
    }

    private void setDataToView(TextView name,  int position) {
        name.setText(exercises.get(position).getName());
        globalName = exercises.get(position).getName();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    protected class ExerciseViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView name;
        private View container;
        public ExerciseViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.exercise_name);
            container = v.findViewById(R.id.cv_workout);
        }


    }

}



