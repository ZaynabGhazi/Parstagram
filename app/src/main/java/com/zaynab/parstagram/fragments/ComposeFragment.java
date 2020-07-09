package com.zaynab.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.parstagram.MainActivity;
import com.zaynab.parstagram.Post;
import com.zaynab.parstagram.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment {
    public static final String TAG = "COMPOSE FRAGMENT";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int MAX_WIDTH = 600;
    public static final int MAX_HEIGHT = 600;
    private ImageView ivPostImage;
    private EditText etDesctiption;
    private Button btnCapture;
    private Button btnSubmit;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    public ComposeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bindView(view);
        make_capture();
        make_submit();
    }

    private void make_submit() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = etDesctiption.getText().toString();
                if (desc.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUsr = ParseUser.getCurrentUser();
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "There is no image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePost(desc, currentUsr, photoFile);
                //redirect to timeline
                /*PostsFragment postsFragment = new PostsFragment();
                getFragmentManager().beginTransaction().replace(R.id.flContainer, postsFragment).commit();*/
            }
        });
    }

    private void make_capture() {
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    private void bindView(View view) {
        ivPostImage = view.findViewById(R.id.ivPostImage);
        etDesctiption = view.findViewById(R.id.etDescription);
        btnCapture = view.findViewById(R.id.btnCaptureImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);
    }


    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void savePost(String desc, ParseUser currentUsr, File photoFile) {
        Post post = new Post();
        post.setDescription(desc);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUsr);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving post.", e);
                    Toast.makeText(getContext(), "Error while saving post", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post saved successfully!");
                etDesctiption.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }

    //returns a file for photo stored on disk given filename
    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Resizing bitmap : 1600 pixels -> ensure Memory < 10 MB given default depth of pixels
                Bitmap resized = scaleToFill(takenImage, MAX_WIDTH, MAX_HEIGHT);
                //ivPostImage.setImageBitmap(takenImage);
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(resized);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap scaleToFill(Bitmap b, int width, int height) {
        float factorH = height / (float) b.getWidth();
        float factorW = width / (float) b.getWidth();
        float factorToUse = (factorH > factorW) ? factorW : factorH;
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse),
                (int) (b.getHeight() * factorToUse), true);
    }

}