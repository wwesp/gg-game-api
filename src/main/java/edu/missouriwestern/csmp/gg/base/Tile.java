package edu.missouriwestern.csmp.gg.base;

import net.sourcedestination.funcles.tuple.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

import javax.imageio.ImageIO;

import apg.game.event.MovedEvent;
import apg.game.factory.TileFactory;
import apg.messages.NoFactoryFoundException;


/** Represents spaces on the Board */
public abstract class Tile implements Container {

	private Board board;
	@Deprecated private Location location;
	private HashSet<Entity> entities = new HashSet<Entity>();
	private char idChar = '.';

	public static final int DRAWING_SIZE = 128;
	private static final HashMap<Tuple2<Class<?extends Game>, Character>, BufferedImage> tileImages = new HashMap<>();
	private static final HashMap<Tuple2<Class<?extends Game>, Character>, URL> tileImageURLs = new HashMap<>();
	private static final HashMap<Tuple2<Class<?extends Game>, Character>, TileFactory> tileFactories = new HashMap<>();

	/**
	 * Registers a {@link TileFactory} for constructing tiles, with the given character representation
	 * @param type character representation of the Tile for some game
	 * @param factory associated TileFactory
	 */
	public static void registerTileFactory(T2<Class<?extends Game>, Character> type, TileFactory factory) {
		tileFactories.put(type, factory);
	}
	/**
	 * Constructs a new tile matching the given character representation on the given {@link Board}, at the given {@link Location}
	 * @param gameClass gameClass for this tile
	 * @param c character representation of the Tile
	 * @param board given Board
	 * @param location given Location
	 * @return created Tile
	 * @throhttp://localhost:8080/APG/robots-logo.pngws NoFactoryFoundException no matching {@link TileFactory} was found
	 */
	public static Tile readTile(Class<?extends Game> gameClass,char c, Board board, Location location) throws NoFactoryFoundException {
		if(tileFactories.containsKey(new T2<Class<?extends Game>, Character>(gameClass,c))) 
			return tileFactories.get(new T2<Class<?extends Game>, Character>(gameClass,c)).buildTile(board, location);
		throw new NoFactoryFoundException("Tile Factory of for '" + c + "'not found!");
	}
	/**
	 * Associates an image {@link URL} with a given tile representation
	 * @param symbol character representation of a Tile
	 * @param imageURL image URL
	 */
	public static void registerTileImage(Class<?extends Game> gameClass, char symbol, URL imageURL) {
		tileImageURLs.put(new T2<Class<?extends Game>, Character>(gameClass,symbol), imageURL);
		BufferedImage img = null;
		try {
			img = ImageIO.read(imageURL);
		} catch (IOException e) {}
		tileImages.put(new T2<Class<?extends Game>, Character>(gameClass,symbol), img);
	}
	/**
	 * Returns tile {@link BufferedImage}
	 * @return tile image
	 */
	public BufferedImage getImage() {
		if(tileImages.containsKey(new T2<Class<?extends Game>, Character>(getBoard().getGame().getClass(), idChar))) 
			return tileImages.get(new T2<Class<?extends Game>, Character>(getBoard().getGame().getClass(),idChar));
		return null;
	}
	/**
	 * Returns a map of character representation to game classes with image {@link URL}s
	 * @return character/game class to image URL map
	 */
	public static HashMap<T2<Class<?extends Game>, Character>, URL>  getImageURLs() {
		return tileImageURLs;
	}
	/**
	 * Returns Tile image urls for given game class
	 * @param gameClass game class
	 * @return collection of image urls
	 */
	public static Collection<URL> getImageURLs(Class<?extends Game> gameClass) {
		ArrayList<URL> urls = new ArrayList<URL>();
		for(Entry<T2<Class<? extends Game>, Character>, URL> e : tileImageURLs.entrySet()) {
			if(e.getKey().a1().equals(gameClass))
				urls.add(e.getValue());
		}
		return urls;
	}
	/**
	 * Constructs a tile from a given {@link Board} at a given {@link Location} with the given character representation
	 * @param board given Board
	 * @param location initial Location
	 * @param c character representation
	 */
	protected Tile(Board board, Location location, char c) {
		this.board = board;
		this.location = location; 
		this.idChar = c;
	}
	
	/**
	 * Returns the {@link Location} of the tile
	 * @return tile location
	 */
	public Location getLocation() {
		HashMap<Location, Tile> temp = getBoard().getTiles();
		for (Entry<Location, Tile> entry : temp.entrySet()) {
	        if (this.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
		return null;
	}
	/**
	 * Return the {@link Board} associated with this tile
	 * @return associated Board
	 */
	public Board getBoard() { return board;}
	/**
	 * Returns set of {@link Entity} that it contains
	 * @return occupying set of entities or an empty set if there are not any.
	 */
	public HashSet<Entity> getEntities() {return entities;}

	/**
	 * Sets an {@link Entity} to occupy this tile.
	 * <p>
	 * Also removes any previously occupying Entity.
	 * @param ent Entity to occupy this Tile
	 * @return weather entry into tile was successful.
	 */
	public boolean addEntity(Entity ent) {
		Tile t = getBoard().getTile(ent);
		if(ent != null) {
			if(t != null){
				t.removeEntity(ent);//Removes entity from previous tile.
				getBoard().getGame().sendEvent(new MovedEvent(getBoard().getGame(), null, ent, this));
			}
		}
		return entities.add(ent);
	}
	
	/**
	 * Returns an {@link Entity} with a given id on this Tile if such an Entity exists
	 * otherwise returns null
	 * @param int entity id
	 * @return Entity or null
	 */
	public Entity getEntity(int id) {
		for(Entity e : entities)
			if(e.getID() == id)
				return e;
		return null;
	}
	
	/**
	 * Returns true if the tile contains a given entity.
	 * @param Entity e
	 * @return boolean
	 * 	 */
	public boolean containsEntity(Entity e){
		return entities.contains(e);
	}
	
	/**
	 * Returns true if the tile contains an entity of a given type
	 * @param Entity e
	 * @return boolean
	 * 	 */
	public boolean containsEntityType(String type){
		for (Entity ent: entities)
			if (ent.getType().equals(type))
				return true;
		return false;
	}
	
	/**
	 * Removes an {@link Entity} from this Tile
	 * @param entity
	 */
	public boolean removeEntity(Entity ent) {return entities.remove(ent);}
	/**
	 * Returns whether this tile is occupied by an {@link Entity}
	 * @return whether this tile is occupied
	 */
	public boolean containsTileEntity() { return !entities.isEmpty();}
	/**
	 * Returns char representation of this Tile
	 * @return char representation
	 */
	public char toChar() { return idChar; }
	/**
	 * Set the char representation of this Tile
	 * @param c char representation
	 */
	public void setChar(char c) { this.idChar = c; }
	
	/**
	 * {@link String} representation of a Tile
	 */
	public String toString() { return "["+toChar()+":"+ getLocation().toString()+"]"; }
	
	/**
	 * Draws the Tile given a Graphics context
	 * @param g Graphics context
	 */
	public void drawTile(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(getImage(), (getLocation().getColumn())*DRAWING_SIZE, (getLocation().getRow())*DRAWING_SIZE, 128, 128, null);
		int numEnts = getEntities().size();
		if(numEnts > 1) {
			@SuppressWarnings("unchecked")
			HashSet<Entity> cEntities = (HashSet<Entity>) getEntities().clone();
			int rows = (int) Math.floor(Math.sqrt(numEnts));
			int newsize = DRAWING_SIZE / 2;
			while(rows*newsize > DRAWING_SIZE && newsize > 5)
				newsize = newsize / 2;
			/*for(int x=0; x < DRAWING_SIZE / newsize; x++) {
				for(int y=0; y < DRAWING_SIZE / newsize && cEntities.size() > 0; y++) {
					Entity ent = cEntities.getrandomelement
					cEntities.remove(ent);
					g.drawImage(ent.getImage(), (getLocation().getColumn())*DRAWING_SIZE + newsize*x, 
							(getLocation().getRow())*DRAWING_SIZE + newsize*y, 
							newsize, newsize, null);
				}
			}*/
			int x = 0, y = 0;
			for(Entity ent: cEntities){
				g.drawImage(ent.getImage(), (getLocation().getColumn())*DRAWING_SIZE + newsize*x, 
						(getLocation().getRow())*DRAWING_SIZE + newsize*y, 
						newsize, newsize, null);
				if(++x > DRAWING_SIZE / newsize){
					x = 0;
					++y;
				}
			}
		}
		else if(numEnts == 1) {
			Entity ent = null;
			for (Entity e: getEntities()){
				ent = e;
			}
			g.drawImage(ent.getImage(), 
					(getLocation().getColumn())*DRAWING_SIZE, (getLocation().getRow())*DRAWING_SIZE,DRAWING_SIZE, DRAWING_SIZE, null);
		}
		g.setColor(Color.WHITE);
		g.drawRect((getLocation().getColumn())*DRAWING_SIZE, (getLocation().getRow())*DRAWING_SIZE, DRAWING_SIZE, DRAWING_SIZE);
	}
}
