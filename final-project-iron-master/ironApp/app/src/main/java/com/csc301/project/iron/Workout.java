package com.csc301.project.iron;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of exercise objects
 */

public class Workout {
    String name;
    List<Exercise> exercises;

    public Workout(String name){
        this.name = name;
        this.exercises = new ArrayList<>();
    }


    public void addExercise(Exercise e){
        this.exercises.add(e);
    }

    public String getName() {
        return name;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }


}
