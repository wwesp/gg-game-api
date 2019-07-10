package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.*;

import java.util.HashMap;
import java.util.Map;

public class EntityMovedEvent extends Event {
    public EntityMovedEvent(Entity ent, Container previous) {
        super(ent.getGame(),
                ent.getGame().getNextEventId(),
                createProperies(ent, previous));
    }

    public static Map<String,String> createProperies(Entity ent, Container previous) {
        var m = new HashMap<String,String>();
        m.put("entity", ""+ent.getID());
        if(previous instanceof Tile) {
            m.put("row", ""+((Tile)previous).getRow());
            m.put("column", ""+((Tile)previous).getColumn());
        } else if(previous instanceof Entity) {
            m.put("container", ""+((Entity)ent).getID());
        }

        return m;
    }
}
