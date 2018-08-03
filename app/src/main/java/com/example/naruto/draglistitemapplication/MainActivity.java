package com.example.naruto.draglistitemapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvData;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            dataList.add("item" + i);
        }
        rvData = findViewById(R.id.rv_data);
        adapter = new MyAdapter(this, dataList, rvData, R.layout.item);
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setAdapter(adapter);
        setOnListItemClickListener();
    }

    //设置监听
    private void setOnListItemClickListener() {
        adapter.setOnItemClickListener(new DragListAdapter.OnItemClickListener() {
            @Override
            public void onClick(DragListAdapter.DragListViewHolder holder) {

            }
        });

        adapter.setOnDeleteClickListener(new DragListAdapter.OnItemDeleteClickListener() {
            @Override
            public void onClick(DragListAdapter.DragListViewHolder holder) {

            }
        });

    }
}
