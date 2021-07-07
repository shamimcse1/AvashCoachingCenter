package codercamp.com.avashcoachingcenter.Activity.Notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import codercamp.com.avashcoachingcenter.Adapter.NoticeAdapter;
import codercamp.com.avashcoachingcenter.Model.NoticeDataModel;
import codercamp.com.avashcoachingcenter.R;

public class DeleteNoticeActivity extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<NoticeDataModel> dataList;
    private DatabaseReference reference;
    private NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);

        recyclerView = findViewById(R.id.Notice_Rercyclerview);
        progressBar = findViewById(R.id.progress_circular);

        reference = FirebaseDatabase.getInstance().getReference().child("Notice");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getNoticeData();

    }

    private void getNoticeData() {
        progressBar.setVisibility(View.VISIBLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    NoticeDataModel model = dataSnapshot.getValue(NoticeDataModel.class);

                    dataList.add(model);
                }

                adapter = new NoticeAdapter(DeleteNoticeActivity.this,dataList);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteNoticeActivity.this, "Error " +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}