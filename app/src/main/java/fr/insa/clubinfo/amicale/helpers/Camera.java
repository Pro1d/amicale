package fr.insa.clubinfo.amicale.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

import fr.insa.clubinfo.amicale.interfaces.OnPictureTakenListener;

/**
 * Created by Proïd on 06/06/2016.
 */

public class Camera {
    private final File file = new File(Environment.getExternalStorageDirectory(),  "fr_insa_clubinfo_amicale_helpers_picture.jpg");
    private static final int TAKE_PICTURE = 1;
    private AsyncTask<File, Void, Drawable> currentTask;
    private final OnPictureTakenListener listener;

    public Camera(OnPictureTakenListener listener) {
        this.listener = listener;
        deleteImageFile();
    }

    public void startCameraIntent(Fragment fragment) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        fragment.startActivityForResult(intent, TAKE_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK)
                loadImageAsync();
        }
    }

    private void deleteImageFile() {
        file.delete();
    }
    public File getImageFile() {
        return file;
    }

    private void loadImageAsync() {
        currentTask = new AsyncTask<File, Void, Drawable>() {

            @Override
            protected void onPreExecute() {
                listener.onPictureTaken();
            }

            @Override
            protected Drawable doInBackground(File... params) {
                File file = params[0];

                return Drawable.createFromPath(file.getPath());
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                listener.onPictureLoaded(drawable);
            }

            @Override
            protected void onCancelled(Drawable drawable) {
                listener.onPictureLoaded(null);
            }
        }.execute(file);
    }


    public void cancel() {
        if(currentTask != null)
            currentTask.cancel(true);
        deleteImageFile();
    }
}
