package com.sundaydavid.firestoreexampleproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "description";

    private EditText editTextTitle, editTextDescription, editTextPriority;
    private TextView textViewData;

    private ListenerRegistration noteListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Notebook/My first Note");
    private CollectionReference notebookRef = db.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_desc);
        textViewData = findViewById(R.id.text_view_data);
        editTextPriority = findViewById(R.id.edit_text_priority);
    }

    @Override
    protected void onStart() {
        super.onStart();

        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)
                    return;

                StringBuilder data = new StringBuilder();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());

                        String documentId = note.getDocumentId();
                        String title = note.getTitle();
                        String description = note.getDescription();
                        int priority = note.getPriority();

                        data.append("ID: ").append(documentId)
                                .append("\nTitle: ").append(title)
                                .append("\ndescription: ")
                                .append(description)
                                .append("\npriority: ")
                                .append(priority)
                                .append("\n\n");
                    }
                }
                textViewData.setText(data.toString());
            }
        });



//      noteListener =  noteRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null){
//                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, e.toString());
//                    return;
//                }
//                if (documentSnapshot != null && documentSnapshot.exists()){
//                    Note note = documentSnapshot.toObject(Note.class);
//                    if (note != null) {
//                        String title = note.getTitle();
//                        String description = note.getDescription();
//
//                        textViewData.setText("Title: " + title + "\n" + "Description: " + description );
//                    }
//                }else {
//                    textViewData.setText("");
//                }
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteListener.remove();
    }

    public void addNote(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (editTextPriority.length() == 0){
                editTextPriority.setText("0");
    }
        int priority = Integer.parseInt(editTextPriority.getText().toString());
        Note note = new Note(title, description, priority);

        notebookRef.add(note);
        editTextTitle.setText("");
        editTextDescription.setText("");

//        Map<String, Object> note = new HashMap<>();
//        note.put(KEY_TITLE, title);
//        note.put(KEY_DESC, description);

//                noteRef
//                 .set(note)
//                 .addOnSuccessListener(new OnSuccessListener<Void>() {
//                     @Override
//                     public void onSuccess(Void aVoid) {
//                         Toast.makeText(MainActivity.this,
//                                 "Note saved", Toast.LENGTH_SHORT).show();
//                         editTextTitle.setText("");
//                         editTextDescription.setText("");
//                     }
//                 }).addOnFailureListener(new OnFailureListener() {
//             @Override
//             public void onFailure(@NonNull Exception e) {
//                 Toast.makeText(MainActivity.this, "error!",
//                         Toast.LENGTH_SHORT).show();
//                 Log.d(TAG, e.toString());
//             }
//         });
    }

    public void loadNotes(View view) {

        Task task1 = notebookRef.whereLessThan("priority", 2)
                .orderBy("priority")
                .get();
        Task task2 = notebookRef.whereGreaterThan("priority", 2)
                .orderBy("priority")
                .get();

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1,task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                StringBuilder data = new StringBuilder();

                for (QuerySnapshot queryDocumentSnapshots: querySnapshots){

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            String documentId = note.getDocumentId();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();

                            data.append("ID: ").append(documentId)
                                    .append("\nTitle: ").append(title)
                                    .append("\ndescription: ")
                                    .append(description)
                                    .append("\npriority: ")
                                    .append(priority)
                                    .append("\n\n");
                        }}
                        textViewData.setText(data.toString());
                    }
        });

//        notebookRef.whereGreaterThanOrEqualTo("priority", 2)
//                .orderBy("priority", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        StringBuilder data = new StringBuilder();
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                            Note note = documentSnapshot.toObject(Note.class);
//                            note.setDocumentId(documentSnapshot.getId());
//
//                            String documentId = note.getDocumentId();
//                            String title = note.getTitle();
//                            String description = note.getDescription();
//                            int priority = note.getPriority();
//
//                            data.append("ID: ").append(documentId)
//                                    .append("\nTitle: ").append(title)
//                                    .append("\ndescription: ")
//                                    .append(description)
//                                    .append("\npriority: ")
//                                    .append(priority)
//                                    .append("\n\n");
//                        }
//                        textViewData.setText(data.toString());
//                    }
//                });
//        noteRef.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()){
////                            String title = documentSnapshot.getString(KEY_TITLE);
////                            String description = documentSnapshot.getString(KEY_DESC);
//
//                            Note note = documentSnapshot.toObject(Note.class);
//                            if (note != null) {
//                                String title = note.getTitle();
//                                String description = note.getDescription();
//
//                                textViewData.setText("Title: " + title + "\n" + "Description: " + description );
//                            }
//                        }else {
//                            Toast.makeText(MainActivity.this, "document not found",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this, "error!",
//                        Toast.LENGTH_SHORT).show();
//                Log.d(TAG, e.toString());
//            }
//        });
    }

//    public void updateDescription(View view) {
//        String description = editTextDescription.getText().toString();
//
//        Map<String, Object> note = new HashMap<>();
//        note.put(KEY_DESC, description);
//        //noteRef.set(note, SetOptions.merge());
//        noteRef.update(note);
//    }
//
//    public void deleteDescription(View view) {
////        Map<String, Object> note = new HashMap<>();
////        note.put(KEY_DESC, FieldValue.delete());
////
////        noteRef.update(note);
//        noteRef.update(KEY_DESC, FieldValue.delete());
//
//    }

    public void deleteNote(View view) {
    }
}