package com.csc301.project.iron;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Exercise> exercises;

    //database boolean
    private DatabaseReference mRootRef;
    private DatabaseReference mExerciseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        toolbar = (Toolbar) findViewById(R.id.toolbar_workout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //referencing the data base
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_workout);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        // specify an adapter (see also next example)
        exercises = new ArrayList<>();
        setRecyclerViewData();
        mAdapter = new WorkoutAdapter(this, exercises);
        mRecyclerView.setAdapter(mAdapter);


        final FloatingActionButton fabAddExercise = (FloatingActionButton) findViewById(R.id.fab_add_exercise);
        fabAddExercise.setOnClickListener(goToStartWorkout());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fabAddExercise.isShown())
                    fabAddExercise.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fabAddExercise.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit_workout:
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    private void addExercise() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WorkoutActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.add_new_item_dialog, null);
        final EditText userInputExerciseName = (EditText) mView.findViewById(R.id.etItemName);
        Button ok_button = (Button) mView.findViewById(R.id.ok_button);
        mBuilder.setTitle("Add Exercise: ");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String given_text = userInputExerciseName.getText().toString();
                if (!given_text.isEmpty()) {

                    Exercise exercise = new Exercise(given_text.trim());

                    //adding new object to arraylist
                    exercises.add(exercise);

                    mRootRef.child("users").child(mUser.getUid()).child("workouts").child(WorkoutsAdapter.globalName).child(given_text).setValue(" ");

                    //notify data set changed in RecyclerView adapter
                    mAdapter.notifyDataSetChanged();

                    //close dialog after all
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private View.OnClickListener goToStartWorkout() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExercise();
            }
        };

    }

    /*
    private View.OnClickListener onConfirmListener(final EditText name, final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exercise exercise = new Exercise(name.getText().toString().trim());

                //adding new object to arraylist
                exercises.add(exercise);

                //notify data set changed in RecyclerView adapter
                mAdapter.notifyDataSetChanged();

                //close dialog after all
                dialog.dismiss();
            }
        };
    }
*/

    private void setRecyclerViewData() {
        mExerciseRef = mRootRef.child("users").child(mUser.getUid()).child("workouts").child(WorkoutsAdapter.globalName);
        mExerciseRef.addValueEventListener(new ValueEventListener() {
            @Override
            //dataSnapshot gives the value
            public void onDataChange(DataSnapshot dataSnapshot) {
                exercises.clear();
                Iterator i = dataSnapshot.getChildren().iterator();


                while(i.hasNext()){

                    exercises.add(new Exercise(((DataSnapshot)i.next()).getKey()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
