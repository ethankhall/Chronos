package com.kopysoft.chronos.singelton;

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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ListenerObj{
	
	private static ListenerObj instance = null;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	private final PropertyChangeSupport midnight = new PropertyChangeSupport(this);
	
	ListenerObj(){
	}
	
	public static ListenerObj getInstance(){
		if (instance == null){
			instance = new ListenerObj();
		}
		return instance;
	}
	
	public void fire(){
		this.pcs.firePropertyChange( "chronos", "update", "" );
	}
	
	public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        this.pcs.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        this.pcs.removePropertyChangeListener( listener );
    }
    
    public void fireMidnight(){
		this.midnight.firePropertyChange( "chronos", "update", "" );
	}
	
	public void addMidnightListener( PropertyChangeListener listener )
    {
        this.midnight.addPropertyChangeListener( listener );
    }

    public void removeMidnightListener( PropertyChangeListener listener )
    {
        this.midnight.removePropertyChangeListener( listener );
    }

}
