package edu.missouriwestern.csmp.gg.base;

import net.sourcedestination.funcles.function.Function3;
import net.sourcedestination.funcles.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/** represents the playing board */
public class Board implements EventProducer {

	private final Map<Location, Tile> tiles;
	private final Map<String,Character> tileTypeChars;
	private final Map<EventListener,Object> listeners = new ConcurrentHashMap<>();
			// no concurrent set, so only keys used to mimic set
	private final String name;
	private final Game game;

	/**
	 * Board Constructor
	 * @param tiles {@link Tile} map with {@link Location}n as the key
	 */
	public Board(Map<String, Character> tileTypeChars, Game game, String name, Map<Location, Tile> tiles) {
		this.game = game;
		this.name = name;
		this.tileTypeChars = Collections.unmodifiableMap(tileTypeChars);
		this.tiles = Collections.unmodifiableMap(tiles);
	}

	/** outfits board according to layout of characters in multi-line string charMap.
	 * Characters that are keys in {@param generators} have an associated tile generator and
	 * will have tiles generated for them. Characters that are not keys in this map can be used to
	 * represent blank space in the map (no tile will be generated). Blank spaces are always treated
	 * as empty cells and cannot be used.
	 *
	 * @param tileTypeChars
	 * @param game
	 * @param name
	 * @param charMap
	 * @param generators
	 * @param tileProperties
	 */
	public Board(Map<String, Character> tileTypeChars, Game game, String name, String charMap,
				 Map<Character, Function3<Board,Location,Map<String,String>,Tile>> generators,
				 Map<Pair<Integer>, Map<String,String>> tileProperties) {
		var tiles = new HashMap<Location,Tile>();
		int x=0, y=0;
		for(char c : charMap.toCharArray()) {
			if(c == '\n') { // reset to next row
				y++; // increment row
				x = 0; // start at first column
			} else if(tileTypeChars.containsKey(c)) {  // create a tile in this column
				var location = new Location(this, x, y); // location of this tile
				tiles.put(location,
						generators.get(c).apply(this, location, // generate a tile
						tileProperties.containsKey(Pair.makePair(x,y)) ?  // get properties if they exist
								tileProperties.get(Pair.makePair(x,y)) :
								new HashMap<String,String>()));
				x++; // increment column
			}
		}
		this.game = game;
		this.name = name;
		this.tileTypeChars = Collections.unmodifiableMap(tileTypeChars);
		this.tiles = Collections.unmodifiableMap(tiles);
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

	public int getWidth() {
		return tiles.keySet().stream()
				.mapToInt(Location::getRow)
				.max().getAsInt() + 1;
	}

	public int getHeight() {
		return tiles.keySet().stream()
				.mapToInt(Location::getColumn)
				.max().getAsInt() + 1;
	}


	/** returns a multi-line string representing the layout of tile types on this board.
	 * The names of classes represented by different characters in this string are held in tileTypeChars.
	 * @return
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int r = 0; r < getWidth(); r++) {
			for(int c = 0; c < getHeight(); c++) {
				Location location = new Location(this, r, c);
				if(tiles.containsKey(location))
					sb.append(tileTypeChars.get(tiles.get(location).getClass().getSimpleName()));
				else sb.append(' ');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}

