package com.zaynab.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATEDAT = "createdAt";
    public static boolean likedByCurrentUser = false;

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public int getLikes(){return getInt("likes");}

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }
}
