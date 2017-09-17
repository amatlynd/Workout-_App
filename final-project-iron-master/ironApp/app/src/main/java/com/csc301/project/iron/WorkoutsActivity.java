package com.csc301.project.iron;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


public class WorkoutsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Workout> workouts;

    //private DatabaseHelper localDB;
    private DatabaseReference mRootRef;
    private DatabaseReference mWorkoutRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private Toolbar toolbar;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        toolbar = (Toolbar) findViewById(R.id.toolbar_workouts);
        setSupportActionBar(toolbar);

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
        workouts = new ArrayList<>();
        setRecyclerViewData();
        mAdapter = new WorkoutsAdapter(this, workouts);
        mRecyclerView.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FloatingActionButton fabAddworkout = (FloatingActionButton) findViewById(R.id.fab_workouts);
        fabAddworkout.setOnClickListener(onAddingListener());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fabAddworkout.isShown())
                    fabAddworkout.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fabAddworkout.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        initNavigationDrawer();

    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                             @Override
                                                             public boolean onNavigationItemSelected(MenuItem menuItem) {

                                                                 int id = menuItem.getItemId();

                                                                 switch (id){
                                                                     case R.id.Workouts:
                                                                         Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                                                                         drawerLayout.closeDrawers();
                                                                         break;
                                                                     case R.id.History:
                                                                         Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_SHORT).show();
                                                                         break;
                                                                     case R.id.Logout:
                                                                         FirebaseAuth.getInstance().signOut();
                                                                         Intent intent = new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                         startActivity(intent);
                                                                         finish();
                                                                 }
                                                                 return true;
                                                             }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText(mAuth.getCurrentUser().getEmail().toString().trim());
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    private View.OnClickListener onAddingListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(WorkoutsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.add_new_item_dialog, null);
                final EditText userInputWorkoutName = (EditText) mView.findViewById(R.id.etItemName);
                Button ok_button = (Button) mView.findViewById(R.id.ok_button);
                mBuilder.setTitle("Add Workout: ");
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                ok_button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        String given_text = userInputWorkoutName.getText().toString();
                        if (!given_text.isEmpty()){

                            Workout workout = new Workout(given_text.trim());

                            //adding new object to arraylist
                            workouts.add(workout);

                            //notify data set changed in RecyclerView adapter
                            mRootRef.child("users").child(mUser.getUid()).child("workouts").child(given_text.trim()).setValue("melons");

                            mAdapter.notifyDataSetChanged();

                            //close dialog after all
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();

                        }
                    }
                });            }
        };
    }

    private void setRecyclerViewData() {
        // reads from firebase under workouts
        mWorkoutRef = mRootRef.child("users").child(mUser.getUid()).child("workouts");
        mWorkoutRef.addValueEventListener(new ValueEventListener() {
            @Override
            //dataSnapshot gives the value
            public void onDataChange(DataSnapshot dataSnapshot) {
                workouts.clear();
                Iterator i = dataSnapshot.getChildren().iterator();


                while(i.hasNext()){

                    workouts.add(new Workout(((DataSnapshot)i.next()).getKey()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //loops through the columns at the specific user ID and adds the workout
        //if(cursor.moveToFirst()) {
        //    while (!cursor.isAfterLast()) {
        //        workouts.add(new Workout(cursor.getString(0)));
        //        cursor.moveToNext();
        //    }
        //}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

















































/*
package com.csc301.project.iron;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


public class WorkoutsActivity extends AppCompatActivity {
    Context context;
    LinearLayout linearLayout;
    LinearLayout.LayoutParams layoutParams;
    private FloatingActionButton add_workout;
    private String m_text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        context = getApplicationContext();
        linearLayout = (LinearLayout) findViewById(R.id.activityWorkoutsLinearLayout);

        //Intent intent = new Intent(this, LoginActivity.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        add_workout = (FloatingActionButton) findViewById(R.id.add_workout_button);

        add_workout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addWorkout();
            }
        });

    }

    public void addWorkout(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WorkoutsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.add_new_item_dialog, null);
        final EditText userInputWorkoutName = (EditText) mView.findViewById(R.id.etWorkoutName);
        Button ok_button = (Button) mView.findViewById(R.id.ok_button);
        String given_text;


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        ok_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String given_text = userInputWorkoutName.getText().toString();
                if (!given_text.isEmpty()){
                    createWorkoutCardView(given_text);
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }




}
*/
