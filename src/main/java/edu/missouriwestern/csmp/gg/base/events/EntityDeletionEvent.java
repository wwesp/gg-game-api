package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.Entity;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;

import java.util.Map;

/** Issued whenever an {@link Entity} is removed from the game */
public class EntityDeletionEvent extends Event {

    private Entity entity;

    public EntityDeletionEvent(Game game, Entity ent) {
        super(game, createProperies(ent));
        this.entity = ent;
    }

    public Entity getEntity() { return entity; }

    public static Map<String,String> createProperies(Entity ent) {
        var m = Map.of(
                "game", ent.getGame().getClass().getSimpleName(),
                "entity", ""+ent.getID()
        );

        return m;
    }
}
