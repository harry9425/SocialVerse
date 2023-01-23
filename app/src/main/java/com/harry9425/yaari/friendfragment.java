package com.harry9425.yaari;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class friendfragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseDatabase mDatabase,mDatabase2;
    allfriendsadapter allfriendsadapter;
    private View view;
    friendsclass friendsclass;
    private EditText srtext;
    String tosr="",curuser;
    public static int allow=0;
    ArrayList<friendsclass> list=new ArrayList<friendsclass>();


    public friendfragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_friendfragment, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.friendsrecycler);
        srtext=(EditText) getActivity().findViewById(R.id.searchfrname);
      // tosr=srtext.getText().toString();
       curuser= FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        allfriendsadapter =new allfriendsadapter(list,getContext());
        recyclerView.setAdapter(allfriendsadapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        tosr="";

        enterrr();
        if(srtext!=null) {
            srtext.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    tosr = srtext.getText().toString();
                    filter(tosr);
                }
            });
        }
        return view;
    }

    private void filter(String tosr) {

        ArrayList<friendsclass> temp=new ArrayList<friendsclass>();
        for(friendsclass friends:list)
        {
          if(friends.getName().contains(tosr))
          {
              temp.add(friends);
          }
        }
        allfriendsadapter.filteredlist(temp);
    }

    private void enterrr()
    {
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("Friends list data").child(curuser).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendsclass friendsclass=new friendsclass();
                    friendsclass.setUid(snapshot.getKey().toString());
                    list.add(friendsclass);
                }
                allfriendsadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }



}
