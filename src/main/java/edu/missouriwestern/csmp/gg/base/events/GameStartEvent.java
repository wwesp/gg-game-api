package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;

/** Issued at the very start of the game */
public class GameStartEvent extends Event {
    public GameStartEvent(Game game) {
        super(game);
    }
}
