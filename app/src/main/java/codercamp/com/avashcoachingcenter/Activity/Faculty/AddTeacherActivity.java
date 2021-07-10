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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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

import codercamp.com.avashcoachingcenter.Model.TeachersDataModel;
import codercamp.com.avashcoachingcenter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeacherActivity extends AppCompatActivity {

    private Spinner TeacherCategory;
    private CircleImageView selectImage;
    private TextInputEditText name, email, post,qualification;
    private MaterialButton AddTeacherBtn;
    private final int RequestCode = 2;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private String category;
    private String Name, Email, Post,Qualification, DownloadUrl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        initView();

        String[] CategoryList = new String[]{"Select Category", "Bangla", "English", "ICT", "Physics", "Chemistry", "Biology",
                "Higher Mathematics", "Accounting"};
        TeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CategoryList));

        TeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = TeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
            }
        });

    }

    private void initView() {
        selectImage = findViewById(R.id.profile_image);
        name = findViewById(R.id.TeacherName);
        email = findViewById(R.id.TeacherEmail);
        post = findViewById(R.id.TeacherPost);
        qualification = findViewById(R.id.TeacherEduQualification);
        TeacherCategory = findViewById(R.id.TeacherCategory);
        AddTeacherBtn = findViewById(R.id.AddTeacherBtn);

        //ProgressDialog SetUp
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait....");
        progressDialog.setMessage("Loading...");


        reference = FirebaseDatabase.getInstance().getReference().child("Teachers");
        storageReference = FirebaseStorage.getInstance().getReference();

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
                    selectImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void checkValidity() {
        Name = name.getText().toString();
        Email = email.getText().toString();
        Post = post.getText().toString();
        Qualification = qualification.getText().toString();

        if (Name.isEmpty()) {
            name.setError("Please Enter Name");
            name.requestFocus();
        } else if (Email.isEmpty()) {
            email.setError("Please Enter Email");
            email.requestFocus();
        } else if (Post.isEmpty()) {
            post.setError("Please Enter Post");
            post.requestFocus();
        } else if (Qualification.isEmpty()) {
            post.setError("Please Enter Qualification");
            post.requestFocus();
        }
        else if (category.equals("Select Category")) {
            Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null) {
            UploadData();
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

                                    UploadData();

                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Log.d("tag", task.getException().getMessage());
                    Toast.makeText(AddTeacherActivity.this, "Something went wrong" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        DatabaseReference dbRef = reference.child(category);
        final String key = dbRef.push().getKey();

        TeachersDataModel model = new TeachersDataModel(Name, Email, Post,Qualification, DownloadUrl, key);

        assert key != null;
        dbRef.child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(AddTeacherActivity.this, "Teachers Added Successfully", Toast.LENGTH_SHORT).show();

                name.setText("");
                email.setText("");
                post.setText("");
                selectImage.setImageResource(R.drawable.circle_profile);
//                startActivity(new Intent(AddTeacherActivity.this,FacultyActivity.class));
//                finish();
                Intent intent = new Intent(AddTeacherActivity.this, FacultyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddTeacherActivity.this, "Teachers Added Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}