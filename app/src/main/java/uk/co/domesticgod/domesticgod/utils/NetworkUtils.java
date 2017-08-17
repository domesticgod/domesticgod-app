package uk.co.domesticgod.domesticgod.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Matthew on 15/08/2017.
 */

public class NetworkUtils {
    final static String DG_BASE_URL = "http://www.domesticgod.co.uk";
    final static Uri DG_BASE_URI = Uri.parse(DG_BASE_URL);
    final static String DG_POSTS_PATH = "wp-json/wp/v2/posts?_embed";

    public static URL buildUrl(){
        Uri builtUri = DG_BASE_URI.buildUpon()
                .appendEncodedPath(DG_POSTS_PATH)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
            //url = new URL("http://domesticgod.co.uk/wp-json/wp/v2/posts");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
