package com.csc301.project.iron;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

public class StartWorkoutActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mRootRef;
    private DatabaseReference mExerciseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private List<Exercise> exercises;
    private CardView mCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        //referencing the data base
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_start_workout);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        exercises = new ArrayList<>();
        setRecyclerViewData();
        mAdapter = new WorkoutAdapter(this, exercises);
        mRecyclerView.setAdapter(mAdapter);
    }


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
