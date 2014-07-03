package com.rc.event_hub;


/**
 * Created by akshay on 5/18/13.
 * Interface that should be implemented by the action producer for event hub
 */
public interface EventHubAction<T> {
    public String getId();

    public T getActionData();
}
