package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.Entity;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;

import java.util.Map;

public class EntityCreation extends Event {
    public EntityCreation(Game game, int id, Entity ent) {
        super(game, id, createProperies(ent));
    }
    public EntityCreation(Game game, Entity ent) {
        super(game, createProperies(ent));
    }

    public static Map<String,String> createProperies(Entity ent) {
        var m = Map.of(
                "game", ent.getGame().getClass().getSimpleName(),
                "entity", ""+ent.getID()
        );

        return m;
    }
}
