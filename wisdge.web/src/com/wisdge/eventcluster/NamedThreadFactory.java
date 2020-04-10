package com.wisdge.eventcluster;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link ThreadFactory} that sets names to the threads created by this factory. Threads created by this factory
 * will take names in the form of the string <code>namePrefix + " thread-" + threadNum</code> where <tt>threadNum</tt> is the 
 * count of threads created by this type of factory.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;

    /**
     * Constructor accepting the prefix of the threads that will be created by this {@link ThreadFactory}
     * 
     * @param namePrefix
     *            Prefix for names of threads
     */
    public NamedThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;

    }
    /**
     * Constructor accepting the prefix of the threads that will be created by this {@link ThreadFactory}
     *
     * @param namePrefix
     *            Prefix for names of threads
     */
    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    /**
     * Returns a new thread using a name as specified by this factory {@inheritDoc}
     */
    public Thread newThread(Runnable runnable) {
        final Thread thread = new Thread(runnable, namePrefix + " thread-" + threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        return thread;
    }

}
