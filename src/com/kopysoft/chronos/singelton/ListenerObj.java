package com.kopysoft.chronos.singelton;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ListenerObj{
	
	private static ListenerObj instance = null;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	
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

}
