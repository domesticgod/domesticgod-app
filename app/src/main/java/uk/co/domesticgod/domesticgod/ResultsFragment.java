package uk.co.domesticgod.domesticgod;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.domesticgod.domesticgod.data.Post;

import static android.view.View.INVISIBLE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment implements
        View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PostsAdaptor mPostsAdaptor;
    private RecyclerView mPostsRecyclerView;
    static final String TAG = "ResultsViewFragment";
    private ProgressBar mProgressBar;
    static final int ERR_NO_INTERNET=1;
    private TextView mErrorTextView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public void setData(String data){
        mProgressBar.setVisibility(INVISIBLE);
        mPostsAdaptor.setData(data);
        if(data==null){
            //TODO add handling of errors in loader to pass through the exception
            //At the moment, no results means no internet
            setError(ERR_NO_INTERNET);
        }
    }

    public void setError(int error){
        switch(error){
            case ERR_NO_INTERNET:{
                mErrorTextView.setText(getString(R.string.err_no_internet));
                mErrorTextView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(INVISIBLE);
                break;
            }default:{
                throw new UnsupportedOperationException("Error "+Integer.toString(error)+" is not recognised");
            }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPostsRecyclerView = (RecyclerView) getView().findViewById(R.id.posts_recyclerview);

        mPostsAdaptor = new PostsAdaptor(this);
        mPostsRecyclerView.setAdapter(mPostsAdaptor);
        mPostsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progresbar_results);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorTextView  = (TextView)getView().findViewById(R.id.tv_results_error);
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
        void onLaunchPost(Post post);
    }
    @Override
    public void onClick(View v) {
        int position = (int)v.getTag(R.string.position_key);
        if (mListener != null) {
            Post post = mPostsAdaptor.getItem(position);
            mListener.onLaunchPost(post);
        }
    }
}
