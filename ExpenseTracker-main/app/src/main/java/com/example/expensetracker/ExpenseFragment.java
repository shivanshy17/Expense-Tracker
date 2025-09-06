package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView recyclerView;

    private TextView expenseTotalSum;

    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;
    private Button btnUpdate;
    private Button btnDelete;

    private String type;
    private String note;
    private int amount;
    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        assert mUser != null;
        String uid=mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance("https://expanse-tracker-ef207-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("ExpenseData").child(uid);

        expenseTotalSum=myview.findViewById(R.id.expense_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_expense);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalExpense=0;
                for(DataSnapshot mysnapshot:snapshot.getChildren()){
                    Data data=mysnapshot.getValue(Data.class);
                    assert data != null;
                    totalExpense+=data.getAmount();
                    String expense_txt_result=String.valueOf(totalExpense);
                    expenseTotalSum.setText(expense_txt_result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseFragment.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setAmount(model.getAmount());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                final int adapterPosition = position;
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=getRef(adapterPosition).getKey();
                        type= model.getType();
                        note=model.getNote();
                        amount=model.getAmount();


                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public ExpenseFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycler_data, parent, false);
                return new ExpenseFragment.MyViewHolder(view);
            }
        };

        adapter.startListening(); // Important!
        recyclerView.setAdapter(adapter);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);

        }
        private void setAmount(int amount){
            TextView mAmount=mView.findViewById(R.id.amount_txt_expense);
            mAmount.setText(String.valueOf(amount));
        }
        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }


    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmount=myview.findViewById(R.id.amount_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        edtType=myview.findViewById(R.id.type_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btn_upd_Delete);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();
                String stamount=String.valueOf(amount);
                stamount=edtAmount.getText().toString().trim();

                int intamount=Integer.parseInt(stamount);
                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intamount,type,note,post_key,mDate);

                mExpenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();;
    }
}