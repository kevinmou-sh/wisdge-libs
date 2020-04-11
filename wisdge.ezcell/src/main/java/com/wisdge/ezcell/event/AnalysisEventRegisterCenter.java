package com.wisdge.ezcell.event;

import java.util.List;

/**
 * Event center.
 */
public interface AnalysisEventRegisterCenter {

    /**
     * Append listener
     *
     * @param name     listener name.
     * @param listener Callback method after each row is parsed.
     */
    void appendLister(String name, AnalysisEventListener<?> listener);

    /**
     * Parse one row to notify all event listeners
     *
     * @param event parse event
     */
    void notifyListeners(List<Object> event);

    /**
     * Clean all listeners.
     */
    void cleanAllListeners();
}
