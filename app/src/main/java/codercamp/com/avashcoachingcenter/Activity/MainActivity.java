package codercamp.com.avashcoachingcenter.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

import codercamp.com.avashcoachingcenter.Activity.Faculty.FacultyActivity;
import codercamp.com.avashcoachingcenter.Activity.Notice.DeleteNoticeActivity;
import codercamp.com.avashcoachingcenter.Activity.Notice.NoticeActivity;
import codercamp.com.avashcoachingcenter.R;

public class MainActivity extends AppCompatActivity {
    private MaterialCardView AddNotices, AddImages, AddBooks, UpdateFaculty, DeleteNotices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        ClickListener();
    }

    private void initViews() {
        AddNotices = findViewById(R.id.AddNotice);
        AddImages = findViewById(R.id.AddGallery);
        AddBooks = findViewById(R.id.AddEbook);
        UpdateFaculty = findViewById(R.id.UpdateFaculty);
        DeleteNotices = findViewById(R.id.Delete);
    }

    public void ClickListener() {

        AddNotices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        AddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        AddBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, eBookActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        UpdateFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FacultyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        DeleteNotices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeleteNoticeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

}