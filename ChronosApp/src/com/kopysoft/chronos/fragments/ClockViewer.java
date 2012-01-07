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

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.kopysoft.chronos.adapter.clock.TodayAdapterIndividual;
import com.kopysoft.chronos.adapter.clock.TodayAdapterDropDown;
import com.kopysoft.chronos.adapter.clock.TodayAdapterSummary;
import com.kopysoft.chronos.adapter.clock.TodayAdapterPair;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.view.RowElement;
import com.kopysoft.chronos.view.ScrollState;
import com.viewpagerindicator.TitleProvider;

@Deprecated
public class ClockViewer extends PagerAdapter implements TitleProvider{

    private static final String TAG = Defines.TAG + " - ClockViewer";

    private static String[] titles = new String[] { "Demo 1", "Demo 2", "Demo 3", "Demo 4" };
    private final Context context;
    private int[] scrollPosition = new int[titles.length];

    public ClockViewer(Context context)
    {
        this.context = context;
        for ( int i = 0; i < titles.length; i++ )
        {
            scrollPosition[i] = 0;
        }
    }

    public String getTitle( int position )
    {
        return titles[position];
    }

    public int getCount()
    {
        return titles.length;
    }

    public Object instantiateItem( View pager, int position )
    {
        Log.d(TAG, "Position: " + position);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        if(position == 0){
            //Log.d(TAG, "Position: " + position);
            Chronos chrono = new Chronos(context);
            ListView retView = new ListView( context );
            BaseAdapter adapter;

            //header to the row
            RowElement header = new RowElement(context);
            header.left().setText("In time");
            header.center().setText("Task");
            header.right().setText("Out time");
            //retView.addHeaderView(header);
            layout.addView(header, 0);
            layout.addView(retView, 1);

            adapter = new TodayAdapterPair(context, chrono.getAllPunches());
            retView.setAdapter( adapter );
            retView.setSelection( scrollPosition[ position ] );

            chrono.close();

        }  else if(position == 1) {
            Chronos chrono = new Chronos(context);
            ListView retView = new ListView( context );
            BaseAdapter adapter;

            RowElement header = new RowElement(context);
            header.left().setText("In time");
            header.center().setText("");
            header.right().setText("Type");
            //retView.addHeaderView(header);
            layout.addView(header, 0);
            layout.addView(retView, 1);

            adapter = new TodayAdapterIndividual(context, chrono.getAllPunches());
            retView.setAdapter( adapter );
            retView.setSelection( scrollPosition[ position ] );

            chrono.close();

        }   else if(position == 2){
            //retView = new ExpandableListView(context);
            Chronos chrono = new Chronos(context);
            ListView retView = new ListView( context );
            BaseAdapter adapter;

            RowElement header = new RowElement(context);
            header.left().setText("Time");
            header.center().setText("");
            header.right().setText("Type");
            //retView.addHeaderView(header);
            layout.addView(header, 0);
            layout.addView(retView, 1);

            adapter = new TodayAdapterSummary(context, chrono.getAllPunches());
            retView.setAdapter( adapter );
            retView.setSelection( scrollPosition[ position ] );

            chrono.close();
        }   else if(position == 3) {
            //retView = new ExpandableListView(context);
            Chronos chrono = new Chronos(context);
            ExpandableListView retView = new ExpandableListView( context );
            BaseExpandableListAdapter adapter;

            RowElement header = new RowElement(context);
            header.left().setText("");
            header.center().setText("Time");
            header.right().setText("Task");
            //retView.addHeaderView(header);
            layout.addView(header, 0);
            layout.addView(retView, 1);

            adapter = new TodayAdapterDropDown(context, chrono.getAllPunches());
            retView.setAdapter( adapter );
            retView.setSelection( scrollPosition[ position ] );

            chrono.close();
        }

        ((ViewPager) pager).addView( layout, 0 );

        /*
        ListView v = new ListView( context );
		String[] from = new String[] { "str" };
		int[] to = new int[] { android.R.id.text1 };
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();
		for ( int i = 0; i < 20; i++ )
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put( "str", String.format( "Item %d", i + 1 ) );
			items.add( map );
		}
		SimpleAdapter adapter = new SimpleAdapter( context, items,
				android.R.layout.simple_list_item_1, from, to );
		v.setAdapter( adapter );
		((ViewPager)pager ).addView( v, 0 );
		v.setSelection( scrollPosition[ position ] );
         */

        return layout;
    }

    public void destroyItem( View pager, int position, Object view )
    {
        ((ViewPager)pager).removeView( (View) view );
    }

    public boolean isViewFromObject( View view, Object object )
    {
        return view.equals( object );
    }

    public void finishUpdate( View view )
    {
    }

    public void restoreState( Parcelable p, ClassLoader c )
    {
        if ( p instanceof ScrollState)
        {
            scrollPosition = ( (ScrollState) p ).getScrollPos();
        }
    }

    public Parcelable saveState()
    {
        return new ScrollState( scrollPosition );
    }

    public void startUpdate( View view )
    {
    }

}