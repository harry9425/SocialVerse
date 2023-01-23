package com.harry9425.yaari;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class chatfragment extends Fragment {

    RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference,getDatabaseReference;
    allchatadapter allchatadapter;
    private View view;
    chatmodel chatmodel,grpmodel;
    private EditText srtext;
    String tosr="",curuser;
    public static int allow=0;
    ArrayList<chatmodel> list=new ArrayList<>();;

    public chatfragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_chatfragment, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.chatrecyclerview);
        srtext=(EditText) getActivity().findViewById(R.id.searchfrname);
       // tosr=srtext.getText().toString();
        curuser= FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        allchatadapter =new allchatadapter(list,getContext());
        recyclerView.setAdapter(allchatadapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Group chat");
        databaseReference.keepSynced(true);
       // linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        if(srtext!=null) {
            enterrr();
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

        ArrayList<chatmodel> temp=new ArrayList<chatmodel>();
        for(chatmodel friends:list)
        {
            if(friends.getName().contains(tosr))
            {
                temp.add(friends);
            }
        }
        allchatadapter.filteredlist(temp);
    }


    private void enterrr()
    {
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("Chats").child(curuser).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals("groups")) {
                        chatmodel chatmodel = new chatmodel();
                        chatmodel.setIsgroup("f");
                        chatmodel.setUid(snapshot.getKey().toString().replace(mainpage.curuser, ""));
                        if (snapshot.hasChild("AA details")) {
                            if (snapshot.getChildrenCount() > 1) {
                                try {
                                    chatmodel.setTime(snapshot.child("AA details").child("lastmess time").getValue().toString());

                                } catch (Exception e) {
                                    chatmodel.setTime("NA");
                                }
                                try {
                                    chatmodel.setLastmess(snapshot.child("AA details").child("lastmess").getValue().toString());

                                } catch (Exception e) {
                                    chatmodel.setLastmess("Nothing to show");
                                }

                            } else {
                                chatmodel.setLastmess("Nothing to show");
                                chatmodel.setTime("NA");
                            }
                        } else {
                            chatmodel.setLastmess("Nothing to show");
                            chatmodel.setTime("NA");
                        }
                        list.add(chatmodel);
                    }
                }
                list.sort(new allchatsorter());
             allchatadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });


    }
}
