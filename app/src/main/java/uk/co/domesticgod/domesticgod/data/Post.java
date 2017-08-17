package uk.co.domesticgod.domesticgod.data;

/**
 * Created by Matthew on 16/08/2017.
 */

public class Post {
    private String mTitle;

    public String getFeaturedImageAddress() {
        return mFeaturedImageAddress;
    }

    public void setmFeaturedImageAddress(String featuredImageAddress) {
        this.mFeaturedImageAddress = featuredImageAddress;
    }

    private String mFeaturedImageAddress;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    private String mContent;

    public void setAddress(String address) {
        this.mAddress = address;
    }

    private String mAddress;
    public String getAddress() {
        return mAddress;
    }

    public Post(String title,String address,String content,String imageUrl){
        mTitle=title;
        mAddress=address;
        mContent=content;
        mFeaturedImageAddress=imageUrl;
    }
    public void setTitle(String title){mTitle=title;}
    public String getTitle(){return mTitle;}


}
