package com.example.myuniversityexpensetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AccountsRecViewAdapter extends RecyclerView.Adapter<AccountsRecViewAdapter.ViewHolder>{

    private static final String TAG = "AccountsRecViewAdapter";
    private ArrayList<Account> accounts = new ArrayList<>();
    private Context mContext;
    private String parentActivity;

    public AccountsRecViewAdapter(Context mContext, String parentActivity) {
        this.mContext = mContext;
        this.parentActivity = parentActivity;
        setAccounts(Utils.getInstance(mContext).getAccounts());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_accounts_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsRecViewAdapter.ViewHolder holder, int position) {
        holder.txtFullName.setText(accounts.get(position).getFirstName() + " " + accounts.get(position).getLastName());

        // Moves user to their main page when their name is clicked
        holder.txtFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,LogInActivity.class);
                intent.putExtra("accountId", accounts.get(position).getId());
                intent.putExtra("deleteAccount", false);
                mContext.startActivity(intent);
            }
        });

        // Deleting an account
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,LogInActivity.class);
                intent.putExtra("accountId", accounts.get(position).getId());
                intent.putExtra("deleteAccount", true);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    public void addAccount(String firstName, String lastName, String password) {
        Utils.getInstance(mContext).addAccount(firstName, lastName, password);
        notifyDataSetChanged();
    }

   public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView parent;
        private TextView txtFullName;
        private TextView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            parent = itemView.findViewById(R.id.parent);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
   }
}
