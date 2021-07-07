package codercamp.com.avashcoachingcenter.Activity.Notice;

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

import codercamp.com.avashcoachingcenter.Activity.MainActivity;
import codercamp.com.avashcoachingcenter.Model.NoticeDataModel;
import codercamp.com.avashcoachingcenter.R;

public class NoticeActivity extends AppCompatActivity {

    private MaterialCardView AddImage;
    private ImageView preview;
    private MaterialButton AddNotices;
    private TextInputEditText title;
    private final int RequestCode = 1;
    private Bitmap bitmap;
    private DatabaseReference reference;
    private String DownloadUrl = "";
    private ProgressDialog progressDialog;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        reference = FirebaseDatabase.getInstance().getReference().child("Notice");
        storageReference = FirebaseStorage.getInstance().getReference();


        AddImage = findViewById(R.id.Add_Image);
        preview = findViewById(R.id.Preview);
        AddNotices = findViewById(R.id.submitNotice);
        title = findViewById(R.id.NoticeTitle);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(false);

        //Select Image
        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        //Submitting
        AddNotices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.getText().toString().isEmpty()) {
                    title.setError("Please Enter Title");
                    title.requestFocus();

                } else if (bitmap == null) {
                    UploadData();
                } else {
                    UploadImage();
                }
            }
        });

    }

    //UploadImage on Database
    private void UploadImage() {
        progressDialog.show();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

        byte[] fileData = stream.toByteArray();
        final StorageReference storage;
        storage = storageReference.child("Notice").child((fileData) + "jpg");

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
                    Toast.makeText(NoticeActivity.this, "Something went wrong" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    }
                });
    }

    //Upload data on Database
    private void UploadData() {

        final String key = reference.push().getKey();

        String Title = title.getText().toString();

        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        String date = simpleDateFormat.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String time = timeFormat.format(calendarTime.getTime());

        NoticeDataModel model = new NoticeDataModel(Title, DownloadUrl, date, time, key);


        assert key != null;
        reference.child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(NoticeActivity.this, "Notice Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NoticeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(NoticeActivity.this, "Notice Uploaded Failed", Toast.LENGTH_SHORT).show();
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
                    preview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}