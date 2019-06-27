package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Location;

import java.util.Map;

public class TileStateUpdateEvent extends Event {
    public TileStateUpdateEvent(Location loc) {
        super(loc.getBoard().getGame(),
                loc.getBoard().getGame().getNextEventId(),
                createProperies(loc));
    }

    public static Map<String,String> createProperies(Location loc) {
        var m = Map.of(
                "board", loc.getBoard().getName(),
                "column", ""+loc.getColumn(),
                "row", ""+loc.getRow()
        );

        return m;
    }
}
