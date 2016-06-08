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
import fr.insa.clubinfo.amicale.interfaces.OnLaunderetteUpdatedListener;
import fr.insa.clubinfo.amicale.models.Launderette;
import fr.insa.clubinfo.amicale.sync.LaunderetteLoader;

public class WashINSAFragment extends Fragment implements OnLaunderetteUpdatedListener {
    private WashINSAAdapter adapter;
    private Launderette launderette;
    private LaunderetteLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new LaunderetteLoader(this);
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

        View view = inflater.inflate(R.layout.fragment_washinsa, container, false);
        adapter = new WashINSAAdapter(launderette, getActivity());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.washinsa_rv_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onLaunderetteLoaded(Launderette launderette) {
        this.launderette = launderette;
        adapter.update(launderette);
    }

    @Override
    public void onLaunderetteSyncFailed() {
        Toast.makeText(getActivity(), R.string.loading_error_message, Toast.LENGTH_SHORT).show();
    }
}
