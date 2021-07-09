package com.example.medicalhomepage;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    ImageView chooseCategoryImage;
    EditText title,title2,title3;
    Button add;
    ProgressBar progressBar;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("Business Quotes");
    StorageReference reference = FirebaseStorage.getInstance().getReference("Business Quotes");
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseCategoryImage = findViewById(R.id.selectImage);
        add = findViewById(R.id.uploadBtn);
        progressBar = findViewById(R.id.progressbar);
        title = findViewById(R.id.etQuotes);
        title2 = findViewById(R.id.etQuotes2);
        title3 = findViewById(R.id.etQuotes3);


        chooseCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, 2);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                    add.setEnabled(false);
                } else {
                    Toast.makeText(MainActivity.this, "Please select image..", Toast.LENGTH_SHORT).show();
                }
            }
        });
        title.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = title.getText().toString().trim();

            add.setEnabled(!name.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void uploadToFirebase(Uri uri) {
        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUploadModel imageUploadModel = new ImageUploadModel(uri.toString(), title.getText().toString() ,title3.getText().toString(),title2.getText().toString());

                        String modelId = root.push().getKey();
                        root.child(modelId).setValue(imageUploadModel);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Items Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                        title.getText().clear();
                        title3.getText().clear();
                        title2.getText().clear();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode ==RESULT_OK && data !=null){
            imageUri=data.getData();
            chooseCategoryImage.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(mUri));
    }

}