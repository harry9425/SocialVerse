package com.harry9425.yaari;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class requestfragement extends Fragment {

    RecyclerView reqrecyclerView;
    private FirebaseDatabase mDatabase;
    allrequestadapter allrequestadapter;
    private View view;
    requestmodelclass requestmodelclass;
    String curuser;
    TextView sentcount,receivedcount;
    public static int allow=0;
    ArrayList<requestmodelclass> list=new ArrayList<requestmodelclass>();
    public requestfragement() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_requestfragement, container, false);
        sentcount=view.findViewById(R.id.reqsentcount);
        receivedcount=view.findViewById(R.id.reqreceivedcount);
        sentcount.setText("Request Received: 0");
        receivedcount.setText("Pending: 0");
        reqrecyclerView=(RecyclerView) view.findViewById(R.id.requestrecycler);
        curuser= FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        allrequestadapter =new allrequestadapter(list,getContext());
        reqrecyclerView.setAdapter(allrequestadapter);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2);
        reqrecyclerView.setLayoutManager(gridLayoutManager);
        enterrr();
        return view;
    }

    private void enterrr()
    {
        final int[] r = {0};
        final int[] s = { 0 };
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("Friends Request data").child(curuser).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                r[0]=0;
                s[0]=0;
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    requestmodelclass requestmodelclass = new requestmodelclass();
                    requestmodelclass.setUid(snapshot.getKey().toString());
                    if (snapshot.child("request type").getValue().toString().equals("received")) {
                        requestmodelclass.setType("r");
                        r[0]++;
                    }
                    else {
                        requestmodelclass.setType("s");
                        s[0]++;
                    }
                    list.add(requestmodelclass);
                }
                allrequestadapter.notifyDataSetChanged();
                if(s[0]==0)
                    sentcount.setVisibility(View.GONE);
                else sentcount.setVisibility(View.VISIBLE);
                if(r[0]==0)
                    receivedcount.setVisibility(View.GONE);
                else
                    receivedcount.setVisibility(View.VISIBLE);
                receivedcount.setText("Request Received: "+r[0]);
                sentcount.setText("Pending: "+s[0]);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
}
