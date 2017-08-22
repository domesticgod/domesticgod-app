package uk.co.domesticgod.domesticgod.utils;

import android.os.Build;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.domesticgod.domesticgod.data.Post;

/**
 * Created by adammatt on 16/08/2017.
 */

public class DataUtils {

    public static Post[] parseJSONtoPosts(String json){
        Post[] posts = null;
        JSONArray allData;
        try {
            if(json==null)return new Post[0];
            allData = new JSONArray(json);
            int numPosts = allData.length();
            posts=new Post[numPosts];

            for (int i=0;i<allData.length();i++){
                JSONObject postJson =allData.getJSONObject(i);

                String title = postJson.getJSONObject("title").getString("rendered");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    title = Html.fromHtml(title, 0).toString();
                }

                String urlStr = allData.getJSONObject(i).getString("link");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    urlStr = Html.fromHtml(urlStr, 0).toString();
                }
                String content = postJson.getJSONObject("content").getString("rendered");

                String mediumAddress = null;
                try {
                    JSONObject embeddedJson = postJson.getJSONObject("_embedded");
                    JSONObject featuredMedia = embeddedJson.getJSONArray("wp:featuredmedia").getJSONObject(0);
                    mediumAddress = featuredMedia.getJSONObject("media_details").getJSONObject("sizes").getJSONObject("medium").getString("source_url");
                } catch (JSONException e) {
                    mediumAddress=null;
                }

                Log.i("Found post with title="+title+"\nLink="+urlStr,content+"\n Image URL="+mediumAddress);
                posts[i]=new Post(title,urlStr,content,mediumAddress);



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return posts;

    }
}
