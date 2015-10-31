package com.digital.strategy.followup;

import com.firebase.client.Firebase;
import com.firebase.client.core.Path;
import com.firebase.client.core.Repo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DS on 10/31/15.
 */
public class Comments extends Firebase {

    public static final String comments_key = "comments";

    public Comments(String url) {
        super(url);
    }

    public Comments(Repo repo, Path path) {
        super(repo, path);
    }

    public void addComment(String comment) {

        Map<String,String> comment_string = new HashMap<String, String>();

        comment_string.put(comments_key, comment);
        //sampledata.push().setValue(commentone);

    }
}
