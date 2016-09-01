package fr.insa.clubinfo.amicale.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private final File file = new File(Environment.getExternalStorageDirectory(),  ".amicale_chat_picture.jpg");
    public static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private AsyncTask<File, Void, Bitmap> currentTask;
    private final OnPictureTakenListener listener;

    public Camera(OnPictureTakenListener listener) {
        this.listener = listener;
    }

    public void startCameraIntent(Fragment fragment) {
        Intent intent = getCameraIntent();
        fragment.startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
    }

    public Intent getCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        return intent;
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
                loadImageAsync();
        }
    }

    public boolean fileExist() {
        return file.exists();
    }

    private void deleteImageFile() {
        file.delete();
    }

    public void loadImageAsync() {
        currentTask = new AsyncTask<File, Void, Bitmap>() {

            @Override
            protected void onPreExecute() {
                listener.onPictureTaken();
            }

            @Override
            protected Bitmap doInBackground(File... params) {
                File file = params[0];
                int width = 1000;
                int height = 1000;
                Bitmap bmp = ImageBitmap.decodeSampledBitmapFromFile(file.getPath(), width, height);
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap drawable) {
                listener.onPictureLoaded(drawable);
                deleteImageFile();
            }

            @Override
            protected void onCancelled() {
                listener.onPictureLoaded(null);
                deleteImageFile();
            }
        }.execute(file);
    }


    public void cancel() {
        if(currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }
        deleteImageFile();
    }
}
