package com.zaynab.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.zaynab.parstagram.EndlessRecyclerViewScrollListener;
import com.zaynab.parstagram.Post;
import com.zaynab.parstagram.PostsAdapter;
import com.zaynab.parstagram.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {
    public static final String TAG = "POSTS_FRAGMENT";
    protected RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    protected List<Post> allPosts;

    public PostsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        queryPosts();
        make_refreshOnSwipe();


    }

    private void bindView(View view) {
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        allPosts = new ArrayList<>();
        PostsAdapter.OnClickListener clickListener = new PostsAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Log.i(TAG, "Post clicked at position " + position);
                Bundle b = new Bundle();
                b.putSerializable("POST", allPosts.get(position));
                PostDetailsFragment postDetailsFragment = new PostDetailsFragment();
                postDetailsFragment.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, postDetailsFragment).commit();
            }
        };
        adapter = new PostsAdapter(getContext(), allPosts, clickListener);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager llManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(llManager);
        scrollListener = new EndlessRecyclerViewScrollListener(llManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //last post
                Log.i(TAG,"Infinite pagination activated!");
                Post last = allPosts.get(allPosts.size()-1);
                fetchOlderContent(last);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
    }

    private void fetchOlderContent(Post last) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereLessThan(Post.KEY_CREATEDAT,last.getCreatedAt());
        query.addDescendingOrder(Post.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.");
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                adapter.addAll(posts);
            }
        });
    }

    private void make_refreshOnSwipe() {
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(20);
            }
        });
    }

    private void populateTimeline(int i) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(i);
        query.addDescendingOrder(Post.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.");
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                adapter.clear();
                adapter.addAll(posts);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.");
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });

    }
}