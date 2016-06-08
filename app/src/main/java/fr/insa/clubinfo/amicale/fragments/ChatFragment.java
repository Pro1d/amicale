package fr.insa.clubinfo.amicale.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.adapters.ChatMessageAdapter;
import fr.insa.clubinfo.amicale.helpers.Camera;
import fr.insa.clubinfo.amicale.interfaces.ChatMessageListener;
import fr.insa.clubinfo.amicale.interfaces.OnPictureTakenListener;
import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;
import fr.insa.clubinfo.amicale.sync.ChatLoader;
import fr.insa.clubinfo.amicale.views.SwitchImageViewAsyncLayout;

public class ChatFragment extends Fragment implements ChatMessageListener, OnPictureTakenListener {
    private Chat chat;
    private ChatLoader loader;
    private ChatMessageAdapter adapter;
    private RecyclerView recyclerView;


    private EditText input;
    private ViewGroup picturePreviewGroup;
    private SwitchImageViewAsyncLayout switchImgAsync;

    private Camera camera;
    private Drawable currentPicture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new ChatLoader(this);
        camera = new Camera(this);
        loader.loadAsync();
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
        adapter = new ChatMessageAdapter(chat);
        recyclerView = (RecyclerView) view.findViewById(R.id.chat_rv_list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Input
        input = (EditText) view.findViewById(R.id.chat_et_input);
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
        picturePreviewGroup = (ViewGroup) view.findViewById(R.id.chat_ll_input_picture);
        switchImgAsync = (SwitchImageViewAsyncLayout) view.findViewById(R.id.chat_sl_picture_async);

        return view;
    }

    private void sendInput() {
        String text = input.getText().toString();
        if(!text.isEmpty() || currentPicture != null) {
            // TODO send(text)
            // TODO remove the following lines
            ChatMessage m = new ChatMessage();
            m.setSelf(true);
            m.setContent(text);
            m.setImage(currentPicture);
            chat.addMessage(m);
            onNewChatMessageReceived(chat, m);
        }
        clearInputs();
    }

    private void takeAndAttachPicture() {
        camera.startCameraIntent(this);
    }

    @Override
    public void onChatLoaded(Chat chat) {
        this.chat = chat;
        adapter.update(chat);
    }

    @Override
    public void onChatSyncFailed() {
        chat = new Chat();
        adapter.update(chat);
        Toast.makeText(getActivity(), R.string.loading_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewChatMessageReceived(Chat chat, ChatMessage msg) {
        // chat.addMessage(msg);
        this.chat = chat;
        adapter.update(chat);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
        // TODO update last item only
    }

    private void clearInputs() {
        input.setText("");
        cancelAndClearImageInput();
    }
    private void cancelAndClearImageInput() {
        camera.cancel();
        currentPicture = null;
        picturePreviewGroup.setVisibility(View.GONE);
        switchImgAsync.hideAll();
    }

    @Override
    public void onPictureTaken() {
        // Waiting for image loading, display progress view
        picturePreviewGroup.setVisibility(View.VISIBLE);
        switchImgAsync.showProgressView();
    }

    @Override
    public void onPictureLoaded(Drawable drawable) {
        // Getting image, save and display
        switchImgAsync.showImageView(drawable);
        currentPicture = drawable;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        camera.onActivityResult(requestCode, resultCode);
    }
}
