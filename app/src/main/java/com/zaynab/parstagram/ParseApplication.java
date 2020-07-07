package com.zaynab.parstagram;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //register parseModels
        ParseObject.registerSubclass(Post.class);
        //setup parse server
        Log.i("URL", getString(R.string.server_URL));
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.App_Id))
                .clientKey(getString(R.string.MASTER_KEY))
                .server(getString(R.string.server_URL)).build());

    }
}
