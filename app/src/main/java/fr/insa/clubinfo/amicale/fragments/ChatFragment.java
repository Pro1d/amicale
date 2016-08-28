package fr.insa.clubinfo.amicale.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.adapters.ChatMessageAdapter;
import fr.insa.clubinfo.amicale.helpers.Camera;
import fr.insa.clubinfo.amicale.helpers.DynamicDefaultPreferences;
import fr.insa.clubinfo.amicale.helpers.ImageBitmap;
import fr.insa.clubinfo.amicale.interfaces.ChatMessageListener;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import fr.insa.clubinfo.amicale.interfaces.OnPictureTakenListener;
import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;
import fr.insa.clubinfo.amicale.sync.ChatLoader;
import fr.insa.clubinfo.amicale.views.ImageViewer;
import fr.insa.clubinfo.amicale.views.SwitchImageViewAsyncLayout;

public class ChatFragment extends Fragment implements ChatMessageListener, OnPictureTakenListener, OnImageClickedListener {

    private static final int hiddenCountThresholdForScroll = 2;
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

    private Camera camera;
    private Bitmap currentPicture;
    private ProgressDialog sendingDialog;
    private UploadTask uploadTask;
    private AsyncTask<Bitmap, Void, byte[]> compressionTask;

    private Map<String, Object> ownActiveUserObject;
    private String ownActiveUserKey = null;
    private Map<String, Object> ownTypingIndicatorObject;
    private String ownTypingIndicatorKey = null;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader = new ChatLoader(this, Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        camera = new Camera(this);
        loader.loadMore(loadMoreCount, (double)System.currentTimeMillis() / 1000);
        loading = true;
        chat = new Chat();
        displayUserName = DynamicDefaultPreferences.getUserNameFromPreferences(getActivity());

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Notify firebase that a new user is active
        ownActiveUserKey = mDatabase.child("activeUsers").push().getKey();
        ownActiveUserObject = new HashMap<>();
        ownActiveUserObject.put("/activeUsers/" + ownActiveUserKey, displayUserName);
        mDatabase.updateChildren(ownActiveUserObject);

        // create typing indicator
        ownTypingIndicatorKey = mDatabase.child("typingIndicator").push().getKey();
        ownTypingIndicatorObject = new HashMap<>();
        ownTypingIndicatorObject.put("/typingIndicator/"+ownTypingIndicatorKey, false);
        mDatabase.updateChildren(ownTypingIndicatorObject);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loader.cancel();

        // Notify firebase that this user is not active anymore
        ownActiveUserObject.put("/activeUsers/"+ownActiveUserKey, null);
        mDatabase.updateChildren(ownActiveUserObject);
        ownTypingIndicatorObject.put("/typingIndicator/"+ownTypingIndicatorKey, null);
        mDatabase.updateChildren(ownTypingIndicatorObject);
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
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

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
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if((actionId&EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEND) {
                    sendInput();
                    return true;
                }
                return false;
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean savedTypingState = (Boolean) ownTypingIndicatorObject.get("/typingIndicator/"+ownTypingIndicatorKey);
                boolean currentTypingState = editable.length() > 0;

                if(savedTypingState != currentTypingState) {
                    ownTypingIndicatorObject.put("/typingIndicator/"+ownTypingIndicatorKey, currentTypingState);
                    mDatabase.updateChildren(ownTypingIndicatorObject);
                }
            }
        });
        ImageButton btnPhoto = (ImageButton) view.findViewById(R.id.chat_ib_photo);
        ImageButton btnClearImg = (ImageButton) view.findViewById(R.id.chat_ib_clear_picture);
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
        String key = mDatabase.child("messages").push().getKey();
        ChatMessage msg = new ChatMessage(key);
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
    private void sendImage(final Bitmap bitmap) {
        // First step, compress the bitmap to jpeg
        compressionTask = new AsyncTask<Bitmap, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(Bitmap... bitmaps) {
                try {
                    // Image has been already downsized in Camera helper
                    return ImageBitmap.compressAsJPEG(bitmaps[0], 50);
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(byte[] bytes) {
                compressionTask = null;
                onImageCompressed(bytes);
            }
            @Override
            protected void onCancelled() {
                compressionTask = null;
                onImageCompressed(null);
            }
        }.execute(bitmap);

        String content = getResources().getString(R.string.chat_dialog_sending_image_content);
        if(sendingDialog == null)
            sendingDialog = ProgressDialog.show(getActivity(), null, content, true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if(compressionTask != null) {
                        compressionTask.cancel(false);
                    }
                    if(uploadTask != null) {
                        uploadTask.cancel();
                    }
                }
            });
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
            uploadTask = imgRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    uploadTask = null;
                    onImageUploaded(null);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask = null;
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
            String key = mDatabase.child("messages").push().getKey();
            ChatMessage msg = new ChatMessage(key);
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
        if(currentPicture == null) {
            input.setText("");
        } else {
            cancelAndClearImageInput();
        }
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
