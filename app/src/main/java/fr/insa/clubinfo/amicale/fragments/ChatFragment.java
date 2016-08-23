package fr.insa.clubinfo.amicale.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.adapters.ChatMessageAdapter;
import fr.insa.clubinfo.amicale.helpers.Camera;
import fr.insa.clubinfo.amicale.interfaces.ChatMessageListener;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import fr.insa.clubinfo.amicale.interfaces.OnPictureTakenListener;
import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;
import fr.insa.clubinfo.amicale.sync.ChatLoader;
import fr.insa.clubinfo.amicale.views.ImageViewer;
import fr.insa.clubinfo.amicale.views.SwitchImageViewAsyncLayout;

public class ChatFragment extends Fragment implements ChatMessageListener, OnPictureTakenListener, OnImageClickedListener {

    private static final int hiddenCountThresholdForScroll = 3;
    private static final int visibleThreshold = 10, loadMoreCount = 30;

    private String displayUserName;
    private Chat chat;

    private ChatLoader loader;
    private ChatMessageAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private boolean loading;

    private EditText input;
    private ViewGroup picturePreviewGroup;
    private ViewGroup textInputGroup;
    private SwitchImageViewAsyncLayout switchImgAsync;
    private ImageButton btnPhoto;
    private ImageButton btnClearImg;

    private Camera camera;
    private Bitmap currentPicture;
    private ProgressDialog sendingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO check authentication
        loader = new ChatLoader(this, Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        camera = new Camera(this);
        loader.loadMore(loadMoreCount, (double)System.currentTimeMillis() / 1000);
        loading = true;
        chat = new Chat();
        displayUserName = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getResources().getString(R.string.prefs_chat_nickname_key), getResources().getString(R.string.prefs_chat_nickname_default_value));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loader.cancel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_chat);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Recycler view
        adapter = new ChatMessageAdapter(chat, this);
        adapter.setShowLoadingView(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.chat_rv_list);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                Log.i("###", "onScrolled visibleItemCount:"+visibleItemCount+" totalItemCount:"+totalItemCount+" firstVisibleItem:"+firstVisibleItem);
                if(!loading) {
                    if(firstVisibleItem < visibleThreshold) {
                        loading = true;
                        loader.loadMore(loadMoreCount, chat.getOldestTimestamp()-0.001);
                    }
            }
            }
        });

        // Input
        input = (EditText) view.findViewById(R.id.chat_et_input);
        btnPhoto = (ImageButton) view.findViewById(R.id.chat_ib_photo);
        btnClearImg = (ImageButton) view.findViewById(R.id.chat_ib_clear_picture);
        ImageButton btnSend = (ImageButton) view.findViewById(R.id.chat_ib_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInput();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAndAttachPicture();
            }
        });
        btnClearImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAndClearImageInput();
            }
        });
        picturePreviewGroup = (ViewGroup) view.findViewById(R.id.chat_ll_mode_image_preview);
        textInputGroup = (ViewGroup) view.findViewById(R.id.chat_ll_mode_text_input);
        switchImgAsync = (SwitchImageViewAsyncLayout) view.findViewById(R.id.chat_sl_picture_async);

        return view;
    }

    private void sendInput() {
        if(currentPicture != null) {
            sendImage(currentPicture);
        }
        else {
            String text = input.getText().toString();
            // Remove first and last white chars.
            text = text.replaceAll("^\\s+|\\s+$", "");
            // Replace multiple end line by one '\n', and remove white chars at beginning or end of a line
            text = text.replaceAll("\\s*\n\\s*", "\n");
            // Replace white chars in a row by one ' '
            text = text.replaceAll("[ \t]+", " ");
            if(!text.isEmpty()) {
                sendText(text);
            }

            clearInputs();
        }
    }

    private void sendText(String text) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child("messages").push().getKey();
        ChatMessage msg = new ChatMessage(key);
        msg.setDate(new GregorianCalendar());
        msg.setTimestamp((double) System.currentTimeMillis()/1000);
        msg.setContent(text);
        msg.setOwn(true);
        msg.setSenderId(Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        msg.setSenderName(displayUserName);

        Map<String, Object> postValues = msg.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/messages/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    /**
     * Send an image message in 3 steps.
     * Step 1 of 3.
     */
    private void sendImage(Bitmap bitmap) {
        // First step, compress the bitmap to jpeg
        new AsyncTask<Bitmap, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(Bitmap... bitmaps) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    return baos.toByteArray();
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(byte[] bytes) {
                onImageCompressed(bytes);
            }
        }.execute(bitmap);

        String content = getResources().getString(R.string.chat_dialog_sending_image_content);
        if(sendingDialog == null)
            sendingDialog = ProgressDialog.show(getActivity(), null, content, true, false);
        else
            sendingDialog.show();
    }


    /**
     * Send an image message in 3 steps.
     * Step 2 of 3.
     */
    private void onImageCompressed(byte[] data) {
        if(data == null) {
            onImageUploaded(null);
        }
        else {
            // Second step, upload the image
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://project-4508461957841032139.appspot.com");
            StorageReference imgRef = storageRef.child("chat/" + displayUserName.replace("[^a-zA-Z0-9_ .+\\-]","_") + "-" + Long.toHexString(System.currentTimeMillis()) + ".jpg");
            UploadTask uploadTask = imgRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    onImageUploaded(null);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    onImageUploaded(downloadUrl.toString());
                }
            });
        }
    }


    /**
     * Send an image message in 3 steps.
     * Step 3 of 3.
     */
    private void onImageUploaded(String imageURL) {
        if(imageURL != null) {
            // Third step, create an message object
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String key = mDatabase.child("messages").push().getKey();
            ChatMessage msg = new ChatMessage(key);
            msg.setDate(new GregorianCalendar());
            msg.setTimestamp((double) System.currentTimeMillis() / 1000);
            msg.setImageURL(imageURL);
            msg.setOwn(true);
            msg.setSenderId(Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
            msg.setSenderName(displayUserName);

            Map<String, Object> postValues = msg.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/messages/" + key, postValues);

            mDatabase.updateChildren(childUpdates);
        }
        else {
            Toast.makeText(getActivity(), R.string.chat_dialog_on_send_image_failed, Toast.LENGTH_SHORT).show();
        }
        sendingDialog.cancel();
        clearInputs();
    }

    private void takeAndAttachPicture() {
        camera.startCameraIntent(this);
    }

    private void clearInputs() {
        input.setText("");
        cancelAndClearImageInput();
    }
    private void cancelAndClearImageInput() {
        camera.cancel();
        currentPicture = null;
        picturePreviewGroup.setVisibility(View.GONE);
        textInputGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPictureTaken() {
        // Waiting for image loading, display progress view
        picturePreviewGroup.setVisibility(View.VISIBLE);
        textInputGroup.setVisibility(View.GONE);
        switchImgAsync.showProgressView();
    }

    @Override
    public void onPictureLoaded(Bitmap drawable) {
        if(picturePreviewGroup.getVisibility() == View.VISIBLE) {
            // Getting image, save and display
            switchImgAsync.showImageView(drawable);
            currentPicture = drawable;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        camera.onActivityResult(requestCode, resultCode);
    }

    @Override
    public void onImageClicked(int index) {
        ImageViewer.getImageViewer().show(chat, chat.getImagePosition(index));
    }

    @Override
    public boolean onBackPressed() {
        ImageViewer imageViewer = ImageViewer.getImageViewer();
        if (imageViewer.isVisible()) {
            imageViewer.hide();
            return true;
        }
        else
            return false;
    }

    @Override
    public void onMoreChatMessagesLoaded(List<ChatMessage> m) {
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        boolean needScroll = (totalItemCount == visibleItemCount);

        // Hide the progress bar if we reach the end
        if(m.size() < loadMoreCount && adapter.getShowLoadingView()) {
            adapter.setShowLoadingView(false);
            adapter.notifyItemRemoved(0);
        }

        loading = false;
        chat.addMessagesToBack(m);
        adapter.notifyItemRangeInserted(adapter.getPosition(0), m.size());
        Log.i("###", "onMoreChatMessagesLoaded "+m.size());

        // to scroll or not to scroll, that is the question
        if(needScroll)
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void onNewChatMessageReceived(ChatMessage msg) {
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        boolean needScroll = (totalItemCount - firstVisibleItem - visibleItemCount <= hiddenCountThresholdForScroll);

        chat.addMessage(msg);

        adapter.notifyItemInserted(adapter.getPosition(chat.getMessagesCount()-1));

        // to scroll or not to scroll, that is the question
        if(needScroll)
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void onImageLoaded(ChatMessage msg) {
        int idx = chat.getIndex(msg.getFirebaseKey());
        if(idx != -1)
            adapter.notifyItemChanged(adapter.getPosition(idx));
    }
}
