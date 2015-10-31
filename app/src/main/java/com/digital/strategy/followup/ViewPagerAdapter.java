package com.digital.strategy.followup;

/**
 * Created by tech on 9/22/15.
 */
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        Fragment ret = new FollowUpOnePageOne();

        switch (position) {
            case 0: FollowUpOnePageTwo titleScreen = new FollowUpOnePageTwo();
                    ret = titleScreen;
                    break;

            case 1: FollowUpOnePageOne questionScreen = new FollowUpOnePageOne();
                    ret =  questionScreen;
                    break;

            case 2: ScreenPararagraph2 paragraphScreen = new ScreenPararagraph2();
                    ret =  paragraphScreen;

            default: break;

        }

        /*if(position == 0) // if the position is 0 we are returning the First tab
        {
            FollowUpOnePageOne pageOne = new FollowUpOnePageOne();
            return pageOne;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            FollowUpOnePageTwo pageTwo = new FollowUpOnePageTwo();
            return pageTwo;
        }*/
        return ret;

    }

    // This method return the titles for the Tabs in the Tab Strip

    //@Override
    //public CharSequence getPageTitle(int position) {
    //    return Titles[position];
    //}

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return 3;
    }
}