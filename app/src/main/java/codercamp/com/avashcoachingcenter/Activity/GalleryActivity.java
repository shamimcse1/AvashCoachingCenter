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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import codercamp.com.avashcoachingcenter.Activity.Faculty.AddTeacherActivity;
import codercamp.com.avashcoachingcenter.Activity.Faculty.UpdateTeacherActivity;
import codercamp.com.avashcoachingcenter.Model.NoticeDataModel;
import codercamp.com.avashcoachingcenter.R;

public class GalleryActivity extends AppCompatActivity {

    private Spinner imageCategory;
    private MaterialCardView selectImage;
    private ImageView GalleryPreview;
    private MaterialButton UploadImage;
    private final int RequestCode = 2;
    private Bitmap bitmap;
    private DatabaseReference reference;
    private String DownloadUrl = "";
    private ProgressDialog progressDialog;
    StorageReference storageReference;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        imageCategory = findViewById(R.id.imageCategory);
        selectImage = findViewById(R.id.SelectImage);
        GalleryPreview = findViewById(R.id.GalleryPreview);
        UploadImage = findViewById(R.id.UploadImage);

        //reference = FirebaseDatabase.getInstance().getReference().child("Gallery").child(category);
        storageReference = FirebaseStorage.getInstance().getReference().child("Gallery");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(false);


        String[] CategoryList = new String[]{"Select Category", "Convocation", "Independence Day", "Other's Events"};
        imageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CategoryList));

        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Select Image
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap == null) {
                    Toast.makeText(GalleryActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();

                } else if (category.equals("Select Category")) {
                    Toast.makeText(GalleryActivity.this, "Please Select an Category", Toast.LENGTH_SHORT).show();

                } else {
                    UploadImage();
                }
            }
        });
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
                    GalleryPreview.setImageBitmap(bitmap);
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
                    Toast.makeText(GalleryActivity.this, "Something went wrong" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        reference = FirebaseDatabase.getInstance().getReference().child("Gallery").child(category);
        final String key = reference.push().getKey();

        assert key != null;
        reference.child(key).setValue(DownloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(GalleryActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(GalleryActivity.this, UpdateTeacherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(GalleryActivity.this, "Image Uploaded Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
