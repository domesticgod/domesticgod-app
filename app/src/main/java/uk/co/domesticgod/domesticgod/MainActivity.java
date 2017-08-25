package uk.co.domesticgod.domesticgod;


import android.app.LoaderManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.domesticgod.domesticgod.data.Post;
import uk.co.domesticgod.domesticgod.data.PostsAsyncLoader;
import uk.co.domesticgod.domesticgod.image.ImageHelper;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>,
        ResultsFragment.OnFragmentInteractionListener,
        View.OnClickListener{
    private static final String TAG = "MainActivity";
    final static int POST_LOADER = 1;
    String mQuery=null;
    private ResultsFragment mResultsFragment;
    private FloatingActionButton mFab;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    private ImageView mPopupImageView;
    private VisionServiceClient client;
    // The URI of the image selected to detect.
    private Uri mImageUri;
    private String mImageDescription;
    List<String> mImageTags;
    private PopupWindow mImagePopup;
    private PopupWindow mAnalysisPopup;

    // The image selected to detect.
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResultsFragment=(ResultsFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_resulst_view);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        }
        getLoaderManager().initLoader(POST_LOADER,null,this);
        mFab = (FloatingActionButton)findViewById(R.id.fab_camera);
        mFab.setOnClickListener(this);

        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.microsoft_subscription_key),getString(R.string.microsoft_base_url));
        }

    }
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        }
        search();
    }

    private void search(){
        mResultsFragment.setData(null);
        getLoaderManager().restartLoader(POST_LOADER,null,this);
    }



    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Log.i(TAG,"Initialising Async Loader");
        return new PostsAsyncLoader(this,mQuery);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mResultsFragment.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    @Override
    public void onLaunchPost(Post post){
        Intent intent = new Intent(this, SinglePostActivity.class);
        intent.putExtra(getString(R.string.url_key),post.getContent());
        intent.putExtra(getString(R.string.image_src_key),post.getFeaturedImageAddress());
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuitem = menu.findItem(R.id.searchview);
        SearchView searchView = (SearchView) menuitem.getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        return true;
    }

    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }

    private void launchImagePopup(){
        // inflate the layout of the image_popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.image_popup, null);
        CoordinatorLayout mainLayout = (CoordinatorLayout)findViewById(R.id.coo_main_root);
        // create the image_popup window
        int width = FrameLayout.LayoutParams.MATCH_PARENT;
        int height = FrameLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the image_popup also dismiss it
        mImagePopup = new PopupWindow(popupView, width, height, focusable);
        mPopupImageView = (ImageView)popupView.findViewById(R.id.iv_popup);

        // show the image_popup window
        mImagePopup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

        // dismiss the image_popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mImagePopup.dismiss();
                return true;
            }
        });
        Toast analysisToast = Toast.makeText(this,"Analysing Image",Toast.LENGTH_LONG);
        analysisToast.show();
    }
    private void launchAnalysisPopup(){
        // inflate the layout of the image_popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.analysis_result_popup, null);
        LinearLayoutCompat mainLayout = (LinearLayoutCompat) popupView.findViewById(R.id.analysis_result_root);
        // create the analysis result window
        int width = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
        int height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // true lets taps outside the image_popup also dismiss it
        mAnalysisPopup = new PopupWindow(popupView, width, height, focusable);
        Button cancelButton = (Button)popupView.findViewById(R.id.bt_analysis_cancel);
        Button searchButton = (Button)popupView.findViewById(R.id.bt_analysis_search);
        TextView descriptionTextView = (TextView) popupView.findViewById(R.id.tv_analysis_description);
        TextView tagsTextView = (TextView) popupView.findViewById(R.id.tv_analysis_tags);

        descriptionTextView.setText("Description = "+mImageDescription);
        tagsTextView.setText("Tags = "+mImageTags.toString());
        mAnalysisPopup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);


        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mImagePopup!=null){
                    mImagePopup.dismiss();
                }
                mAnalysisPopup.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO add search
                if(mImagePopup!=null){
                    mImagePopup.dismiss();
                }
                mAnalysisPopup.dismiss();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                launchImagePopup();
                mPopupImageView.setImageURI(mImageUri);
                mPopupImageView.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG,"Attempting to load bitmap from :"+mImageUri.toString());
            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    mImageUri, getContentResolver());
            if (mBitmap != null) {

                // Add detection log.
                Log.d("DescribeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                        + "x" + mBitmap.getHeight());

                doImageDescribe();
            }
        }
    }
    public void doImageDescribe() {
        Toast describingToast = Toast.makeText(this,"Analysing image",Toast.LENGTH_LONG);
        describingToast.show();

        try {
            new doImageAnalysisRequest().execute();
        } catch (Exception e)
        {
            Toast errorToast = Toast.makeText(this,"Error encountered. Exception is: " + e.toString(),Toast.LENGTH_LONG);
            errorToast.show();
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if(mCurrentPhotoPath!=null) {
            this.deleteFile(mCurrentPhotoPath);
            mCurrentPhotoPath=null;
        }
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "uk.co.domesticgod.fileprovider",
                        photoFile);
                mImageUri=photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCurrentPhotoPath!=null) {
            this.deleteFile(mCurrentPhotoPath);
            mCurrentPhotoPath=null;
        }
    }

    private class doImageAnalysisRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doImageAnalysisRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return processImageAnalysisRequest();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                mImageDescription = result.description.captions.get(0).text;
                mImageTags = result.description.tags;


                launchAnalysisPopup();

            }

        }
    }
    private String processImageAnalysisRequest() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.describe(inputStream, 1);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }
}

