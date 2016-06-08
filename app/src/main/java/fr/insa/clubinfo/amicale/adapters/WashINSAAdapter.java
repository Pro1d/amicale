package fr.insa.clubinfo.amicale.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.models.Launderette;
import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 05/06/2016.
 */

public class WashINSAAdapter extends RecyclerView.Adapter<WashINSAAdapter.ViewHolder> {

    private static final int layoutViewTitle = R.layout.adapter_washinsa_category;
    private static final int layoutViewItem = R.layout.adapter_washinsa_machine;

    private Launderette launderette;
    private final Context context;

    public WashINSAAdapter(Launderette launderette, Context context) {
        this.launderette = launderette;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(isCategoryTitlePosition(position))
            return layoutViewTitle;
        else
            return layoutViewItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Category title
        if(isCategoryTitlePosition(position)) {
            if(isDryerPosition(position)) {
                holder.title.setText(R.string.washinsa_category_drying);
            } else {
                holder.title.setText(R.string.washinsa_category_washing);
            }
        }
        // Machine state
        else {
            LaundryMachine machine;
            if (isDryerPosition(position))
                machine = launderette.getDryer(position - 1);
            else
                machine = launderette.getWashingMachine(position - 2 - launderette.getDryersCount());

            holder.description.setText(machine.getDescription());
            if(machine.getState() == LaundryMachine.State.BUSY) {
                String txt = context.getResources().getQuantityString(R.plurals.washinsa_machine_state_running, machine.getMinutesRemaining(), machine.getMinutesRemaining());
                holder.availability.setText(txt);
                holder.number.setBackgroundResource(R.drawable.background_busy_machine_number);
            }
            else {
                holder.availability.setText(R.string.washinsa_machine_state_free);
                holder.number.setBackgroundResource(R.drawable.background_free_machine_number);
            }
            holder.number.setText(String.format(Locale.getDefault(), "%d", machine.getNumber()));
        }
    }

    @Override
    public int getItemCount() {
        if(launderette == null)
            return 0;
        else
            return launderette.getMachinesCount() + 2; // category title count = 2
    }

    public void update(Launderette launderette) {
        this.launderette = launderette;
        this.notifyDataSetChanged();
    }

    private boolean isDryerPosition(int position) {
        return launderette != null && position < launderette.getDryersCount()+1;
    }

    private boolean isCategoryTitlePosition(int position) {
        return launderette != null && (position == 0 || position == launderette.getDryersCount()+1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;
        final TextView availability;
        final TextView number;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.adapter_washinsa_tv_category_name);
            description = (TextView) view.findViewById(R.id.adapter_washinsa_tv_description);
            availability = (TextView) view.findViewById(R.id.adapter_washinsa_tv_availability);
            number = (TextView) view.findViewById(R.id.adapter_washinsa_tv_number);
        }
    }
}
