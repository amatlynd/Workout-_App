package com.csc301.project.iron;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;




/**
 * Created by armanghassemi on 2017-03-11.
 */

public class InitializationManager extends android.app.Application {



    @Override
    public void onCreate() {

        super.onCreate();

        Firebase.setAndroidContext(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(this);


    }

}
