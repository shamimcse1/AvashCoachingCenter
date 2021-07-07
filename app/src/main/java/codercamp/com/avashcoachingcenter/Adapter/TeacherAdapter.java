package codercamp.com.avashcoachingcenter.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import codercamp.com.avashcoachingcenter.Activity.Faculty.UpdateTeacherActivity;
import codercamp.com.avashcoachingcenter.Model.TeachersDataModel;
import codercamp.com.avashcoachingcenter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.MyViewHolder> {

    private Context context;
    private List<TeachersDataModel> modelList;
    private String category;

    public TeacherAdapter(Context context, List<TeachersDataModel> modelList, String category) {
        this.context = context;
        this.modelList = modelList;
        this.category = category;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.teacher_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TeachersDataModel teachersDataModel = modelList.get(position);

        holder.name.setText(teachersDataModel.getName());
        holder.email.setText(teachersDataModel.getEmail());
        holder.post.setText(teachersDataModel.getPost());

        Glide.with(context).load(teachersDataModel.getImageUrl()).into(holder.imageView);

        holder.updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateTeacherActivity.class);
                intent.putExtra("name", teachersDataModel.getName());
                intent.putExtra("email", teachersDataModel.getEmail());
                intent.putExtra("post", teachersDataModel.getPost());
                intent.putExtra("key", teachersDataModel.getKey());
                intent.putExtra("imageUrl", teachersDataModel.getImageUrl());
                intent.putExtra("category", category);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private TextView name, email, post;
        private MaterialButton updateInfo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.teacher_image);
            name = itemView.findViewById(R.id.teacherName);
            email = itemView.findViewById(R.id.teacherEmail);
            post = itemView.findViewById(R.id.teacherPost);
            updateInfo = itemView.findViewById(R.id.teacherUpdate);

        }
    }
}
