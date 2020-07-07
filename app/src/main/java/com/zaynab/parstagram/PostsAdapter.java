package com.zaynab.parstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;

import org.w3c.dom.Text;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;


    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvDesc;
        private ImageView ivPhoto;
        private TextView tvTimestamp;
        private ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            ivPhoto = itemView.findViewById(R.id.ivPost);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvDesc.setText(post.getDescription());
            tvTimestamp.setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString())+" ago");
            ParseFile image = post.getImage();
            if (image != null) Glide.with(context).load(image.getUrl()).into(ivPhoto);
            Glide.with(context).load(R.drawable.profile_placeholder).apply(RequestOptions.circleCropTransform()).into(ivProfile);
        }
    }

    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.insta_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    //Helper function for refresh-on-swipe container
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
