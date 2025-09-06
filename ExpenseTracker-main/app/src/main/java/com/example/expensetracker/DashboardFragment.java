package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashboardFragment extends Fragment {

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;
    private TextView fab_income_text;
    private TextView fab_expense_text;

    private boolean isOpen=false;

    private Animation FadeOpen, FadeClose;

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;
    private FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter;
    private FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View myview=inflater.inflate(R.layout.fragment_dashboard, container,false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
            return myview;
        }
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance("https://expanse-tracker-ef207-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("IncomeData").child(uid);

        mExpenseDatabase = FirebaseDatabase.getInstance("https://expanse-tracker-ef207-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);

        fab_income_text=myview.findViewById(R.id.income_ft_text);
        fab_expense_text=myview.findViewById(R.id.expense_ft_text);

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);


        FadeOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        addData();

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);
                    fab_income_text.startAnimation(FadeClose);
                    fab_expense_text.startAnimation(FadeClose);
                    fab_income_text.setClickable(false);
                    fab_expense_text.setClickable(false);
                    isOpen=false;
                }else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);
                    fab_income_text.startAnimation(FadeOpen);
                    fab_expense_text.startAnimation(FadeOpen);
                    fab_income_text.setClickable(true);
                    fab_expense_text.setClickable(true);
                    isOpen=true;
                }
            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;

                for(DataSnapshot mysnap: snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    assert data != null;
                    totalsum+=data.getAmount();
                }
                String stResult = String.valueOf(totalsum);

                totalIncomeResult.setText(stResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum=0;
                for(DataSnapshot mysnap: snapshot.getChildren()){
                    Data data = mysnap.getValue(Data.class);

                    assert data != null;
                    totalsum+=data.getAmount();


                }
                String stResult = String.valueOf(totalsum);

                totalExpenseResult.setText(stResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler
        

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return myview;
    }

    //Floating button animation

    private void ftAnimation(){
        if (isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);
            fab_income_text.startAnimation(FadeClose);
            fab_expense_text.startAnimation(FadeClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);
            isOpen=false;
        }else {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);
            fab_income_text.startAnimation(FadeOpen);
            fab_expense_text.startAnimation(FadeOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
            isOpen=true;
        }
    }

    private void addData(){

        //Fab Button income..
        fab_income_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                incomeDataInsert();

            }


        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
              
                expenseDataInsert();

            }
        });

    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);
        AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmount=myview.findViewById(R.id.amount_edt);
        EditText edtType=myview.findViewById(R.id.type_edt);
        EditText edtNote=myview.findViewById(R.id.note_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("DEBUG", "Save button clicked");
                Log.d("DEBUG", "Database URL: " + FirebaseDatabase.getInstance().getReference().toString());

                String type=edtType.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field!");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field!");
                    return;
                }

                int ourAmountInt = 0;
                try {
                    ourAmountInt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    edtAmount.setError("Invalid amount!");
                    return;
                }

                if (TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field!");
                    return;
                }

                String id=mIncomeDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());
                Log.d("DEBUG", "Type: " + type);
                Log.d("DEBUG", "Amount: " + amount);
                Log.d("DEBUG", "Note: " + note);
                Data data=new Data(ourAmountInt,type,note,id,mDate);

                assert id != null;
                mIncomeDatabase.child(id).setValue(data)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("DEBUG", "Data added successfully");
                                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ERROR", "Error adding data: " + task.getException().getMessage());
                                Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FIREBASE", "Failed to add data", e);
                            Toast.makeText(getActivity(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

                ftAnimation();

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText amount=myview.findViewById(R.id.amount_edt);
        EditText type=myview.findViewById(R.id.type_edt);
        EditText note=myview.findViewById(R.id.note_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                String tmAmount=amount.getText().toString().trim();
                String tmtype=type.getText().toString().trim();
                String tmnote=note.getText().toString().trim();


                if(TextUtils.isEmpty(tmAmount)){
                    amount.setError("Required Field!!");
                    return;
                }

                int inamount=Integer.parseInt(tmAmount);

                if(TextUtils.isEmpty(tmtype)){
                    type.setError("Required Field!!");
                    return;
                }
                if(TextUtils.isEmpty(tmnote)){
                    note.setError("Required Field!!");
                    return;
                }

                String id=mExpenseDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(inamount,tmtype,tmnote,id,mDate);
                assert id != null;
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_SHORT).show();


                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                ftAnimation();
                dialog.dismiss();

            }
        });

        dialog.show();


    }


    @Override
    public void onStart() {
        super.onStart();

        // For Income Adapter
        FirebaseRecyclerOptions<Data> incomeOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(incomeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeAmount(model.getAmount());
                viewHolder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        // For Expense Adapter
        FirebaseRecyclerOptions<Data> expenseOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expenseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setExpenseType(model.getType());
                viewHolder.setExpenseAmount(model.getAmount());
                viewHolder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (incomeAdapter != null) incomeAdapter.stopListening();
        if (expenseAdapter != null) expenseAdapter.stopListening();
    }

    // For Income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        View mIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type) {
            TextView mType = mIncomeView.findViewById(R.id.type_income_ds);
            mType.setText(type);
        }

        public void setIncomeAmount(int amount) {
            TextView mAmount = mIncomeView.findViewById(R.id.amount_income_ds);
            mAmount.setText(String.valueOf(amount));
        }

        public void setIncomeDate(String date) {
            TextView mDate = mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    // For Expense data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        View mExpenseView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type) {
            TextView mType = mExpenseView.findViewById(R.id.type_expense_ds);
            mType.setText(type);
        }

        public void setExpenseAmount(int amount) {
            TextView mAmount = mExpenseView.findViewById(R.id.amount_expense_ds);
            mAmount.setText(String.valueOf(amount));
        }

        public void setExpenseDate(String date) {
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }


}