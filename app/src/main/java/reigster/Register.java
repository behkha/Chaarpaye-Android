package reigster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.behkha.chaarpaye.MainActivity;
import com.behkha.chaarpaye.R;

import java.util.ArrayList;

import utils.NoneSwipeAbleViewPager;
import utils.UserInfo;

/**
 * Created by User on 3/18/2018.
 */

public class Register extends AppCompatActivity {

    private Context mContext;
    private NoneSwipeAbleViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.register_activity_layout);

        mViewPager = (NoneSwipeAbleViewPager) findViewById(R.id.register_view_pager);
        setupViewPager(mViewPager);
    }

    private void setupViewPager(NoneSwipeAbleViewPager viewpager){
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mFragments.add( new LoginFragment() );
        mFragments.add( new SignUpFragment() );
        RegisterViewPagerAdapter adapter = new RegisterViewPagerAdapter(getSupportFragmentManager() , mFragments);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(2);
    }
}
