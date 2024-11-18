package com.github.skyfall.ui.send;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.skyfall.R;
import com.github.skyfall.data.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SendActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        searchButton = findViewById(R.id.searchicon_button);
        searchInput = findViewById(R.id.username_input);
        recyclerView = findViewById(R.id.users_RecyclerView);

        searchInput.requestFocus();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchInput.getText().toString();

                if (searchTerm.isEmpty() || searchTerm.length() < 2) {
                    searchInput.setError("Invaglid Username");
                    return;
                }

                setupSearchRecyclerView(searchTerm);
            }
        });
    }

    void setupSearchRecyclerView(String searchTerm){

        Query query = allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    protected void onStart(){
        super.onStart();
        if (adapter!=null){
            adapter.startListening();
        }
    }

    protected void onStop(){
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }
    }

    protected void onResume(){
        super.onResume();
        if (adapter!=null){
            adapter.startListening();
        }
    }

}