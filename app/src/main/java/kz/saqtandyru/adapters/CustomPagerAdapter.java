package kz.saqtandyru.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import kz.saqtandyru.R;
import kz.saqtandyru.fragments.InsuranceFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES =
            new int[]{R.string.insurance};
    private final Context mContext;

    public CustomPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int pos) {
        if (pos == 0) {
            return InsuranceFragment.newInstance("InsuranceFragment, 1");
        }
        return InsuranceFragment.newInstance("InsuranceFragment, Default");
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 1;
    }
}