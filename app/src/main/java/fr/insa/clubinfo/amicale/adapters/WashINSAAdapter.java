package fr.insa.clubinfo.amicale.adapters;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.util.Locale;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.interfaces.OnWashINSAAlarmButtonClickedListener;
import fr.insa.clubinfo.amicale.models.LaundryRoom;
import fr.insa.clubinfo.amicale.models.LaundryMachine;

/**
 * Created by Proïd on 05/06/2016.
 */

public class WashINSAAdapter extends RecyclerView.Adapter<WashINSAAdapter.ViewHolder> {

    private static final int layoutViewTitle = R.layout.adapter_washinsa_category;
    private static final int layoutViewItem = R.layout.adapter_washinsa_machine;

    private LaundryRoom laundryRoom;
    private final Context context;
    private final OnWashINSAAlarmButtonClickedListener buttonListener;

    public WashINSAAdapter(LaundryRoom laundryRoom, Context context, OnWashINSAAlarmButtonClickedListener buttonListener) {
        this.laundryRoom = laundryRoom;
        this.context = context;
        this.buttonListener = buttonListener;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
            final LaundryMachine machine;
            if (isDryerPosition(position))
                machine = laundryRoom.getDryer(position - 1);
            else
                machine = laundryRoom.getWashingMachine(position - 2 - laundryRoom.getDryersCount());

            holder.description.setText(machine.getDescription());
            switch(machine.getState()) {
                case FREE:
                    holder.availability.setText(R.string.washinsa_machine_state_free);
                    holder.number.setBackgroundResource(R.drawable.background_free_machine_number);
                    break;
                case BUSY:
                    int minutes = machine.getMinutesRemaining();
                    String txt = context.getResources().getQuantityString(R.plurals.washinsa_machine_state_running, minutes, minutes);
                    holder.availability.setText(txt);
                    holder.number.setBackgroundResource(R.drawable.background_busy_machine_number);
                    break;
                case DISUSED:
                    holder.availability.setText(R.string.washinsa_machine_state_disused);
                    holder.number.setBackgroundResource(R.drawable.background_disused_machine_number);
                    break;
                case FINISHED:
                    holder.availability.setText(R.string.washinsa_machine_state_finished);
                    holder.number.setBackgroundResource(R.drawable.background_finished_machine_number);
                    break;
                case UNKNOWN:
                    holder.availability.setText(R.string.washinsa_machine_state_unknown);
                    holder.number.setBackgroundResource(R.drawable.background_unknown_machine_number);
                    break;
            }

            holder.number.setText(String.format(Locale.getDefault(), "%d", machine.getNumber()));
            if(machine.getState() == LaundryMachine.State.BUSY) {
                holder.alarm.setEnabled(true);
                holder.alarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonListener.onAlarmButtonClicked(machine);
                    }
                });
                holder.swipe.setSwipeListener(null);
            } else {
                holder.alarm.setEnabled(false);
                holder.alarm.setClickable(false);
                holder.swipe.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
                    @Override public void onOpened(SwipeRevealLayout view) {
                        view.close(true);
                    }
                    @Override public void onSlide(SwipeRevealLayout view, float slideOffset) { }
                    @Override public void onClosed(SwipeRevealLayout view) { }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if(laundryRoom == null)
            return 0;
        else
            return laundryRoom.getMachinesCount() + 2; // category title count = 2
    }

    public void update(LaundryRoom laundryRoom) {
        this.laundryRoom = laundryRoom;
        this.notifyDataSetChanged();
    }

    private boolean isDryerPosition(int position) {
        return laundryRoom != null && position < laundryRoom.getDryersCount()+1;
    }

    private boolean isCategoryTitlePosition(int position) {
        return laundryRoom != null && (position == 0 || position == laundryRoom.getDryersCount()+1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;
        final TextView availability;
        final TextView number;
        final ImageButton alarm;
        final SwipeRevealLayout swipe;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.adapter_washinsa_tv_category_name);
            description = (TextView) view.findViewById(R.id.adapter_washinsa_tv_description);
            availability = (TextView) view.findViewById(R.id.adapter_washinsa_tv_availability);
            number = (TextView) view.findViewById(R.id.adapter_washinsa_tv_number);
            alarm = (ImageButton) view.findViewById(R.id.adapter_washinsa_ib_alarm);
            swipe = (SwipeRevealLayout) view.findViewById(R.id.adapter_washinsa_srl_swipe);
        }
    }
}
