package com.example.iome.user_profile.ui.user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.iome.R;
import com.example.iome.login.SignInActivity;
import com.example.iome.user_profile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.auth.LoginActivity;

public class UserProfileFragment extends Fragment {
    private CardView topSongsCardView, meditationCardView, attentionCardView, logoutCardView;
    private TextView emailTextView;


    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);
        topSongsCardView = root.findViewById(R.id.top_songs_card_view);
        meditationCardView = root.findViewById(R.id.meditaion_score_card_view);
        attentionCardView = root.findViewById(R.id.attention_score_card_view);
        emailTextView = root.findViewById(R.id.user_name_text_view);
        logoutCardView = root.findViewById(R.id.logout_card_view);
        Toolbar toolbar = root.findViewById(R.id.toolbar_user_profile);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        emailTextView.setText(email);



        topSongsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopSongsFragment newFragment = new TopSongsFragment();
                startNewFragment(newFragment);
            }
        });
        meditationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChartFragment newFragment = new ChartFragment();
                Bundle args = new Bundle();
                args.putString("data_type", "meditation");
                newFragment.setArguments(args);
                startNewFragment(newFragment);
            }
        });
        attentionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChartFragment newFragment = new ChartFragment();
                Bundle args = new Bundle();
                args.putString("data_type", "attention");
                newFragment.setArguments(args);
                startNewFragment(newFragment);
            }
        });
        logoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireActivity(), SignInActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });


        return root;
    }


    private void startNewFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
