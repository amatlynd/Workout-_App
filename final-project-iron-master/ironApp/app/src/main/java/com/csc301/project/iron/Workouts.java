package com.csc301.project.iron;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
Stores a collection of Workout objects
 */

public class Workouts {
    public Set<Workout> workouts_list;

    public Workouts(){
        this.workouts_list = new HashSet<>();
    }
}
