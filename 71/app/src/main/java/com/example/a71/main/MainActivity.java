package com.example.a71.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a71.R;
import com.example.a71.create.CreateAdvertActivity;
import com.example.a71.list.ItemListActivity;

public class MainActivity extends AppCompatActivity {

    Button btnCreate, btnShowAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreate = findViewById(R.id.btnCreate);
        btnShowAll = findViewById(R.id.btnShowAll);

        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, CreateAdvertActivity.class))
        );

        btnShowAll.setOnClickListener(v ->
                startActivity(new Intent(this, ItemListActivity.class))
        );
    }
}