package com.harry9425.yaari;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class postfragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    allpostadapter allpostadapter;
    private View view;
    postmodel postmodel;
    public static String curuser;
    ArrayList<postmodel> list=new ArrayList<postmodel>();
    
    public postfragment() {}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_postfragment, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.postrecycler);
        //curuser= FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        allpostadapter =new allpostadapter(list,getContext());
        recyclerView.setAdapter(allpostadapter);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        enterrr();
        return view;
    }

    private void enterrr()
    {
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("AAA usernames").child(curuser).child("Posts").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postmodel postmodel =new postmodel();
                    postmodel.setUrl(snapshot.child("url").getValue().toString());
                    list.add(postmodel);
                }
                allpostadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
    
}