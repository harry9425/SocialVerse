package com.harry9425.yaari;


import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPageAdapter extends FragmentPagerAdapter {

    public SectionsPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if(position==0)
        {
            requestfragement mrequestfragment =new requestfragement();
            return mrequestfragment;
        }
        else if(position==1)
        {
            chatfragment mchatfragment=new chatfragment();
            return mchatfragment;
        }
        else if(position==2)
        {
            friendfragment mfriendfragment = new friendfragment();
            return mfriendfragment;
        }
        else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position)
        {
            case 0:
                return "REQUESTS";
            case 1:

                return "CHATS";
            case 2:

                return "FRIENDS";

            default:return null;
        }
    }
}
