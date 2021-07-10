package codercamp.com.avashcoachingcenter.Activity.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import codercamp.com.avashcoachingcenter.Adapter.TeacherAdapter;
import codercamp.com.avashcoachingcenter.Model.TeachersDataModel;
import codercamp.com.avashcoachingcenter.R;

public class FacultyActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private RecyclerView BanglaDep, EnglishDep, ICTDep, PhysicsDep, ChemistryDep, HigherDep, BiologyDep, AccountDep;
    private LinearLayout BanglaDepL, EnglishDepL, ICTDepL, PhysicsDepL, ChemistryDepL, HigherDepL, BiologyDepL, AccountDepL;
    private List<TeachersDataModel> list1, list2, list3, list4, list5, list6, list7, list8;
    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private TeacherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        initView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyActivity.this, AddTeacherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Bangla();
        English();
        ICT();
        Physics();
        Chemistry();
        Biology();
        HigherMath();
        Accounting();
        progressDialog.show();
    }

    private void initView() {

        //ProgressDialog SetUp
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait....");
        progressDialog.setMessage("Loading...");

        reference = FirebaseDatabase.getInstance().getReference().child("Teachers");

        fab = findViewById(R.id.floatingBtn);

        //Find All Recyclerview
        BanglaDep = findViewById(R.id.BanglaRecyclerviewId);
        EnglishDep = findViewById(R.id.EnglishRecyclerviewId);
        ICTDep = findViewById(R.id.ICTRecyclerviewId);
        PhysicsDep = findViewById(R.id.PhysicsRecyclerviewId);
        ChemistryDep = findViewById(R.id.ChemistryRecyclerviewId);
        HigherDep = findViewById(R.id.HigherMathematicsRecyclerviewId);
        BiologyDep = findViewById(R.id.BiologyRecyclerviewId);
        AccountDep = findViewById(R.id.AccountingRecyclerviewId);

        //Find All LinearLayout for No Data Found
        BanglaDepL = findViewById(R.id.BanglaNoData);
        EnglishDepL = findViewById(R.id.EnglishNoData);
        ICTDepL = findViewById(R.id.ICTNoData);
        PhysicsDepL = findViewById(R.id.PhysicsNoData);
        ChemistryDepL = findViewById(R.id.ChemistryNoData);
        HigherDepL = findViewById(R.id.HigherMathematicsNoData);
        BiologyDepL = findViewById(R.id.BiologyNoData);
        AccountDepL = findViewById(R.id.AccountingNoData);


    }

    private void Bangla() {
        DatabaseReference BanglaReference = reference.child("Bangla");

        BanglaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1 = new ArrayList<>();
                if (!snapshot.exists()) {
                    BanglaDepL.setVisibility(View.VISIBLE);
                    BanglaDep.setVisibility(View.GONE);
                } else {
                    BanglaDepL.setVisibility(View.GONE);
                    BanglaDep.setVisibility(View.VISIBLE);

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list1.add(model);
                        //Log.d("Data", model.getName().toString()+ model.getEmail().toString());
                    }
                    BanglaDep.setHasFixedSize(true);
                    BanglaDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list1, "Bangla");
                    adapter.notifyDataSetChanged();
                    BanglaDep.setAdapter(adapter);

                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void English() {
        DatabaseReference EnglishReference = reference.child("English");

        EnglishReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2 = new ArrayList<>();
                if (!snapshot.exists()) {
                    EnglishDepL.setVisibility(View.VISIBLE);
                    EnglishDep.setVisibility(View.GONE);
                } else {
                    EnglishDepL.setVisibility(View.GONE);
                    EnglishDep.setVisibility(View.VISIBLE);

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list2.add(model);
                        //Log.d("Data", model.getName().toString()+ model.getEmail().toString());
                    }
                    EnglishDep.setHasFixedSize(true);
                    EnglishDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list2, "English");
                    adapter.notifyDataSetChanged();
                    EnglishDep.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void ICT() {
        DatabaseReference ICTReference = reference.child("ICT");

        ICTReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list3 = new ArrayList<>();
                if (!snapshot.exists()) {
                    ICTDepL.setVisibility(View.VISIBLE);
                    ICTDep.setVisibility(View.GONE);
                } else {
                    ICTDepL.setVisibility(View.GONE);
                    ICTDep.setVisibility(View.VISIBLE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list3.add(model);
                    }
                    ICTDep.setHasFixedSize(true);
                    ICTDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list3, "ICT");
                    adapter.notifyDataSetChanged();
                    ICTDep.setAdapter(adapter);

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void Physics() {
        DatabaseReference PhysicsReference = reference.child("Physics");

        PhysicsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list4 = new ArrayList<>();
                if (!snapshot.exists()) {
                    PhysicsDepL.setVisibility(View.VISIBLE);
                    PhysicsDep.setVisibility(View.GONE);
                } else {
                    PhysicsDepL.setVisibility(View.GONE);
                    PhysicsDep.setVisibility(View.VISIBLE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list4.add(model);
                        assert model != null;
                        Log.d("Data", model.getName().toString() + model.getEmail().toString());
                    }
                    PhysicsDep.setHasFixedSize(true);
                    PhysicsDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list4, "Physics");
                    adapter.notifyDataSetChanged();
                    PhysicsDep.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void Chemistry() {
        DatabaseReference ChemistryReference = reference.child("Chemistry");

        ChemistryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list5 = new ArrayList<>();
                if (!snapshot.exists()) {
                    ChemistryDepL.setVisibility(View.VISIBLE);
                    ChemistryDep.setVisibility(View.GONE);
                } else {
                    ChemistryDepL.setVisibility(View.GONE);
                    ChemistryDep.setVisibility(View.VISIBLE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list5.add(model);
                    }
                    ChemistryDep.setHasFixedSize(true);
                    ChemistryDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list5, "Chemistry");
                    adapter.notifyDataSetChanged();
                    ChemistryDep.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //progressDialog.dismiss();
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void Biology() {
        DatabaseReference BiologyReference = reference.child("Biology");

        BiologyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list6 = new ArrayList<>();
                if (!snapshot.exists()) {
                    BiologyDepL.setVisibility(View.VISIBLE);
                    BiologyDep.setVisibility(View.GONE);
                } else {
                    BiologyDepL.setVisibility(View.GONE);
                    BiologyDep.setVisibility(View.VISIBLE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list6.add(model);
                    }
                    BiologyDep.setHasFixedSize(true);
                    BiologyDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list6, "Biology");
                    adapter.notifyDataSetChanged();
                    BiologyDep.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void HigherMath() {
        DatabaseReference HigerMathReference = reference.child("Higher Mathematics");

        HigerMathReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list7 = new ArrayList<>();
                if (!snapshot.exists()) {
                    HigherDepL.setVisibility(View.VISIBLE);
                    HigherDep.setVisibility(View.GONE);
                } else {
                    HigherDepL.setVisibility(View.GONE);
                    HigherDep.setVisibility(View.VISIBLE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list7.add(model);
                    }
                    HigherDep.setHasFixedSize(true);
                    HigherDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list7, "Higher Mathematics");
                    adapter.notifyDataSetChanged();
                    HigherDep.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void Accounting() {
        DatabaseReference AccountingReference = reference.child("Accounting");

        AccountingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list8 = new ArrayList<>();
                if (!snapshot.exists()) {
                    AccountDepL.setVisibility(View.VISIBLE);
                    AccountDep.setVisibility(View.GONE);
                } else {
                    AccountDepL.setVisibility(View.GONE);
                    AccountDep.setVisibility(View.VISIBLE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeachersDataModel model = dataSnapshot.getValue(TeachersDataModel.class);
                        list8.add(model);
                    }
                    AccountDep.setHasFixedSize(true);
                    AccountDep.setLayoutManager(new LinearLayoutManager(FacultyActivity.this));
                    adapter = new TeacherAdapter(FacultyActivity.this, list8, "Accounting");
                    adapter.notifyDataSetChanged();
                    AccountDep.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}