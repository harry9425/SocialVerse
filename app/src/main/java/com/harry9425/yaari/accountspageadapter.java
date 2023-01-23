package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class accountspageadapter extends FragmentPagerAdapter {

    public accountspageadapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if(position<=2)
        {
            postfragment postfragment=new postfragment();
            return postfragment;
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
                return "Posts";
            case 1:

                return "Reels";
            case 2:

                return "Interact";

            default:return null;
        }
    }
}
