package com.bullcoin.app.navigation.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView name = root.findViewById(R.id.profile_name);

        name.setText(DataModel.get().getUserFirstName() + " " + DataModel.get().getUserLastName());

        List<String> posts = Arrays.asList(root.getResources().getStringArray(R.array.posts));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(getActivity(), posts);

        ImageButton settingsButton = root.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_navigation_profile_to_settingsFragment);
            }
        });

        ImageView avatar = root.findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_profile_to_profileButtonsFragment);
            }
        });
        avatar.setImageDrawable(DataModel.get().getAvatar());

        recyclerView.setAdapter(adapter);

        return root;
    }


    public static class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        PostsRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public PostsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_row_post, parent, false);
            return new PostsRecyclerViewAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(PostsRecyclerViewAdapter.ViewHolder holder, int position) {
            String text = mData.get(position);
            holder.name.setText(DataModel.get().getUserFirstName() + " " + DataModel.get().getUserLastName());
            holder.text.setText(text);
            holder.avatar.setImageDrawable(DataModel.get().getAvatar());
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView text;
            TextView time;
            ImageView avatar;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.post_name);
                time = itemView.findViewById(R.id.post_time);
                text = itemView.findViewById(R.id.post_text);
                avatar = itemView.findViewById(R.id.avatar);
            }
        }
    }
}
