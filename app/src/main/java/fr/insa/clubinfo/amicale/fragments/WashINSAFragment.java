package fr.insa.clubinfo.amicale.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.adapters.WashINSAAdapter;
import fr.insa.clubinfo.amicale.interfaces.OnLaundryRoomUpdatedListener;
import fr.insa.clubinfo.amicale.models.LaundryRoom;
import fr.insa.clubinfo.amicale.sync.LaundryRoomLoader;

public class WashINSAFragment extends Fragment implements OnLaundryRoomUpdatedListener {
    private WashINSAAdapter adapter;
    private LaundryRoom laundryRoom;
    private LaundryRoomLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new LaundryRoomLoader(getActivity(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loader.loadAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        loader.cancel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_washinsa);

        adapter = new WashINSAAdapter(laundryRoom, getActivity());

        View view = inflater.inflate(R.layout.fragment_washinsa, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.washinsa_rv_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onLaundryRoomLoaded(LaundryRoom laundryRoom) {
        this.laundryRoom = laundryRoom;
        adapter.update(laundryRoom);
    }

    @Override
    public void onLaundryRoomSyncFailed(LaundryRoom defaultLaundryRoom) {
        if(laundryRoom == null) {
            this.laundryRoom = defaultLaundryRoom;
            adapter.update(defaultLaundryRoom);
        }

        Toast.makeText(getActivity(), R.string.loading_error_message, Toast.LENGTH_SHORT).show();
    }
}
