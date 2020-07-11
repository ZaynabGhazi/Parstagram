package com.zaynab.parstagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.parstagram.fragments.GridProfileFragment;
import com.zaynab.parstagram.fragments.PostDetailsFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
        private ImageView ivLikes;
        private TextView tvLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            ivPhoto = itemView.findViewById(R.id.ivPost);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            ivLikes = itemView.findViewById(R.id.ivLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            itemView.setOnClickListener(this);
        }

        public void bind(final Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvDesc.setText(post.getDescription());
            tvTimestamp.setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");

            //correct heart icon:
            if (post.likedByCurrentUser) {
                Glide.with(context).load(R.drawable.ufi_heart_active).into(ivLikes);
            }
            else   Glide.with(context).load(R.drawable.ufi_heart).into(ivLikes);

            ParseFile image = post.getImage();
            if (image != null) Glide.with(context).load(image.getUrl()).into(ivPhoto);
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

            final boolean[] liked = {false};
            //implement a like-system
            ivLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //check if user already liked
                    //should be optimized
                    final ParseRelation<ParseUser> likers = post.getRelation("likers");
                    final ParseQuery<ParseUser> usrLikers = likers.getQuery();
                    usrLikers.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                Log.i("USERS:", "Looking for current user");
                                for (int i = 0; i < objects.size(); i++) {
                                    Log.i("USERS:", objects.get(i).getUsername());
                                    if (objects.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                        liked[0] = true;
                                        break;
                                    }
                                }//end loop
                                if (liked[0]) {
                                        unlikePost(view,post,likers,liked);
                                } else {
                                        likePost(view,post,likers);
                                }
                            }//end works
                        }//end done
                    });
                }//end onclick
            });
            tvLikes.setText(Integer.toString(post.getLikes()) + " likes");
            tvLikes.setVisibility(View.VISIBLE);
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
        public void likePost(View view, final Post post, ParseRelation<ParseUser> likers){
            ivLikes.setImageDrawable(view.getResources().getDrawable(R.drawable.ufi_heart_active));
            likers.add(ParseUser.getCurrentUser());
            post.put("likes", post.getLikes() + 1);
            post.likedByCurrentUser = true;
            PostsAdapter.this.notifyItemChanged(getAdapterPosition());
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.i("LIKES", "Error liking post!");
                    }
                    tvLikes.setText(Integer.toString(post.getLikes()) + " likes");
                }
            });
        }

        public void unlikePost(View view, final Post post, ParseRelation<ParseUser> likers, final boolean[] liked){
            ivLikes.setImageDrawable(view.getResources().getDrawable(R.drawable.ufi_heart));
            likers.remove(ParseUser.getCurrentUser());
            post.put("likes", post.getLikes() - 1);
            post.likedByCurrentUser = false;
            PostsAdapter.this.notifyItemChanged(getAdapterPosition());
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.i("LIKES", "Error unliking post!");
                    }
                    liked[0] = false;
                    tvLikes.setText(Integer.toString(post.getLikes()) + " likes");
                }
            });
        }
    }//end VH_CLASS

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

    //quick fix to recycled icons
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
