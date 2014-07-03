package com.rc.event_hub;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by akshay on 5/18/13.
 * A class where events are produced and received
 * Its a singleton class
 */
public final class EventHub {
    /**
     * TAG for logging
     */
    private static final String TAG = "###EventHub###";
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(EventHub.TAG);
    /**
     * HashMap for events
     */
    private HashMap<String, ArrayList<EventHubActionListener>> mEventHubMembers;
    /**
     * Event hub actions
     */
    private HashMap<String, EventHubAction> mEventHubActions;
    /**
     * Instance
     */
    private static EventHub mInstance;
    /**
     * Action dispatchers
     */
    private ExecutorService mEventHubExecutor;

    /**
     * Constructor
     */
    private EventHub() {
        mEventHubMembers = new HashMap<String, ArrayList<EventHubActionListener>>();
        mEventHubActions = new HashMap<String, EventHubAction>();
        mEventHubExecutor = Executors.newFixedThreadPool(3);
    }

    /**
     * Factory method to get instance of event hub
     *
     * @return Instance of EventHub
     */
    public static EventHub getInstance() {
        if (mInstance == null)
            mInstance = new EventHub();
        return mInstance;
    }

    /**
     * Method to register an event hub action
     *
     * @param eventHubAction event hub action
     */
    public void registerEventHubAction(final EventHubAction eventHubAction) {
        logger.debug("Registering event hub action %d", eventHubAction.getId());
        mEventHubActions.put(eventHubAction.getId(), eventHubAction);
        if (mEventHubMembers.containsKey(eventHubAction.getId()))
            return;
        mEventHubMembers.put(eventHubAction.getId(), new ArrayList<EventHubActionListener>());
    }

    /**
     * Method to clear event hub
     */
    public void clearEventHub() {
        mEventHubActions.clear();
        mEventHubMembers.clear();
    }

    /**
     * Method to un-register event hub action
     *
     * @param eventHubActionId event hub action id
     */

    public void unregisterEventHubAction(final String eventHubActionId) {
        logger.debug("Unregistering event hub action %d", eventHubActionId);
        if (!mEventHubMembers.containsKey(eventHubActionId))
            return;
        mEventHubExecutor.submit(new Runnable() {
            @Override
            public void run() {
                ArrayList<EventHubActionListener> eventHubActionListeners = mEventHubMembers.get(eventHubActionId);
                for (EventHubActionListener eventHubActionListener : eventHubActionListeners) {
                    logger.debug("Sending event action un-registering");
                    eventHubActionListener.actionUnRegistered(eventHubActionId);
                }
                mEventHubMembers.remove(eventHubActionId);
                mEventHubActions.remove(eventHubActionId);
            }
        });
    }

    /**
     * Method to publish action
     *
     * @param eventHubAction event hub action
     */
    public void publishActionHappened(final EventHubAction eventHubAction) {
        logger.debug("Publishing action happened " + eventHubAction.getId());
        if (!mEventHubMembers.containsKey(eventHubAction.getId()))
            return;
        mEventHubExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mEventHubActions.put(eventHubAction.getId(), eventHubAction);
                ArrayList<EventHubActionListener> eventHubActionListeners = mEventHubMembers.get(eventHubAction.getId());
                logger.debug("Total listeners " + eventHubActionListeners.size());
                for (EventHubActionListener eventHubActionListener : eventHubActionListeners) {
                    logger.debug("Sending to listened " + eventHubActionListeners.indexOf(eventHubActionListener));
                    eventHubActionListener.actionHappened(eventHubAction.getActionData());
                }
            }
        });
    }


    /**
     * Method to register event hub action listener
     *
     * @param eventHubActionListener event hub action listener
     * @param actionId               action id
     * @return EventHubAction
     */
    public EventHubAction registerListenerForAction(final String actionId, final EventHubActionListener eventHubActionListener) {
        logger.debug("Registering listener for action " + actionId);
        if (!mEventHubActions.containsKey(actionId) || mEventHubMembers.containsKey(actionId)) {
            if (mEventHubMembers.containsKey(actionId)) {
                mEventHubMembers.get(actionId).add(eventHubActionListener);
            } else {
                ArrayList<EventHubActionListener> listeners =
                        new ArrayList<EventHubActionListener>();
                listeners.add(eventHubActionListener);
                mEventHubMembers.put(actionId, listeners);
            }
            return null;
        }
        mEventHubMembers.get(actionId).add(eventHubActionListener);
        return mEventHubActions.get(actionId);
    }

    /**
     * Method to unregister a listener for an action id
     *
     * @param actionId
     * @param eventHubActionListener
     */
    public void unregisterListener(final String actionId, final EventHubActionListener eventHubActionListener) {
        logger.debug("Un-registering event hub action listener");
        if (!mEventHubActions.containsKey(actionId)) {
            return;
        }
        if (mEventHubMembers != null) {
            final ArrayList<EventHubActionListener> event = mEventHubMembers.get(actionId);
            if (event != null)
                event.remove(eventHubActionListener);
        }
    }
}

