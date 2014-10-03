/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 */

public class CategorizedCircularBuffer<T, K> {
    // protected final static Logger LOGGER = LogManager.getLogger(CategorizedCircularBuffer.class);
    private final int maxSize;

    LinkedList<Pair<T, K>> mainList;

    private Map<K, LinkedList<T>> typedLists = new HashMap<K, LinkedList<T>>();

    public CategorizedCircularBuffer(int maxCount) {
        if (maxCount < 1)
            throw new IllegalArgumentException("Bad size");
        this.maxSize = maxCount;
        mainList = new LinkedList<Pair<T, K>>();
    }

    public void add(K key, T value) {
        // add to main list
        mainList.addFirst(new Pair<T, K>(key, value));
        while (mainList.size() > maxSize) {
            removeLastEntry();
        }

        LinkedList<T> typedList = typedLists.get(key);
        if (typedList == null) {
            typedList = new LinkedList<T>();
            typedLists.put(key, typedList);
        }
        typedList.addFirst(value);
    }

    private void removeLastEntry() {
        Pair<T, K> lastEntry = mainList.pollLast();

        LinkedList<T> typedList = typedLists.get(lastEntry.key);
        if (typedList == null)
            throw new IllegalStateException("Internal error - can't find list for " + lastEntry);

        T remove = typedList.removeLast();
        if (!lastEntry.value.equals(remove))
            throw new IllegalStateException("Internal error - mismatching values "
                    + lastEntry.value + " , " + remove);

        if (typedList.size() == 0)
            typedLists.remove(lastEntry.key);
    }

    public List<T> subList(int fromIndex, int toIndex) {
        List<T> ret = new ArrayList<T>(toIndex - fromIndex);
        for (Pair<T, K> pair : mainList.subList(fromIndex, toIndex)) {
            ret.add(pair.value);
        }
        return ret;
    }

    public int size() {
        return mainList.size();
    }

    public List<T> subListByKey(K key, int fromIndex, int toIndex) {
        LinkedList<T> typedList = typedLists.get(key);
        if (typedList == null)
            return Collections.EMPTY_LIST;

        return typedList.subList(fromIndex, toIndex);
    }

    public int sizeByKey(K key) {
        LinkedList<T> typedList = typedLists.get(key);
        if (typedList == null)
            return 0;

        return typedList.size();
    }

    static class Pair<T, K> {
        K key;

        T value;

        public Pair(K key, T value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Pair{" + "key=" + key + " value=" + value + '}';
        }
    }

}
