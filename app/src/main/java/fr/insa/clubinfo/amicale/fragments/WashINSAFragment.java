package fr.insa.clubinfo.amicale.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class WashINSAFragment extends Fragment implements OnLaundryRoomUpdatedListener, SwipeRefreshLayout.OnRefreshListener {
    private WashINSAAdapter adapter;
    private LaundryRoom laundryRoom;
    private LaundryRoomLoader loader;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new LaundryRoomLoader(getActivity(), this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loader.loadAsync(true);
    }

    boolean initialLoadDone = false;
    @Override
    public void onResume() {
        super.onResume();
        if(!initialLoadDone) {
            loader.loadAsync(false);
            initialLoadDone = true;
        }
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

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.washinsa_srl_refresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.washinsa_refresh_color1,
                R.color.washinsa_refresh_color2, R.color.washinsa_refresh_color3,
                R.color.washinsa_refresh_color4, R.color.washinsa_refresh_color5);
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
            }
        });

        return view;
    }

    @Override
    public void onLaundryRoomLoaded(LaundryRoom laundryRoom) {
        this.laundryRoom = laundryRoom;
        swipeRefresh.setRefreshing(false);
        adapter.update(laundryRoom);
    }

    @Override
    public void onLaundryRoomSyncFailed(LaundryRoom defaultLaundryRoom) {
        swipeRefresh.setRefreshing(false);

        if(laundryRoom == null) {
            this.laundryRoom = defaultLaundryRoom;
            adapter.update(defaultLaundryRoom);
        }

        Toast.makeText(getActivity(), R.string.loading_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        // Called by SwipeRefreshLayout when the user pull to refresh
        loader.loadAsync(false);
    }
}
