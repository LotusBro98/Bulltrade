package com.bullcoin.app.screens;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;
import com.bullcoin.app.asset.AssetActivity;
import com.bullcoin.app.datamodel.DataModel;

public class SaveTreesFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.save_the_trees, container, false);

        ImageView backButton = root.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());

        VideoView videoView = root.findViewById(R.id.videoView);
        String path = "android.resource://" + "com.bullcoin.app" + "/" + R.raw.save_trees;
        videoView.setVideoURI(Uri.parse(path));
        videoView.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_trees_preload));

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
                videoView.setBackgroundDrawable(null);
            }
        });

        root.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssetActivity.navigateHere(getActivity(), DataModel.get().getAsset("Bullcoin"));
            }
        });

        return root;
    }
}
