package com.mg.common.utils.cache;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Allows to lock by instance (called a 'key')
 * The usage pattern is:
 * 
 * KeySemaphore<MyKeyClass> ks = new KeySemaphore();
 * 
 * MyKeyClass aKey = ...;
 * boolean found = false;
 * try
 * {
 *    if( (found = ks.aquire(aKey, timeOut)) )
 *    {
 *    ... //protected zone
 *    }
 * }
 * finally
 * {
 *    if(found)
 *      ks.release(aKey);
 * }
 * @author Edwin
 *
 * @param <T>
 */
public class KeySemaphore<T extends Comparable<?>>
{
    protected Logger log = LogManager.getLogger( );
//    private static int locks = 0;

    private Collection< T > keys ;

    public KeySemaphore() {
		
    	super();
    	 keys = new TreeSet<T>();
	}
    
    
    public KeySemaphore(Comparator<T> comparator) {
		
    	super();
    	 keys = comparator!=null? new TreeSet< T >(comparator):new TreeSet<T>();
	}

	/**
     * Attempt to grab the lock within the given period of time
     * If this method return true the client MUST eventually call release;
     * @param key the key to be locked
     * @param timeout the max wait time for the lock
     * @return
     */
    public synchronized boolean acquire( T key, long timeout )
    {
        boolean bGrabed = false;
        if ( keys.contains( key ) )
        {
            try
            {
                wait( timeout );
            }
            catch ( InterruptedException e )
            {
            	//e.printStackTrace();
            }
        }
        if ( !keys.contains( key ) )
        { 
            keys.add( key );
            bGrabed = true;
        }
        log.debug("locked {}",key );

        return bGrabed;
    }

    public synchronized void acquire( T key )
    {
        while ( keys.contains( key ) )
        {
            try
            {  
            	log.debug( "waiting {}..", key );
                wait( 2000 );
            }
            catch ( InterruptedException e )
            { 
            	return;
            }
        }

        log.debug("locked {}",key );
        keys.add( key );
    }

    /**
     * Waits until the lock is released but never grabs it
     * @param key
     */
    public synchronized void waitFor( T key )
    {
        while ( keys.contains( key ) )
        {
            try
            {  
            	log.debug( "waiting {}..", key );
                wait( 2000 );
            }
            catch ( InterruptedException e )
            { 
            	return;
            }
        }
    }

    public synchronized void release( T key )
    {
        log.debug("released {}",key );
        if ( keys.remove( key ) )
            notifyAll();
    }
}
