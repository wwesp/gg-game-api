package edu.missouriwestern.csmp.gg.base;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/** represents the playing board */
public class Board implements EventProducer {

	private final Map<Location, Tile> tiles = new HashMap<>();
	private final Map<EventListener,Object> listeners = new ConcurrentHashMap<>();
			// no concurrent set, so only keys used to mimic set
	private final String name;
	private final Game game;

	/**
	 * Board Constructor
	 * @param tiles {@link Tile} map with {@link Location}n as the key
	 */
	public Board(HashMap<Location, Tile> tiles, Game game, String name) {
		this.game = game;
		this.name = name;
		this.tiles.putAll(tiles);
	}

	@Override
	public void registerListener(EventListener listener) {
		listeners.put(listener, null);
	}

	@Override
	public void deregisterListener(EventListener listener) {
		listeners.put(listener, null);
	}

	@Override
	public Stream<EventListener> getListeners() {
		return listeners.keySet().stream();
	}

	/**
	 * Returns the {@link Game} associated with this board
	 * @return associated Game
	 */
	public Game getGame() { return game; }
	
	/**
	 * Checks if Location exists on board.
	 * @param loc
	 * @return
	 */
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
		var adjLoc = tile.getLocation().getAdjacentLocation(direction);
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
		var adjLoc = loc.getAdjacentLocation(direction);
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
	 * Returns stream of {@link Tile}s associated with this Board
	 * @return stream of all tiles associated with the board
	 */
	public Stream<Tile> getTileStream() { return tiles.values().stream(); }

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
	public Optional<Tile> getTile(Entity ent){
		return tiles.values().stream()
				.filter(t -> t.containsEntity(ent))
				.findFirst();
	}

	public String getName() {
		return name;
	}
}

