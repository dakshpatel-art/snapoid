package com.example.snapoid;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    public ViewPagerAdapter(FragmentManager supportFragmentManager) {

    }

    public void addFragment(Object filtersListFragment, String filters) {
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 0;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
