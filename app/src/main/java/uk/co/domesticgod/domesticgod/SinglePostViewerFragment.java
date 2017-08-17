package uk.co.domesticgod.domesticgod;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import static uk.co.domesticgod.domesticgod.SinglePostActivity.URL_KEY;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SinglePostViewerFragment.OnFragmentInteractionListener} interface

 * create an instance of this fragment.
 */
public class SinglePostViewerFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM_URL = "url";
    static final String TAG = "PostViewerFragment";
    // TODO: Rename and change types of parameters
    private String mUrl;
    private WebView mWebView;
    private String mImageSource=null;
    private ImageView mBannerImageView;

    private OnFragmentInteractionListener mListener;

    public SinglePostViewerFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle==null || !bundle.containsKey(getString(R.string.url_key))) {
            bundle = getActivity().getIntent().getExtras();
            if (!bundle.containsKey(getString(R.string.url_key)))
                throw new UnsupportedOperationException("This fragment requires a url");
        }
        mUrl=bundle.getString(getString(R.string.url_key));
        if(bundle.containsKey(getString(R.string.image_src_key))){
            mImageSource = bundle.getString(getString(R.string.image_src_key));
        }
        Log.v(TAG,"Found url to load:"+mUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_single_post_viewer, container, false);
        mWebView = (WebView)view.findViewById(R.id.wv_single_post);
        Log.i(TAG,"Loading url in to WebView :"+mUrl);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.loadData(mUrl,"text/html","UTF-8");
        mBannerImageView = (ImageView)view.findViewById(R.id.iv_toolbar_image);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mBannerImageView!=null){
            Glide.with(view.getContext()).load(mImageSource)
                    .into(mBannerImageView);
            Log.i(TAG,"Loading image in to banner:"+mImageSource);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
