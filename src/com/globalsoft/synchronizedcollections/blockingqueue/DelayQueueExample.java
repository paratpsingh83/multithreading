package com.globalsoft.synchronizedcollections.blockingqueue;

public class DelayQueueExample {
    /**
     * This is an unbounded BlockingQueue of objects that implement
     * the Delayed interface
     * - DelayQueue keeps the elements internally until a certain delay has expired !!
     * - an object can only be taken from the queue when its delay has expired !!
     * - We cannot place null items in the queue
     * - the queue is sorted so that the object at the head has a delay that has expired for the longest time
     * If no delay has expired, then there is no head element and poll()
     * method will return null
     * size() return the count of both expired and unexpired items !!!
     */

    public static void main(String[] args) {

    }
}
