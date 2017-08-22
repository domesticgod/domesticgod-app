package uk.co.domesticgod.domesticgod;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SinglePostActivity extends AppCompatActivity {
    static final String URL_KEY="URL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(getString(R.string.image_src_key));
        ImageView iv = (ImageView)findViewById(R.id.iv_toolbar_image);
        if(imageUrl!=null) {
            Glide.with(this).load(imageUrl)
                    .into(iv);
        }


    }
}

