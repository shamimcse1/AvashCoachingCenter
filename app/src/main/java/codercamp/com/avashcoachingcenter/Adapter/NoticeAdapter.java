package codercamp.com.avashcoachingcenter.Adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import codercamp.com.avashcoachingcenter.Model.NoticeDataModel;
import codercamp.com.avashcoachingcenter.R;


public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {

    private Context context;
    private List<NoticeDataModel> dataModels;

    public NoticeAdapter(Context context, List<NoticeDataModel> dataModels) {
        this.context = context;
        this.dataModels = dataModels;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_layout, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, int position) {

        NoticeDataModel model = dataModels.get(position);

        holder.Title.setText(model.getTitle());
        if (model.getImageUrl() !=null){
            Glide.with(context).load(model.getImageUrl()).into(holder.DeleteImage);
        }

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Notice");
                builder.setIcon(R.drawable.danger);
                builder.setMessage("Are sure want to delete this Notice ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice");
                        reference.child(model.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Notice Delete Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Notice Delete Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        notifyItemRemoved(position);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = null;
                try {
                    alertDialog = builder.create();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                if (alertDialog != null){
                    alertDialog.show();
                }


            }
        });
       //

    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {

        private TextView Title;
        private MaterialButton deleteBtn;
        private ImageView DeleteImage;

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);

            Title = itemView.findViewById(R.id.DeleteNotice_Title);
            deleteBtn = itemView.findViewById(R.id.DeleteNoticeBtn);
            DeleteImage = itemView.findViewById(R.id.DeleteNoticeImage);



        }
    }
}
