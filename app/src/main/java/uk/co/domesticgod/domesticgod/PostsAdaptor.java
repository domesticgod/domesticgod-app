package uk.co.domesticgod.domesticgod;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import uk.co.domesticgod.domesticgod.data.Post;
import uk.co.domesticgod.domesticgod.utils.DataUtils;

/**
 * Created by Matthew on 15/08/2017.
 */

public class PostsAdaptor extends RecyclerView.Adapter<PostsAdaptor.PostsViewHolder> {

    private String mData;
    Post[] mPosts;
    Context mContext;

    View.OnClickListener mListener;

    public PostsAdaptor(View.OnClickListener listener){
        mListener=listener;
    }

    public void setData(String data){
        mData=data;
        if(mData!=null) {
            mPosts = DataUtils.parseJSONtoPosts(data);
            notifyDataSetChanged();
        }else{


        }
    }

    @Override
    public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater.from(mContext));
        View view = inflater.inflate(R.layout.post,parent,false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        holder.setTitle(mPosts[position].getTitle());
        if(mPosts[position].getFeaturedImageAddress()!=null) {
            Glide.with(holder.itemView.getContext())
                    .load(mPosts[position].getFeaturedImageAddress())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.mImageView);
        }

        holder.itemView.setTag(R.string.position_key,position);
        holder.itemView.setOnClickListener(mListener);
    }

    @Override
    public int getItemCount() {
        if(mData == null) {
            return 0;
        }else{
            return mPosts.length;
        }
    }

    class PostsViewHolder extends RecyclerView.ViewHolder{
        TextView mTitleTextView;
        ImageView mImageView;
        public PostsViewHolder(View view){
            super(view);
            this.mImageView = (ImageView) view.findViewById(R.id.iv_post_featured);
            this.mTitleTextView = (TextView) view.findViewById(R.id.tv_post_title);
        }

        public void setTitle(String title){
            mTitleTextView.setText(title);
        }

    }
    public Post getItem(int position){
        return mPosts[position];
    }
}

