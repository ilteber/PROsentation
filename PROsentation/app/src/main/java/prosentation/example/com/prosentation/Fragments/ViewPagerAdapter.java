package prosentation.example.com.prosentation.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilgin on 01.05.2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> FragmentList = new ArrayList<>();
    private final List<String> FragmentListTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        return FragmentList.get(position);
    }

    @Override
    public int getCount(){
        return FragmentListTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return FragmentListTitles.get(position);
    }

    public void AddFragment(Fragment fragment, String Title){
        FragmentList.add(fragment);
        FragmentListTitles.add(Title);
    }
}
