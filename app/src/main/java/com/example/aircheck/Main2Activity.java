package com.example.aircheck;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aircheck.Interface.IFirebaseLoadDone;
import com.example.aircheck.pm25.Item;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements IFirebaseLoadDone {
    Button btn1;
    SearchableSpinner state;

    DatabaseReference itemRef;
    IFirebaseLoadDone iFirebaseLoadDone;

    List<Item> items;

    boolean isFirstTimeClick=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btn1 = (Button) findViewById(R.id.pm);

        state = (SearchableSpinner) findViewById(R.id.province);
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!isFirstTimeClick) {
                    Item item = items.get(position);
                    btn1.setText(item.getPm());
                }
                else
                    isFirstTimeClick=false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        itemRef = FirebaseDatabase.getInstance().getReference("Data");

        iFirebaseLoadDone = this;

        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> items = new ArrayList<>();
                for(DataSnapshot itemSnapShot:dataSnapshot.getChildren()) {
                    items.add(itemSnapShot.getValue(Item.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });

    }

    @Override
    public void onFirebaseLoadSuccess(List<Item> itemList) {
        items = itemList;

        List<String> item_list = new ArrayList<>();
        for(Item item:itemList)
            item_list.add(item.getProvince());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.spinner_item,item_list);
        state.setAdapter(adapter);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {

    }
}
