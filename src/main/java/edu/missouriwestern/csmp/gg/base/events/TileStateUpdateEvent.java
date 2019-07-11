package edu.missouriwestern.csmp.gg.base.events;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Tile;

import java.util.Map;

public class TileStateUpdateEvent extends Event {
    public TileStateUpdateEvent(Tile tile) {
        super(tile.getBoard().getGame(),
                createProperies(tile.getBoard(), tile.getColumn(), tile.getRow()));
    }

    public static Map<String,String> createProperies(Board board, int column, int row) {
        var m = Map.of(
                "board", board.getName(),
                "column", ""+column,
                "row", ""+row
        );

        return m;
    }
}
