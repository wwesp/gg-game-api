package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.*;

import java.util.HashMap;
import java.util.Map;

/** Issued whenever an {@link Entity} moves from one container to another */
public class EntityMovedEvent extends Event {

    private final Entity entity;
    private final Container previousContainer;

    public EntityMovedEvent(Entity ent, Container previous) {
        super(ent.getGame(),
                createProperies(ent, previous));
        this.entity = ent;
        this.previousContainer = previous;
    }

    public Entity getEntity() { return entity; }
    public Container getPreviousContainer() { return previousContainer; }

    public static Map<String,String> createProperies(Entity ent, Container previous) {
        var m = new HashMap<String,String>();
        m.put("entity", ""+ent.getID());
        if(previous instanceof Tile) {
            m.put("board", ""+((Tile)previous).getBoard().getName());
            m.put("row", ""+((Tile)previous).getRow());
            m.put("column", ""+((Tile)previous).getColumn());
        } else if(previous instanceof Entity) {
            m.put("container", ""+((Entity)ent).getID());
        }

        return m;
    }
}
