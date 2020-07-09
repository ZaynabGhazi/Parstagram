package com.zaynab.parstagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;
import com.zaynab.parstagram.fragments.GridProfileFragment;
import com.zaynab.parstagram.fragments.PostDetailsFragment;

import org.w3c.dom.Text;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private OnClickListener clickListener;


    //communicate with fragment:
    public interface OnClickListener {
        void OnItemClicked(int position);
    }

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
        this.context = context;
        this.posts = posts;
        this.clickListener = clickListener;

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvDesc.setText(post.getDescription());
            tvTimestamp.setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");
            ParseFile image = post.getImage();
            if (image != null) Glide.with(context).load(image.getUrl()).into(ivPhoto);
            // Glide.with(context).load(post.getUser().get("Photo")).placeholder(R.drawable.profile_placeholder).apply(RequestOptions.circleCropTransform()).into(ivProfile);
            Glide.with(context).load(R.drawable.profile_placeholder).apply(RequestOptions.circleCropTransform()).into(ivProfile);
            if (post.getUser().get("Photo") != null)
                Glide.with(context).load(post.getUser().getParseFile("Photo").getUrl()).placeholder(R.drawable.profile_placeholder).apply(RequestOptions.circleCropTransform()).into(ivProfile);

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Instead of communicating with fragments through an interface
                    launchGridProfile(view);
                }
            });
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Instead of communicating with fragments through an interface
                    launchGridProfile(view);
                }
            });


        }

        public void launchGridProfile(View view) {
            Bundle b = new Bundle();
            b.putSerializable("USER", posts.get(getAdapterPosition()));
            GridProfileFragment gridProfileFragment = new GridProfileFragment();
            gridProfileFragment.setArguments(b);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, gridProfileFragment).commit();
        }

        @Override
        public void onClick(View view) {
            clickListener.OnItemClicked(getAdapterPosition());
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

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
