package com.cloudwell;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.cloudwell.property.PropertiesListActivity;


public class authentication extends AppCompatActivity implements View.OnClickListener {

    private FragmentStateAdapter adapterViewPager;

    private TextView SignIn_btn, SignUp_btn;
    private ViewPager2 viewPager2;
    private Button sign_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SignIn_btn = findViewById(R.id.log_in);
        SignUp_btn = findViewById(R.id.sign_up);
        sign_button = findViewById(R.id.id_sign_button);
        sign_button.setOnClickListener(this);
        SignUp_btn.setOnClickListener(this);
        SignIn_btn.setOnClickListener(this);
        sign_button.setText("SIGNIN");

        viewPager2 = findViewById(R.id.viewPager);
        adapterViewPager = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapterViewPager);
        viewPager2.setCurrentItem(0);

    }

    @Override
    public void onClick(View v) {
        if (v == SignIn_btn) {
            viewPager2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager2.setCurrentItem(1);
                    sign_button.setText("SIGNIN");
                }
            }, 10);
        }
        if (v == SignUp_btn) {
            viewPager2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager2.setCurrentItem(0);
                    sign_button.setText("SIGNUP");
                }
            }, 10);
        }

        if (v==sign_button){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
