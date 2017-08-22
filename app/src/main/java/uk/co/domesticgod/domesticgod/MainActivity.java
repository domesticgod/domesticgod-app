package uk.co.domesticgod.domesticgod;


import android.app.LoaderManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import uk.co.domesticgod.domesticgod.data.Post;
import uk.co.domesticgod.domesticgod.data.PostsAsyncLoader;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>,
ResultsFragment.OnFragmentInteractionListener{
    private static final String TAG = "MainActivity";
    final static int POST_LOADER = 1;
    String mQuery=null;
    private ResultsFragment mResultsFragment;

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

    }
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        }
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
}
