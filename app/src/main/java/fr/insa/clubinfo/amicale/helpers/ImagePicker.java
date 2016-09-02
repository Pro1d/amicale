package fr.insa.clubinfo.amicale.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.interfaces.OnPictureTakenListener;

/**
 * Created by Proïd on 28/08/2016.
 */

public class ImagePicker {
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
            if (resultCode == Activity.RESULT_OK && intent != null)
                loadImageAsync(intent.getData(), context);
            return resultCode;
        }
        // Image picker or camera intent
        else if (requestCode == MULTI_SOURCE_PICK_IMAGE_REQUEST_CODE) {
            boolean imagePicked = false;
            if(intent != null && resultCode == Activity.RESULT_OK) {
                imagePicked = loadImageAsync(intent.getData(), context);
            }
            if(!imagePicked)
                return Camera.TAKE_PICTURE_REQUEST_CODE;
        }

        return resultCode;
    }

    private boolean loadImageAsync(Uri imageUri, Context context) {
        if (imageUri != null){
            String filePath = getFilePath(imageUri, context);
            if(filePath != null) {
                currentTask = new AsyncTask<String, Void, Bitmap>() {

                    @Override
                    protected void onPreExecute() {
                        listener.onPictureTaken();
                    }

                    @Override
                    protected Bitmap doInBackground(String... params) {
                        int width = 1000;
                        int height = 1000;

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

                return true;
            }
        }
        return false;
    }

    private String getFilePath(Uri imageUri, Context context) {
        String filePath = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isMediaDocument(imageUri))
        {
            String wholeID = DocumentsContract.getDocumentId(imageUri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        else {
            try {
                //We get the file path from the media info returned by the content resolver
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
            } catch (Exception e) {
                filePath = imageUri.getPath();
            }
        }
        return filePath;
    }

    // Fix for android >= Lollipop
    private static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void cancel() {
        if(currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }
    }
}
