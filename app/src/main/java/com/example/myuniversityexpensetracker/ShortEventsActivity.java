package com.example.myuniversityexpensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShortEventsActivity extends AppCompatActivity {

    private static final String TAG = "ShortEventsActivity";

    private RecyclerView shortActivitiesRecView;
    private EventsRecViewAdapter adapter;
    private Button btnAddNewShortActivity, btnReturnMain;
    private int accountId;
    ArrayList<Event> events = new ArrayList<>();
    private Account incomingAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_events);

        initViews();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (null != intent) {
            accountId = intent.getIntExtra("accountId", -1);
            if (accountId != -1) {
                incomingAccount = Utils.getInstance(this).getAccountByID(accountId);
                if (null != incomingAccount) {
                    setData(incomingAccount);
                } else {
                }
            }
        }

        adapter = new EventsRecViewAdapter(this, "shortEvents", incomingAccount, accountId);
        shortActivitiesRecView.setAdapter(adapter);
        shortActivitiesRecView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setEvents(events);

        btnAddNewShortActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(ShortEventsActivity.this, AddNewShortEventActivity.class);
              intent.putExtra("accountId", accountId);
              intent.putExtra("editEvent", false);
              startActivity(intent);
            }
        });

        btnReturnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShortEventsActivity.this, MainActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        shortActivitiesRecView = findViewById(R.id.shortActivitiesRecView);
        btnAddNewShortActivity = findViewById(R.id.btnAddNewShortActivity);
        btnReturnMain = findViewById(R.id.btnReturnMain);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setData(Account incomingAccount) {
        events = Utils.getInstance(ShortEventsActivity.this).getShortEvents(incomingAccount);
    }
}