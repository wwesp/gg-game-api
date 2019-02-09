package edu.missouriwestern.csmp.gg.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedReader;
import java.awt.Graphics2D;

import apg.game.event.OutDatedLocationEvent;
import apg.game.event.TileRevealEvent;

/** represents the playing board */
public class Board {

	private HashMap<Location, Tile> tiles = new HashMap<Location, Tile>();
	private int rows;
	private int columns;
	private Game game;
	
	/** 
	 * Reads in a board description from a BufferedReader
	 * @param game class
	 * @param in input BufferedReader
	 * @return the created Board object
	 * @throws IOException
	 */
	public static Board readBoard(Class<?extends Game> gameClass, BufferedReader in) throws IOException {
		HashMap<Location, Tile> tiles = null;
		String firstLine = in.readLine();
		System.out.println("FirstLine: " + firstLine);
		Scanner sin = new Scanner(firstLine);
		String validationStr = sin.next();
		if(!validationStr.equals("MAP"))
			throw new IllegalArgumentException("bad map format, must begin with 'MAP '");  
		//TODO: use a better exception model, generate error messages for command-line use
		int columns = sin.nextInt();
		int rows = sin.nextInt();
		tiles = new HashMap<Location, Tile>();
		Board board = new Board(rows, columns, tiles);
		String line = null; 
		for(int row=0; row < rows; row++) {
			line = in.readLine();
			if(line == null) 
				throw new IllegalArgumentException("bad map format, wrong number of rows");
			if(columns != line.length())
				throw new IllegalArgumentException("bad map format, non-uniform row lengths");
			for(int col=0; col<columns; col++) {
				Location l = new Location(col, row);
				Tile t = Tile.readTile(gameClass, line.charAt(col), board, l);
				tiles.put(l, t);
			}
		}
		return board;
	}

	/**
	 * Board Constructor
	 * @param rows number of rows
	 * @param cols number of columns
	 * @param tiles {@link Tile} map with {@link Location}n as the key
	 */
	public Board(int rows, int cols, HashMap<Location, Tile> tiles) {
		this.rows = rows;
		this.columns = cols;
		this.tiles = tiles;
	}

	/**
	 * Replaces the tile at the specified {@link Location} with given char representation
	 * @param l location on the board
	 * @param t char representation of the new tile
	 */
	public void replaceTile(Location l, char t) {
		tiles.remove(l);
		tiles.put(l, Tile.readTile(getGame().getClass(), t, this, l));
		getGame().sendEvent(new OutDatedLocationEvent(getGame(), null, l));
	}
	
	/**
	 * Replaces a tile at a given location with a new tile
	 * @param loc location of new tile
	 * @param tile new value for tile
	 */
	public void replaceTile(Location loc, Tile tile) {
		tiles.put(loc, tile);
		getGame().sendEvent(new OutDatedLocationEvent(getGame(), null, loc));
	}

	/**
	 * Returns number of rows in the board
	 * @return number of rows
	 */
	public int getRows() { return rows; }
	/**
	 * Returns number of columns
	 * @return number of columns
	 */
	public int getColumns() { return columns; }
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
		if (loc.getColumn() > getColumns()||loc.getColumn() < 0 || 
				loc.getRow() > getRows() || loc.getRow() < 0)
			return false;
		return true;
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
	public HashMap<Location, Tile> getTiles() { return tiles; }
	
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
	 * @param entity - entity that exists on tile
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
	/**
	 * String representation of the entire board.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("MAP " + getColumns() + " " + getRows() +"\n");
		/*for(int row = 0; row < getRows(); row++) {
			for(int col=0; col < getColumns(); col++) {
				Location l = new Location(col, row);
				buf.append(getTile(l).toChar());
			}
			buf.append("\n");
		}*/
		for(int row = 0; row<getRows();row++)
			for(int col=0;col<getColumns();col++){
				buf.append(new TileRevealEvent(getGame(),null,getTile(new Location(col,row))).getUpdateString());
				buf.append("\n");
			}
		return buf.toString();
	}
	/**
	 * Draws the Board given a graphics Context
	 * @param g graphics context
	 */
	public void draw(Graphics2D g) {
		for(Tile t : tiles.values()) {
			t.drawTile(g);
		}
	}
}

