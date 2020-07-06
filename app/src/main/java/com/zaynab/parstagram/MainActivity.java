package com.zaynab.parstagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42 ;
    private ImageView ivPostImage;
    private EditText etDesctiption;
    private Button btnCapture;
    private Button btnSubmit;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivPostImage = findViewById(R.id.ivPostImage);
        etDesctiption = findViewById(R.id.etDescription);
        btnCapture = findViewById(R.id.btnCaptureImage);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        
        //queryPosts();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = etDesctiption.getText().toString();
                if (desc.isEmpty()) {
                    Toast.makeText(MainActivity.this,"Description cannot be empty.",Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUsr = ParseUser.getCurrentUser();
                if (photoFile == null || ivPostImage.getDrawable() == null){
                    Toast.makeText(MainActivity.this,"There is no image.",Toast.LENGTH_SHORT).show();
                    return;
                }
                savePost(desc,currentUsr,photoFile);
            }
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //returns a file for photo stored on disk given filename
    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    private void savePost(String desc, ParseUser currentUsr, File photoFile) {
        Post post = new Post();
        post.setDescription(desc);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUsr);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG,"Error while saving post.",e);
                    Toast.makeText(MainActivity.this,"Error while saving post",Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG,"Post saved successfully!");
                etDesctiption.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e!=null){
                    Log.e(TAG,"Issue with getting posts.");
                    return;
                }
                for(Post post: posts){
                    Log.i(TAG,"Post: "+post.getDescription()+", username: "+post.getUser().getUsername());
                }
            }
        });

    }
}