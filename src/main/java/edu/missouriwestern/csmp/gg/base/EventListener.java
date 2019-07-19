package edu.missouriwestern.csmp.gg.base;

import java.util.function.Consumer;

/** Interface for any class that listends for and acts on game events
 *
 */
// TODO: don't extend Consumer -- that's silly
// TODO: use annotations to provide filter on received event classes / types
public interface EventListener extends Consumer<Event> {

}
