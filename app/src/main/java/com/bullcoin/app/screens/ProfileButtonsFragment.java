package com.bullcoin.app.screens;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class ProfileButtonsFragment extends Fragment {

    private static final int PICK_FROM_GALLERY = 2;

    ImageView avatar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile_other, container, false);

        avatar = root.findViewById(R.id.avatar);
        avatar.setImageDrawable(DataModel.get().getAvatar());
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                        photoPickerIntent.putExtra("return-data", true);
                        photoPickerIntent.putExtra("crop", "true");
                        photoPickerIntent.putExtra("scale", true);
                        photoPickerIntent.putExtra("aspectX", 1);
                        photoPickerIntent.putExtra("aspectY", 1);
                        // indicate output X and Y
                        photoPickerIntent.putExtra("outputX", 300);
                        photoPickerIntent.putExtra("outputY", 300);

                        startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        TextView Profil_Name=root.findViewById(R.id.profile_name);
        String FullName=DataModel.get().getUserFirstName() +" "+ DataModel.get().getUserLastName();
        Profil_Name.setText(FullName);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");

                DataModel.get().setAvatar(getContext(), photo);
                avatar.setImageDrawable(DataModel.get().getAvatar());
            } else {
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);

                    DataModel.get().setAvatar(getContext(), photo);
                    avatar.setImageDrawable(DataModel.get().getAvatar());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
