
package org.helium.event;

import java.util.EventObject;

/**
 * An event object of helium is based on the Java standard {@link EventObject event}
 *
 * @since 2.7.5
 */
public abstract class Event extends EventObject {

    private static final long serialVersionUID = -1704315605423947137L;

    /**
     * The timestamp of event occurs
     */
    private final long timestamp;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public Event(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
