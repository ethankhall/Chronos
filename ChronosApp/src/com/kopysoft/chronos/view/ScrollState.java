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

package com.kopysoft.chronos.view;

import android.os.Parcel;
import android.os.Parcelable;

public class ScrollState implements Parcelable
{
	private int[] scrollPos;

	public static Parcelable.Creator<ScrollState> CREATOR = new Parcelable.Creator<ScrollState>()
	{

		public ScrollState createFromParcel( Parcel source )
		{
			int size = source.readInt();
			int[] scrollPos = new int[ size ];
			source.readIntArray( scrollPos );
			return new ScrollState( scrollPos );
		}

		public ScrollState[] newArray( int size )
		{
			return new ScrollState[ size ];
		}
	};
	public ScrollState( int[] scrollPos )
	{
		this.scrollPos = scrollPos;
	}

	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel( Parcel dest, int flags )
	{
		dest.writeInt( scrollPos.length );
		dest.writeIntArray( scrollPos );
	}

	public int[] getScrollPos()
	{
		return scrollPos;
	}
}
