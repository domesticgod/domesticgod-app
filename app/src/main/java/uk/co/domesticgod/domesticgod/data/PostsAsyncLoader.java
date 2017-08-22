package uk.co.domesticgod.domesticgod.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import uk.co.domesticgod.domesticgod.utils.NetworkUtils;



public class PostsAsyncLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "PostsAsyncLoader";
    String mDataStr;
    String mQuery=null;

    public PostsAsyncLoader(Context context,String query) {
        super(context);
        mQuery=query;
        Log.v(TAG,"PostsAsyncLoader constructor called");

    }

    @Override
    protected void onStartLoading() {
        Log.v(TAG,"onStartLoading called");
        if(mDataStr != null){
            Log.v(TAG,"Loader already has data");
            deliverResult(mDataStr);
        }else {
            Log.v(TAG,"Loader doesn't have data so loading now");
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        Log.v(TAG,"Generating url to retrieve posts query="+mQuery);
        
        URL postsUrl = NetworkUtils.buildUrl(mQuery);
        Log.i(TAG,"Calling url to get posts:"+ postsUrl.toString());
        try {
            mDataStr = NetworkUtils.getResponseFromHttpUrl(postsUrl);
        } catch (IOException e) {
            mDataStr=null;
            e.printStackTrace();
        }
        Log.v(TAG,"Got response:"+mDataStr);
        return mDataStr;
    }


}
