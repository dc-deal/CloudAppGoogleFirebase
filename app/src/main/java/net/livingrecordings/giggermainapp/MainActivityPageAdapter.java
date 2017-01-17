package net.livingrecordings.giggermainapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class MainActivityPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    // creator
    public MainActivityPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
           //     xdeactiveFragmentMainCalendarList tab1 = new xdeactiveFragmentMainCalendarList();
                FragmentMainEquipListFragment tab1 = new FragmentMainEquipListFragment();
                return tab1;
            case 1:
                FragmentMainContactList tab2 = new FragmentMainContactList();
                return tab2;
            default:
                return null;
        }
    }


    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}