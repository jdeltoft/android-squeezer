package uk.org.ngo.squeezer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import uk.org.ngo.squeezer.model.Player;

class PlayerDropdownAdapter extends ArrayAdapter<Player> {
    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }
    public interface OnPlayerLongClickListener {
        void onPlayerLongClick(Player player);
    }

    public static final Player POWER_OFF_ALL = new Player(java.util.Map.of("playerid", "POWER_OFF_ALL", "name", "POWER_OFF_ALL"));
    private final Player activePlayer;
    private final OnPlayerClickListener clickListener;
    private final OnPlayerLongClickListener longClickListener;
    private boolean continuePlayback;

    public PlayerDropdownAdapter(Context actionBarContext, List<Player> connectedPlayers, Player activePlayer, OnPlayerClickListener clickListener, OnPlayerLongClickListener longClickListener) {
        super(actionBarContext, 0);
        add(null);
        addAll(connectedPlayers);
        add(POWER_OFF_ALL);
        this.activePlayer = activePlayer;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Player item = getItem(position);
        if (item == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.continue_playback, parent, false);
            view.setOnClickListener(v -> {
                continuePlayback = !continuePlayback;
                view.<CheckBox>findViewById(R.id.checkbox).setChecked(continuePlayback);
            });
            return view;
        } else if (item == POWER_OFF_ALL) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dropdown_power_off_all, parent, false);
            view.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onPlayerClick(item);
            });
            return view;
        } else {
            TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dropdown_item, parent, false);
            view.setText(item.getName());
            if (!item.getPlayerState().isPoweredOn()) {
                view.setAlpha(0.5f);
            } else {
                view.setAlpha(1.0f);
            }
            view.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onPlayerClick(item);
            });
            view.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onPlayerLongClick(item);
                }
                return true; // Consume the long click event
            });
            return view;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        Player item = getItem(position);
        if (item == null) {
            return false;
        }
        if (item == POWER_OFF_ALL) {
            return true;
        }
        return !item.equals(activePlayer);
    }

    public boolean continuePlayback() {
        return continuePlayback;
    }
}
