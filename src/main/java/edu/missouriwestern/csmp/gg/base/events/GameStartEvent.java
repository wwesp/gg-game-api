package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;

public class GameStartEvent extends Event {
    public GameStartEvent(Game game) {
        super(game);
    }
}
