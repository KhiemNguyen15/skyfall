package com.github.skyfall.ui.send;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.net.URI;
import java.util.ArrayList;

public class SendActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    RecyclerView recyclerView;
    //SearchUserRecyclerAdapter adapter;
    UserRecyclerViewAdapter adapter;

    /*public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }*/

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
                    searchInput.setError("Invalid Username");
                    return;
                }

                setupSearchRecyclerView(searchTerm);
            }
        });

        Intent intent = getIntent();
        if(intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if(Intent.ACTION_SEND.equals(action) && type != null) {
                if(type.equalsIgnoreCase("text/plain"))
                    handleTextData(intent);
                else if(type.equalsIgnoreCase("image/"))
                    handleImageData(intent);
                else if(type.equalsIgnoreCase("application/pdf"))
                    handlePdfData(intent);
            } else if(Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if(type.startsWith("image/"))
                    handleMultipleImageData(intent);
            }
        }
    }

    private void handlePdfData(Intent intent) {
        Uri pdfFile = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(pdfFile != null)
            Log.d("Pdf File Path: ", "" + pdfFile.getPath());

    }

    private void handleImageData(Intent intent) {
        Uri imageFile = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(imageFile != null)
            Log.d("Image File Path: ", "" + imageFile.getPath());
    }

    private void handleTextData(Intent intent) {
        String textData = intent.getStringExtra(Intent.EXTRA_STREAM);
        if(textData != null)
            Log.d("Text Data: ", "" + textData);
    }
    private void handleMultipleImageData(Intent intent) {
        ArrayList<Uri> imageList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if(imageList != null)
            for(Uri uri : imageList)
                Log.d("Path: ", "" + uri.getPath());

    }

    void setupSearchRecyclerView(String searchTerm){

        //Query query = allUserCollectionReference().whereGreaterThanOrEqualTo("username", searchTerm);

        ArrayList<User> users = new ArrayList<>();
        Task<User> u = FirebaseManager.getInstance().getUser(searchTerm);

        u.addOnSuccessListener(result -> {
            Log.d("demo", "addOnSuccessListener happened.");
            users.add(u.getResult());
            adapter = new UserRecyclerViewAdapter(users);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        });
        //FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
    }

    /*protected void onStart(){
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
    }*/

}