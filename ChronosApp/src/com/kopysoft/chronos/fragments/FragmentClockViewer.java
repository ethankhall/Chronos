/*******************************************************************************
 * Copyright (c) 2011 Ethan Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.kopysoft.chronos.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.fragments.ClockFragments.PayPeriod.PayPeriodSummaryFragment;
import com.kopysoft.chronos.fragments.ClockFragments.Today.TodayPairFragment;
import com.viewpagerindicator.TitleProvider;

import java.util.LinkedList;
import java.util.List;

public class FragmentClockViewer extends FragmentPagerAdapter implements TitleProvider {

    private List<Fragment> fragments;
    private final String TAG = Defines.TAG + " - Clock Viewer Fragment";

    public FragmentClockViewer(FragmentManager fm) {
        super(fm);

        fragments = new LinkedList<Fragment>();
        fragments.add(TodayPairFragment.newInstance());
        fragments.add(PayPeriodSummaryFragment.newInstance());
        //fragments.add(TodayIndividualFragment.newInstance());
        //fragments.add(TodaySummaryFragment.newInstance());
        //fragments.add(TodayDropDownFragment.newInstance());
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public String getTitle(int i) {
        return " ";
    }
}
