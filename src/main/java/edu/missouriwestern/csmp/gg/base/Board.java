package edu.missouriwestern.csmp.gg.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

/** represents the playing board */
public class Board {

	private Map<Location, Tile> tiles = new HashMap<Location, Tile>();
	private Game game;

	/**
	 * Board Constructor
	 * @param tiles {@link Tile} map with {@link Location}n as the key
	 */
	public Board(HashMap<Location, Tile> tiles) {
		this.tiles = tiles;
	}
	/**
	 * Returns the {@link Game} associated with this board
	 * @return associated Game
	 */
	public Game getGame() { return game; }
	/**
	 * Sets the {@link Game} associated with this Board
	 * @param game the game to be set
	 */
	void setGame(Game game) {
		this.game = game;
	}
	
	/**
	 * Checks if Location exists on board.
	 * @param loc
	 * @return
	 */
	//TODO: Test this method
	private boolean locationExists(Location loc){
		return tiles.containsKey(loc);
	}
	/**
	 * Find an adjacent {@link Tile} given a Tile and {@link Direction}
	 * @param tile original Tile
	 * @param direction direction of adjacent tile
	 * @return adjacent Tile
	 */
	public Tile getAdjacentTile(Tile tile, Direction direction) {
		Location adjLoc = tile.getLocation().getAdjacentLocation(direction);
		if (locationExists(adjLoc))
			return getTile(adjLoc);
		return null;
	}
	/**
	 * Find adjacent Tile given a Location and Direction 
	 * @param loc location of original tile
	 * @param direction direction of adjacent tile
	 * @return adjacent Tile
	 */
	public Tile getAdjacentTile(Location loc, Direction direction) {
		Location adjLoc = loc.getAdjacentLocation(direction);
		if(locationExists(adjLoc))
			return getTile(adjLoc);
		return null;
	}
	/**
	 * Returns the {@link Direction} between two {@link Tile}s
	 * <p>
	 * Returns the direction tile 'to' is relative to tile 'from'.
	 * If the tiles are not adjacent, this method returns one direction
	 * 'to' is relative to 'from', but not necessarily all of them. 
	 * 
	 * @param from starting Tile
	 * @param to   ending Tile
	 * @return direction from starting Tile to ending Tile
	 */
	public Direction getAdjacentTileDirection(Tile from, Tile to) {
		if(from.getLocation().getColumn() > to.getLocation().getColumn())
			return Direction.WEST;
		if(from.getLocation().getColumn() < to.getLocation().getColumn())
			return Direction.EAST;
		if(from.getLocation().getRow() > to.getLocation().getRow())
			return Direction.NORTH;
		return Direction.SOUTH;
	}
	
	/**
	 * Returns HashMap of {@link Tile}s associated with this Board
	 * @return HashMap of tiles
	 */
	public Map<Location, Tile> getTiles() { return tiles; }
	
	/**
	 * Returns Collection of {@link Tile}s associated with this Board
	 * @return collection of tiles
	 */
	public Collection<Tile> getTileCollection() { return tiles.values(); }
	/**
	 * Returns a {@link Tile} at the given {@link Location}
	 * @param location location of tile
	 * @return tile at given location
	 */
	public Tile getTile(Location location) {
		if(!tiles.containsKey(location))
			return null;
		return tiles.get(location);
	}
	/**
	 * Returns a {@link Tile} with the given {@link Entity}
	 * @param ent - entity that exists on tile
	 * @return tile that contains given entity
	 */
	public Tile getTile(Entity ent){
		ArrayList<Tile> tempTiles = new ArrayList<Tile>(tiles.values());
		for (Tile t: tempTiles){
			HashSet<Entity> entMap = t.getEntities();
			for (Entity e: entMap)
				if (e == ent)
					return t;
		}
		return null; //Tile does not exist with given entity.
	}
}

