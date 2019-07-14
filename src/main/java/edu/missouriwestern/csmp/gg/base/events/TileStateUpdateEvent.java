package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Tile;

import java.util.Map;

/** Event is triggered when an attribute of a tile changes.
 * Event is not triggered when an entity enters or leaves a tile
 */
public class TileStateUpdateEvent extends Event {

    private final Tile tile;

    public TileStateUpdateEvent(Tile tile) {
        super(tile.getBoard().getGame(),
                createProperies(tile.getBoard(), tile.getColumn(), tile.getRow()));
        this.tile = tile;
    }

    /** returns the {@link Tile} whose status was updated */
    public Tile getTile() { return tile; }

    public static Map<String,String> createProperies(Board board, int column, int row) {
        var m = Map.of(
                "board", board.getName(),
                "column", ""+column,
                "row", ""+row
        );

        return m;
    }
}
