package com.example.myuniversityexpensetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.ArrayList;

public class EventsRecViewAdapter extends RecyclerView.Adapter<EventsRecViewAdapter.ViewHolder>{

    private static final String TAG = "AccountsRecViewAdapter";
    private ArrayList<Event> events = new ArrayList<>();
    private Context eContext;
    private String parentActivity;
    private Account incomingAccount;
    private int accountId;

    public EventsRecViewAdapter(Context eContext, String parentActivity, Account incomingAccount, int accountId) {
        this.eContext = eContext;
        this.parentActivity = parentActivity;
        this.incomingAccount = incomingAccount;
        this.accountId = accountId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_events_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsRecViewAdapter.ViewHolder holder, int position) {
        holder.eventName.setText(events.get(position).getEventName());
        holder.eventCategory.setText(events.get(position).getCategory());
        String date = String.valueOf(events.get(position).getDay()) + "/" + String.valueOf(events.get(position).getMonth()) + "/" + String.valueOf(events.get(position).getYear());
        holder.txtDateCreated.setText(date);
        if (events.get(position).isIncome()) {
            holder.incomeOutcome.setText("Income: ");
        } else {
            holder.incomeOutcome.setText("Outgoing: ");
        }
        holder.money.setText(String.valueOf(events.get(position).getMoney()));
        holder.description.setText(events.get(position).getDescription());

        if (events.get(position).isExpanded()) {
            if (events.get(position).isLongTerm()) {
                holder.repeatPeriodTxt.setVisibility(View.VISIBLE);
                holder.txtRepeatPeriod.setVisibility(View.VISIBLE);
                holder.txtRepeatPeriod.setText(String.valueOf(events.get(position).getRepeatNum()) + " " + events.get(position).getRepeatPeriod());
            }
            TransitionManager.beginDelayedTransition(holder.parent);
            holder.expandedRelLayout.setVisibility(View.VISIBLE);
            holder.downArrow.setVisibility(View.GONE);
        } else {
            TransitionManager.beginDelayedTransition(holder.parent);
            holder.expandedRelLayout.setVisibility(View.GONE);
            holder.downArrow.setVisibility(View.VISIBLE);
        }

        holder.btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(eContext);
                builder.setMessage("Are you sure you want to delete " + events.get(position).getEventName() + "?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (events.get(position).isLongTerm()) {
                            Utils.getInstance(eContext).removeLongEvent(events.get(position), incomingAccount);
                            Toast.makeText(eContext, "Event Removed", Toast.LENGTH_SHORT).show();
                            ArrayList<Event> events = Utils.getInstance(eContext).getLongEvents(incomingAccount);
                            setEvents(events);
                            notifyDataSetChanged();
                        } else {
                            Utils.getInstance(eContext).removeShortEvent(events.get(position), incomingAccount);
                            Toast.makeText(eContext, "Event Removed", Toast.LENGTH_SHORT).show();
                            ArrayList<Event> events = Utils.getInstance(eContext).getShortEvents(incomingAccount);
                            setEvents(events);
                            notifyDataSetChanged();
                        }
                    }
                });

                builder.create().show();
            }
        });

        holder.btnEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events.get(position).isLongTerm()) {
                    Intent intent = new Intent(eContext, AddNewLongEventActivity.class);
                    intent.putExtra("accountId", accountId);
                    intent.putExtra("editEvent", true);
                    intent.putExtra("longEventId", events.get(position).getId());
                    eContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(eContext, AddNewShortEventActivity.class);
                    intent.putExtra("accountId", accountId);
                    intent.putExtra("editEvent", true);
                    intent.putExtra("shortEventId", events.get(position).getId());
                    eContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView parent;
        private TextView eventName, btnDeleteEvent, eventCategory;

        private ImageView downArrow, upArrow;
        private RelativeLayout expandedRelLayout;
        private TextView incomeOutcome, money, description, btnEditEvent, txtDateCreated, repeatPeriodTxt, txtRepeatPeriod;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.txtEventName);
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent);
            parent = itemView.findViewById(R.id.eventsParent);
            eventCategory = itemView.findViewById(R.id.txtShortEventCategory);
            downArrow = itemView.findViewById(R.id.btnDownArrow);

            upArrow = itemView.findViewById(R.id.btnUpArrow);
            expandedRelLayout = itemView.findViewById(R.id.shortEventsExpandedRelLayout);
            incomeOutcome = itemView.findViewById(R.id.txtShortIncomeOutcome);
            money = itemView.findViewById(R.id.txtShortEventMoney);
            description = itemView.findViewById(R.id.txtShortEventDesc);
            btnEditEvent = itemView.findViewById(R.id.btnEditEvent);
            txtDateCreated = itemView.findViewById(R.id.txtDateCreated);
            txtRepeatPeriod = itemView.findViewById(R.id.txtRepeatPeriod);
            repeatPeriodTxt = itemView.findViewById(R.id.repeatPeriodTxt);

            downArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = events.get(getAdapterPosition());
                    event.setExpanded(!event.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            upArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = events.get(getAdapterPosition());
                    event.setExpanded(!event.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

    public void addShortEvent(Event event, Account account){
        Utils.getInstance(eContext).addShortEvent(event, account);
        notifyDataSetChanged();
    }

    public void addLongEvent(Event event, Account account){
        Utils.getInstance(eContext).addLongEvent(event, account);
        notifyDataSetChanged();
    }

    public void editShortEvent(Event newShortEvent, Account account, int shortEventId) {
        Utils.getInstance(eContext).editShortEvent(newShortEvent, account, shortEventId);
        notifyDataSetChanged();
    }

    public void editLongEvent(Event newLongEvent, Account account, int longEventId) {
        Utils.getInstance(eContext).editLongEvent(newLongEvent, account, longEventId);
        notifyDataSetChanged();
    }

    public static String getTAG() {
        return TAG;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
