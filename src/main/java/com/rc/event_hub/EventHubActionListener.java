package com.rc.event_hub;


/**
 * Created by akshay on 5/18/13.
 * Event hub action listener
 */
public interface EventHubActionListener<T> {

    public void actionHappened(final T actionEvent);

    public void actionUnRegistered(final String actionId);
}
