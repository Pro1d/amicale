package fr.insa.clubinfo.amicale.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.interfaces.OnPictureTakenListener;

/**
 * Created by Proïd on 28/08/2016.
 */

public class ImagePicker {
    private final File file = new File(Environment.getExternalStorageDirectory(),  ".amicale_chat_picture.jpg");
    private static final int PICK_IMAGE_REQUEST_CODE = 10;
    private static final int MULTI_SOURCE_PICK_IMAGE_REQUEST_CODE = 11;
    private AsyncTask<String, Void, Bitmap> currentTask;
    private final OnPictureTakenListener listener;

    public ImagePicker(OnPictureTakenListener listener) {
        this.listener = listener;
    }

    public void startPickerIntent(Fragment fragment, Intent cameraIntent) {
        //This allows to select the application to use when selecting an image.
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        String title = fragment.getResources().getString(R.string.image_picker_title);
        Intent chooser = Intent.createChooser(i, title);
        if(cameraIntent != null) {
            List<Intent> cameraIntents = new ArrayList<>(1);
            cameraIntents.add(cameraIntent);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
            fragment.startActivityForResult(chooser, MULTI_SOURCE_PICK_IMAGE_REQUEST_CODE);
        }
        else {
            fragment.startActivityForResult(chooser, PICK_IMAGE_REQUEST_CODE);
        }
    }

    public int onActivityResult(int requestCode, int resultCode, Intent intent, Context context) {
        // Only image picker intent
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
                loadImageAsync(intent.getData(), context);
            return resultCode;
        }
        // Image picker or camera intent
        else if (requestCode == MULTI_SOURCE_PICK_IMAGE_REQUEST_CODE) {
            if(intent == null) {
                return Camera.TAKE_PICTURE_REQUEST_CODE;
            }
            else {
                if (resultCode == Activity.RESULT_OK)
                    loadImageAsync(intent.getData(), context);
                return resultCode;
            }
        }
        else {
            return resultCode;
        }
    }

    private void loadImageAsync(Uri imageUri, Context context) {
        if (imageUri != null){
            try {
                //We get the file path from the media info returned by the content resolver
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                currentTask = new AsyncTask<String, Void, Bitmap>() {

                    @Override
                    protected void onPreExecute() {
                        listener.onPictureTaken();
                    }

                    @Override
                    protected Bitmap doInBackground(String... params) {
                        int width = 1000;
                        int height = 1000;
                        if(ImageBitmap.getScreenWidth() < 1000) {
                            width = (int) (ImageBitmap.getScreenWidth() * 0.9);
                            height = 0;
                        }

                        return ImageBitmap.decodeSampledBitmapFromFile(params[0], width, height);
                    }

                    @Override
                    protected void onPostExecute(Bitmap drawable) {
                        listener.onPictureLoaded(drawable);
                    }

                    @Override
                    protected void onCancelled() {
                        listener.onPictureLoaded(null);
                    }
                }.execute(filePath);

            }catch(Exception e){
                Toast.makeText(context, R.string.image_picker_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cancel() {
        if(currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }

        file.delete();
    }
}
