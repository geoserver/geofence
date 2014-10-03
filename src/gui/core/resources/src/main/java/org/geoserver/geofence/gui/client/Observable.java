/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class Observable.
 */
public class Observable {

    /** The changed. */
    private boolean changed = false;

    /** The obs. */
    private Vector obs;

    /**
     * Instantiates a new observable.
     */

    public Observable() {
        obs = new Vector();
    }

    /**
     * Adds the observer.
     * 
     * @param o
     *            the o
     */
    public synchronized void addObserver(Observer o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    /**
     * Delete observer.
     * 
     * @param o
     *            the o
     */
    public synchronized void deleteObserver(Observer o) {
        obs.removeElement(o);
    }

    /**
     * Notify observers.
     */
    public void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * Notify observers.
     * 
     * @param arg
     *            the arg
     */
    public void notifyObservers(Object arg) {
        /*
         * a temporary array buffer, used as a snapshot of the state of current Observers.
         */
        Object[] arrLocal;

        synchronized (this) {
            /*
             * We don't want the Observer doing callbacks into arbitrary code while holding its own
             * Monitor. The code where we extract each Observable from the Vector and store the
             * state of the Observer needs synchronization, but notifying observers does not (should
             * not). The worst result of any potential race-condition here is that: 1) a newly-added
             * Observer will miss a notification in progress 2) a recently unregistered Observer
             * will be wrongly notified when it doesn't care
             */
            if (!changed)
                return;
            arrLocal = obs.toArray();
            clearChanged();
        }

        for (int i = arrLocal.length - 1; i >= 0; i--)
            ((Observer) arrLocal[i]).update(this, arg);
    }

    /**
     * Delete observers.
     */
    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    /**
     * Sets the changed.
     */
    protected synchronized void setChanged() {
        changed = true;
    }

    /**
     * Clear changed.
     */
    protected synchronized void clearChanged() {
        changed = false;
    }

    /**
     * Checks for changed.
     * 
     * @return true, if successful
     */
    public synchronized boolean hasChanged() {
        return changed;
    }

    /**
     * Count observers.
     * 
     * @return the int
     */
    public synchronized int countObservers() {
        return obs.size();
    }

}
