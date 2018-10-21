package com.example.android.likemyblog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import android.text.format.DateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    private List<User> user_list;

    private Context context;
    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;






    public BlogRecyclerAdapter(List<BlogPost> blog_list, List<User> user_list){
        this.blog_list = blog_list;
        this.user_list = user_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder((view));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //User Data

        holder.setIsRecyclable(false);
        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String descData = blog_list.get(position).getDesc();
        holder.setDesctext(descData);

        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        String blog_user_id = blog_list.get(position).getUser_id();

        if(blog_user_id.equals(currentUserId))
        {
            holder.blogDeleteBtn.setEnabled(true);
            holder.blogDeleteBtn.setVisibility(View.VISIBLE);
        }
        String userName = user_list.get(position).getName();
        String userImage = user_list.get(position).getImage();

        holder.setUserData(userName, userImage);


        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        }
        catch(Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //Get Like
        firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists())
                {
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                }
                else
                {
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_grey));

                }


            }
        });

        //Get Likes Count

        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();
                   // holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                    holder.updateLikeCount(count);

                } else {
                    //holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_grey));

                    holder.updateLikeCount(0);

                }

            }
        });




        //Likes Feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists())
                        {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).set(likesMap);
                        }
                        else
                        {
                            firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).delete();

                        }

                    }
                });

            }
        });



        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });


        holder.blogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        blog_list.remove(position);
                        user_list.remove(position);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;

        private TextView blogUserName;
        private CircleImageView blogUserImage;


     //   private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        private ImageView blogCommentBtn;
        private TextView blogCommentCount;

        private Button blogDeleteBtn;
        public ImageView blogLikeBtn;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn =mView.findViewById(R.id.blog_comment_icon);
            blogDeleteBtn = mView.findViewById(R.id.blog_delete_btn);


        }

        public void setDesctext(String descText){

            descView = mView.findViewById(R.id.blog_description);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri)
        {
            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.mipmap.image2);
            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(downloadUri).into(blogImageView);
        }
        public void setTime(String date)
        {
            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }
        public void setUserData(String name, String image)
        {
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.mipmap.image1);
            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(blogUserImage);

        }
        public void updateLikeCount(int count)
        {
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");
        }
        public void updateCommentCount(int count)
        {
            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
            blogCommentCount.setText(count + " Comments");
        }

    }


}