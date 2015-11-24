package com.group9.cse110project;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "yzR4iindMc1Kaz1yNWLbVTl1eMBL3xyFJE67Pl5J", "FAN31bfiwFbz2BPVUXYZfBPitGkwhLsVfWkdG3d3");
    }
}