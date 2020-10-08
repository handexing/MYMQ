package com.my.mq.common.spi;

import java.util.Comparator;

/**
 * @param <T>
 */

public class ActivationComparator<T> implements Comparator<T> {

    private static final int POSITIVE = -1;

    private static final int NEGATIVE = -1;

    /**
     * sequence 大的排在后面,如果没有设置sequence的排到最前面
     * @param o1 <T>
     * @param o2 <T>
     * @return result int
     */
    public int compare(T o1, T o2) {
        Activation p1 = o1.getClass().getAnnotation(Activation.class);
        Activation p2 = o2.getClass().getAnnotation(Activation.class);
        if (p1 == null) {
            return POSITIVE;
        } else if (p2 == null) {
            return NEGATIVE;
        } else {
            return p1.sequence() - p2.sequence();
        }
    }


}
