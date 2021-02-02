package com.bullcoin.app.screens;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.navigation.home.maintabs.WalletFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsFragment extends Fragment {

    public class FriendBonus {
        public String name;
        public int iconResourceID;
        public int bonusResourceID;
        public int percent;

        public FriendBonus(String name, int iconResourceID, int bonusResourceID, int percent) {
            this.name = name;
            this.iconResourceID = iconResourceID;
            this.bonusResourceID = bonusResourceID;
            this.percent = percent;
        }
    }

    private FriendBonus[] friendBonuses = {
            null,
            new FriendBonus("Mark Spenser", R.drawable.max_spencer, R.drawable.ic_bonus_1, 20),
            new FriendBonus("Андрей Баев", R.drawable.andrew, R.drawable.ic_bonus_4, 80),
            new FriendBonus("Nekomata Okayu", R.drawable.nekomata, R.drawable.ic_bonus_3, 60),
            new FriendBonus("Bull Bulls", R.drawable.bulbulls, R.drawable.ic_bonus_2, 40)
    };

    private final static int BONUS_HEADER = 0;
    private final static int BONUS_FRIEND = 1;

    RecyclerView recyclerFriends;
    FriendSpinnerAdapter adapter;
    TextView friendsText;
    Button friendsButton;
    boolean hidden = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsText = root.findViewById(R.id.text_friends);

        adapter = new FriendSpinnerAdapter(getContext(), Arrays.asList(friendBonuses));

        recyclerFriends = root.findViewById(R.id.recycler_friends);
        recyclerFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerFriends.setAdapter(adapter);

        friendsButton = root.findViewById(R.id.friends_button);
        friendsButton.setOnClickListener(v -> toggleFriends());

        hideFriends();
        hidden = true;

        ImageView backButton = root.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());

        return root;
    }

    private void hideFriends() {
        friendsButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
        int items = adapter.getItemCount();
        adapter.mData = new ArrayList<>();
        adapter.notifyItemRangeRemoved(0, items);
    }

    private void showFriends() {
        friendsButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
        adapter.mData = Arrays.asList(friendBonuses);
        int items = adapter.getItemCount();
        adapter.notifyItemRangeInserted(0, items);
    }

    private void toggleFriends() {
        if (hidden) {
            hidden = false;
            showFriends();
        } else {
            hidden = true;
            hideFriends();
        }
    }

    public class FriendSpinnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public List<FriendBonus> mData;
        private LayoutInflater mInflater;
        Context context;

        FriendSpinnerAdapter(Context context, List<FriendBonus> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == BONUS_HEADER) {
                View view = mInflater.inflate(R.layout.spinner_row_friend_title, parent, false);
                return new FriendSpinnerAdapter.ViewHolderTitle(view);
            } else {
                View view = mInflater.inflate(R.layout.spinner_row_friend, parent, false);
                return new FriendSpinnerAdapter.ViewHolder(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return BONUS_HEADER;
            } else {
                return BONUS_FRIEND;
            }
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            FriendBonus friendBonus = mData.get(position);

            if (getItemViewType(position) == BONUS_FRIEND) {
                ViewHolder holderFr = (ViewHolder) holder;
                holderFr.name.setText(friendBonus.name);
                holderFr.percent.setText(friendBonus.percent + "%");
                holderFr.avatar.setImageDrawable(getResources().getDrawable(friendBonus.iconResourceID));
                holderFr.bonus.setImageDrawable(getResources().getDrawable(friendBonus.bonusResourceID));
            }
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView percent;
            ImageView avatar;
            ImageView bonus;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.friend_name);
                percent = itemView.findViewById(R.id.friend_bonus_percent);
                avatar = itemView.findViewById(R.id.friend_avatar);
                bonus = itemView.findViewById(R.id.friend_bonus_img);
            }
        }

        public class ViewHolderTitle extends RecyclerView.ViewHolder {
            ViewHolderTitle(View itemView) {
                super(itemView);
            }
        }
    }
}
