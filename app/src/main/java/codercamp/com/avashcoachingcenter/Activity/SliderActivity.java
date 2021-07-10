package codercamp.com.avashcoachingcenter.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import codercamp.com.avashcoachingcenter.Activity.Faculty.FacultyActivity;
import codercamp.com.avashcoachingcenter.R;

public class SliderActivity extends AppCompatActivity {
    private MaterialCardView AddImage;
    private ImageView previewSlider;
    private MaterialButton UploadSlider;
    private TextInputEditText titleSlider;
    private final int RequestCode = 1;
    private DatabaseReference databaseReference;
    private String DownloadUrl;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        initView();

        //Select Image
        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        UploadSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap == null) {
                    Toast.makeText(SliderActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();

                } else if (titleSlider.getText().toString().isEmpty()) {
                    titleSlider.setError("Please Enter Slider Name");
                    titleSlider.requestFocus();
                } else {
                    UploadImage();
                }
            }
        });
    }

    private void initView() {

        titleSlider = findViewById(R.id.SliderTitle);
        AddImage = findViewById(R.id.Add_Slider);
        UploadSlider = findViewById(R.id.UploadSlider);
        previewSlider = findViewById(R.id.previewSlider);

        storageReference = FirebaseStorage.getInstance().getReference("Slider");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(false);
    }

    //Take a Image from the storage
    private void OpenGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode && resultCode == RESULT_OK) {

            if (data != null) {
                Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    previewSlider.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //UploadImage on Database
    private void UploadImage() {

        progressDialog.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

        byte[] fileData = stream.toByteArray();
        final StorageReference storage;
        storage = storageReference.child((fileData) + "jpg");

        final UploadTask uploadTask = storage.putBytes(fileData);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    DownloadUrl = String.valueOf(uri);
                                    UploadData();

                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Log.d("tag", task.getException().getMessage());
                    Toast.makeText(SliderActivity.this, "Something went wrong" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }


    //Upload data on Database
    private void UploadData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Slider");
        final  String key = databaseReference.push().getKey().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", titleSlider.getText().toString());
        map.put("imageUrl", DownloadUrl);

        databaseReference.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    titleSlider.setText("");
                    previewSlider.setImageResource(R.drawable.preview_logo);
                    Toast.makeText(SliderActivity.this, "Slider Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SliderActivity.this, "Slider Uploaded Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}