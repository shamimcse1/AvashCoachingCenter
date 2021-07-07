package codercamp.com.avashcoachingcenter.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import codercamp.com.avashcoachingcenter.R;

public class eBookActivity extends AppCompatActivity {
    private MaterialCardView AddPdf;
    private MaterialButton UploadPdf;
    private TextInputEditText titlePdf;
    private TextView SelectOrNot;
    private final int RequestCode = 1;
    private DatabaseReference databaseReference;
    private String DownloadUrl;
    private ProgressDialog progressDialog;
    private Uri pdfData;
    private StorageReference storageReference;
    private String PdfName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_book);

        initView();

        //Select a PDF
        AddPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPdfGallery();
            }
        });

        //Submitting
        UploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (titlePdf.getText().toString().isEmpty()) {
                    titlePdf.setError("Please Enter Title");
                    titlePdf.requestFocus();

                } else if (pdfData == null) {
                    Toast.makeText(eBookActivity.this, "Please Select a Pdf File", Toast.LENGTH_SHORT).show();
                } else {
                    Upload_Pdf();
                }
            }
        });
    }

    private void Upload_Pdf() {
        progressDialog.show();
        StorageReference sReference = storageReference.child("PDF/" + PdfName + "-" + System.currentTimeMillis() + ".pdf");
        sReference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DownloadUrl = String.valueOf(uri);

                        UploadPdfONDatabase(DownloadUrl);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(eBookActivity.this, "Pdf Uploaded Failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(eBookActivity.this, "Something wen wrong", Toast.LENGTH_SHORT).show();
                Log.d("tag", e.getMessage());

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    private void UploadPdfONDatabase(String downloadUrl) {

        String key = databaseReference.child("pdf").push().getKey();
        String title = titlePdf.getText().toString();
        HashMap<String, String> map = new HashMap();
        map.put("pdfTitle", title);
        map.put("pdfUrl", downloadUrl);
        databaseReference.child("PDF").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    titlePdf.setText("");
                    Toast.makeText(eBookActivity.this, "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(eBookActivity.this, "Pdf Uploaded Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initView() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(false);

        AddPdf = findViewById(R.id.Add_Pdf);
        titlePdf = findViewById(R.id.PdfTitle);
        UploadPdf = findViewById(R.id.UploadPdf);
        SelectOrNot = findViewById(R.id.FileSelectTxt);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    //Take a Pdf from the storage
    @SuppressLint("ObsoleteSdkInt")
    private void OpenPdfGallery() {

       /* Intent intent = new Intent();
        intent.setType("pdf/doc/ppt/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf File"), RequestCode);*/

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode && resultCode == RESULT_OK && data != null) {

            pdfData = data.getData();
            if (pdfData.toString().startsWith("content://")) {

                Cursor cursor = null;
                try {
                    cursor = eBookActivity.this.getContentResolver().query(pdfData, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {

                        PdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (pdfData.toString().startsWith("file://")) {
                PdfName = new File(pdfData.toString()).getName();
            }

            SelectOrNot.setText(PdfName);
            Log.d("tag", pdfData.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}