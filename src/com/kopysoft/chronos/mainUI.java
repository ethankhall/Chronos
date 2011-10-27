package com.kopysoft.chronos;

/**
 * 			Copyright (C) 2011 by Ethan Hall
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * 	in the Software without restriction, including without limitation the rights
 * 	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * 	copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.view.ViewPagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;
import org.joda.time.DateTime;

import java.sql.SQLException;

public class mainUI extends FragmentActivity {
    /** Called when the activity is first created. */
    private static final String TAG = Defines.TAG + " - Main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dropAndTest();

        ViewPagerAdapter adapter = new ViewPagerAdapter( this );
        ViewPager pager =
                (ViewPager)findViewById( R.id.viewpager );
        TitlePageIndicator indicator =
                (TitlePageIndicator)findViewById( R.id.indicator );
        pager.setAdapter( adapter );
        indicator.setViewPager( pager );
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.None);
    }

    private void dropAndTest(){
        try{
            ConnectionSource connectionSource = new AndroidConnectionSource(new Chronos(this));

            TableUtils.dropTable(connectionSource, Punch.class, true); //Drop all
            TableUtils.createTable(connectionSource, Punch.class); //Create Table

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = BaseDaoImpl.createDao(connectionSource, Punch.class);

            int iActionReason = 0;
            int iJobNumber = 0;
            int iPunchTag = 1;
            DateTime iTime = new DateTime();

            for(int i = 0; i < 15; i++){

                iTime = iTime.minusHours(1);

                Punch temp = new Punch(iActionReason, iJobNumber, iPunchTag, iTime);

                Log.d(TAG, "Output: " + punchDao.create(temp));
            }


            connectionSource.close();
        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }
}