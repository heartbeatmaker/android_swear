package com.example.swearVer2;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class FriendManagingActivity extends AppCompatActivity {

    ViewPager pager;
    LinearLayout viewPager_linearLayout;
    Button friend_list_btn, find_friends_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_managing);

        initViewPager();
    }


    private void initViewPager(){

        pager = (ViewPager)findViewById(R.id.main_viewPager);
        pagerAdapter adapter = new pagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        viewPager_linearLayout = (LinearLayout)findViewById(R.id.viewPager_linearLayout);

        friend_list_btn = (Button)findViewById(R.id.viewPager_friendList);
        find_friends_btn = (Button)findViewById(R.id.viewPager_findFriends);

        pager.setCurrentItem(0);

        friend_list_btn.setOnClickListener(movePageListener);
        friend_list_btn.setTag(0);
        find_friends_btn.setOnClickListener(movePageListener);
        find_friends_btn.setTag(1);

        friend_list_btn.setSelected(true);

        //swipe 처리하는 부분
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                int i = 0;
                while(i<2){
                    if(position == i){
                        viewPager_linearLayout.findViewWithTag(i).setSelected(true);
//                        viewPager_linearLayout.findViewWithTag(i).setBackgroundColor(R.drawable.grad_bg);
                    }
                    else{
                        viewPager_linearLayout.findViewWithTag(i).setSelected(false);
//                        viewPager_linearLayout.findViewWithTag(i).setBackgroundColor(R.drawable.quantum_ic_clear_grey600_24);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
    }

    View.OnClickListener movePageListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();

            int i= 0;
            while(i<2){
                if(tag == i){
                    viewPager_linearLayout.findViewWithTag(i).setSelected(true);
//                    viewPager_linearLayout.findViewWithTag(i).setBackgroundColor(R.drawable.grad_bg);
                }
                else{
                    viewPager_linearLayout.findViewWithTag(i).setSelected(false);
//                    viewPager_linearLayout.findViewWithTag(i).setBackgroundColor(R.drawable.quantum_ic_clear_grey600_24);
                }
                i++;
            }
            pager.setCurrentItem(tag);
        }
    };



    private class pagerAdapter extends FragmentStatePagerAdapter {

        public pagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return new Fragment_friendList();
                case 1:
                    return new Fragment_findFriend();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
