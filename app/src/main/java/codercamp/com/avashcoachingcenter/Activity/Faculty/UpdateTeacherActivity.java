package codercamp.com.avashcoachingcenter.Activity.Faculty;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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

import codercamp.com.avashcoachingcenter.Model.TeachersDataModel;
import codercamp.com.avashcoachingcenter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateTeacherActivity extends AppCompatActivity {
    private CircleImageView updateImage;
    private TextInputEditText name, email, post, qualification;
    private MaterialButton UpdateBtn, DeleteBtn;
    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private String Name, Email, Post, ImageUrl, Qualification, key;
    private final int RequestCode = 2;
    private Bitmap bitmap;
    private StorageReference storageReference;
    private String category;
    public String DownloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        initView();

        Name = getIntent().getStringExtra("name");
        Email = getIntent().getStringExtra("email");
        Post = getIntent().getStringExtra("post");
        key = getIntent().getStringExtra("key");
        Qualification = getIntent().getStringExtra("qualification");
        category = getIntent().getStringExtra("category");
        ImageUrl = getIntent().getStringExtra("imageUrl");


        name.setText(Name);
        email.setText(Email);
        post.setText(Post);

        Glide.with(this).load(ImageUrl).into(updateImage);


        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheekValidation();
            }
        });
        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteInfo();
            }
        });


    }


    private void initView() {
        //ProgressDialog SetUp
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait....");
        progressDialog.setMessage("Loading...");

        reference = FirebaseDatabase.getInstance().getReference().child("Teachers");
        storageReference = FirebaseStorage.getInstance().getReference();


        updateImage = findViewById(R.id.UpdateProfile_image);
        name = findViewById(R.id.UpdateTeacherName);
        email = findViewById(R.id.UpdateTeacherEmail);
        post = findViewById(R.id.UpdateTeacherPost);
        qualification = findViewById(R.id.UpdateTeacherQuali);
        UpdateBtn = findViewById(R.id.UpdateTeacherBtn);
        DeleteBtn = findViewById(R.id.DeleteTeacherBtn);
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
                    updateImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void cheekValidation() {

        Name = name.getText().toString();
        Email = email.getText().toString();
        Post = post.getText().toString();
        Qualification = qualification.getText().toString();
        if (Name.isEmpty()) {
            name.setError("Please Enter Name");
            name.requestFocus();
        } else if (Email.isEmpty()) {
            name.setError("Please Enter Email");
            name.requestFocus();
        } else if (Post.isEmpty()) {
            post.setError("Please Enter Post");
            post.requestFocus();
        } else if (Qualification.isEmpty()) {
            post.setError("Please Enter Qualification");
            post.requestFocus();
        } else if (bitmap == null) {
            UploadData(ImageUrl);
        } else {
            UploadImage();
        }

    }


    //UploadImage on Database
    private void UploadImage() {

        progressDialog.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

        byte[] fileData = stream.toByteArray();
        final StorageReference storage;
        storage = storageReference.child("Teacher Image").child((fileData) + "jpg");

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

                                    UploadData(ImageUrl);

                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Log.d("tag", task.getException().getMessage());
                    Toast.makeText(UpdateTeacherActivity.this, "Something went wrong" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
    private void UploadData(String imageUrl) {
        progressDialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", Name);
        map.put("email", Email);
        map.put("post", Post);
        map.put("qualification", Qualification);
        map.put("imageUrl", imageUrl);

        reference.child(category).child(key).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Data Update Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this, FacultyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Data Update Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void DeleteInfo() {
        progressDialog.show();

        reference.child(category).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Data Delete Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this, FacultyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Data Delete Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}