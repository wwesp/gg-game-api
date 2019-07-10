package edu.missouriwestern.csmp.gg.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimaps;
import com.google.gson.GsonBuilder;
import net.sourcedestination.funcles.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

/** represents the playing board */
public class Board implements EventProducer {
	private static Logger logger = Logger.getLogger(EventProducer.class.getCanonicalName());

	private final Map<Pair<Integer>, Tile> tiles;
	private final BiMap<Character,String> tileTypeChars;
	private final Map<EventListener,Object> listeners = new ConcurrentHashMap<>();
			// no concurrent set, so only keys used to mimic set
	private final String name;
	private final Game game;

	/** outfits board according to layout of characters in multi-line string charMap.
	 * Characters that are not keys in {@param tileTypeChars} can be used to
	 * represent blank space in the map (no tile will be generated). Blank spaces are always treated
	 * as empty cells and cannot be used.
	 *
	 * @param tileTypeChars
	 * @param game
	 * @param name
	 * @param charMap
	 * @param tileProperties
	 */
	public Board(Map<Character, String> tileTypeChars, Game game, String name, String charMap,
                 Map<Character, Map<String,String>> tileTypeProperties,
                 Map<Pair<Integer>, Map<String,String>> tileProperties) {
		var tiles = new HashMap<Pair<Integer>,Tile>();
		int col=0, row=0;
		for(char c : charMap.toCharArray()) {
			if(c == '\n') { // reset to next row
				row++; // increment row
				col = 0; // start at first column
			} else  {  // create a tile in this column
				if(tileTypeChars.containsKey(c)) {
                    var properties = new HashMap<String,String>();

                    if(tileTypeProperties.containsKey(c))  // if properties for tile type were specified
                        properties.putAll(tileTypeProperties.get(c));

                    if(tileProperties.containsKey(Pair.makePair(col, row)))  // if properties for this location were specified
                        properties.putAll(tileProperties.get(Pair.makePair(col, row)));
                    var tile = new Tile(this, col, row, tileTypeChars.get(c), properties);
					tiles.put(Pair.makePair(col, row), tile);
				}
				col++; // increment column
			}
		}
		this.game = game;
		this.name = name;
		this.tileTypeChars = HashBiMap.create(tileTypeChars);
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
	 * @return
	 */
	private boolean locationExists(int column, int row){
		return tiles.containsKey(Pair.makePair(column, row));
	}

	/**
	 * Find an adjacent {@link Tile} given a Tile and {@link Direction}
	 * @param tile original Tile
	 * @param direction direction of adjacent tile
	 * @return adjacent Tile
	 */
	public Tile getAdjacentTile(Tile tile, Direction direction) {
		var row = tile.getRow();
		var column = tile.getColumn();
		switch (direction) {
			case NORTH: row--; break;
			case SOUTH: row++; break;
			case WEST: column--; break;
			case EAST: column++; break;
		}
		if (locationExists(column, row))
			return getTile(column, row);
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
		if(from.getColumn() > to.getColumn())
			return Direction.WEST;
		if(from.getColumn() < to.getColumn())
			return Direction.EAST;
		if(from.getRow() > to.getRow())
			return Direction.NORTH;
		return Direction.SOUTH;
	}
	
	/**
	 * Returns HashMap of {@link Tile}s associated with this Board
	 * @return HashMap of tiles
	 */
	public Map<Pair<Integer>, Tile> getTiles() { return tiles; }
	
	/**
	 * Returns stream of {@link Tile}s associated with this Board
	 * @return stream of all tiles associated with the board
	 */
	public Stream<Tile> getTileStream() { return tiles.values().stream(); }

	/**
	 * Returns a {@link Tile} at the given coordinates
	 * @return tile at given location
	 */
	public Tile getTile(int column, int row) {
		if(!locationExists(column, row))
			return null;
		return tiles.get(Pair.makePair(column, row));
	}

	/**
	 * Returns a {@link Tile} with the given {@link Entity}
	 * @param ent - entity that exists on tile
	 * @return tile that contains given entity
	 */
	public Optional<Tile> getTile(Entity ent){
		var container = getGame().getEntityLocation(ent);
		if(container instanceof Tile) {
			return Optional.of((Tile)container);
		}
		return Optional.empty();
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return tiles.keySet().stream()
				.mapToInt(Pair::_1)
				.max().getAsInt() + 1;
	}

	public int getHeight() {
		return tiles.keySet().stream()
				.mapToInt(Pair::_2)
				.max().getAsInt() + 1;
	}


	/** returns a multi-line string representing the layout of tile types on this board.
	 * The names of classes represented by different characters in this string are held in tileTypeChars.
	 * @return
	 */
	public String getTileMap() {

		StringBuffer sb = new StringBuffer();
		for(int r = 0; r < getHeight(); r++) {
			for(int c = 0; c < getWidth(); c++) {
				var location = Pair.makePair(c, r);
				if(tiles.containsKey(location))
					sb.append(tileTypeChars.inverse().get(tiles.get(location).getType()));
				else sb.append(' ');
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	/** returns a JSON representation of this board and its properties
	 */
	@Override
	public String toString() {
		var gsonBuilder = new GsonBuilder();
		var gson = gsonBuilder.create();
		var m = new HashMap<String,Object>();
		m.put("height", getHeight());
		m.put("width", getWidth());
		m.put("tilemap", getTileMap());
		m.put("tileTypes", tileTypeChars);
		return gson.toJson(m);
	}
}

