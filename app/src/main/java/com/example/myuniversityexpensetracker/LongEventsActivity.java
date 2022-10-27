package com.example.myuniversityexpensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class LongEventsActivity extends AppCompatActivity {

    private static final String TAG = "LongEventsActivity";

    private RecyclerView longActivitiesRecView;
    private EventsRecViewAdapter adapter;
    private Button btnAddNewLongActivity, btnReturnMain;
    private int accountId;
    ArrayList<Event> events = new ArrayList<>();
    private Account incomingAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_events);

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

        adapter = new EventsRecViewAdapter(this, "longEvents", incomingAccount, accountId);
        longActivitiesRecView.setAdapter(adapter);
        longActivitiesRecView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setEvents(events);

        btnAddNewLongActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LongEventsActivity.this, AddNewLongEventActivity.class);
                intent.putExtra("accountId", accountId);
                intent.putExtra("editEvent", false);
                startActivity(intent);
            }
        });

        btnReturnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LongEventsActivity.this, MainActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        longActivitiesRecView = findViewById(R.id.longActivitiesRecView);
        btnAddNewLongActivity = findViewById(R.id.btnAddNewLongActivity);
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
        events = Utils.getInstance(LongEventsActivity.this).getLongEvents(incomingAccount);
    }
}