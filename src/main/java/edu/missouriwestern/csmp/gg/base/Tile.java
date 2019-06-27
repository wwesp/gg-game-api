package edu.missouriwestern.csmp.gg.base;

import com.google.gson.GsonBuilder;
import edu.missouriwestern.csmp.gg.base.events.TileStateUpdateEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/** Represents spaces on the Board */
public final class Tile implements Container, HasProperties {

	private final Board board;
	private final Location location;
	private final String type;
	private final HashMap<Integer,Entity> entities = new HashMap<>();
	private final Map<String,String> properties;

	/**
	 * Constructs a tile from a given {@link Board} at a given {@link Location} with the given character representation
	 * @param board given Board
	 * @param location initial Location
	 * */
	protected Tile(Board board, Location location, String type, Map<String,String> properties) {
		this.board = board;
		this.location = location;
		this.type = type;
		this.properties = new ConcurrentHashMap<>(properties);
	}

	/**
	 * Constructs a tile from a given {@link Board} at a given {@link Location} with the given character representation
	 * @param board given Board
	 * @param location initial Location
	 * */
	protected Tile(Board board, Location location, String type) {
		this(board, location, type, new HashMap<>());
	}
	
	/**
	 * Returns the {@link Location} of the tile
	 * @return tile location
	 */
	public Location getLocation() {
		return location;
	}


	public String getType() { return type; }

	/**
	 * Return the {@link Board} associated with this tile
	 * @return associated Board
	 */
	public Board getBoard() { return board;}
	/**
	 * Returns set of {@link Entity} that it contains
	 * @return occupying set of entities or an empty set if there are not any.
	 */
	public Stream<Entity> getEntities() {return entities.values().stream(); }

	/**
	 * Sets an {@link Entity} to occupy this tile.
	 * <p>
	 * Also removes any previously occupying Entity.
	 * @param ent Entity to occupy this Tile
	 * @return weather entry into tile was successful.
	 */
	public void addEntity(Entity ent) {
		var t = getBoard().getTile(ent);
		if(t.isPresent()){
			t.get().removeEntity(ent); //Removes entity from previous tile.
		}

		entities.put(ent.getID(),ent);
	}
	
	/**
	 * Returns an {@link Entity} with a given id on this Tile if such an Entity exists
	 * otherwise returns null
	 * @param id
	 * @return Entity or null
	 */
	public Entity getEntity(int id) {
		return entities.get(id);
	}
	
	/**
	 * Returns true if the tile contains a given entity.
	 * @param ent
	 * @return boolean
	 * 	 */
	public boolean containsEntity(Entity ent){
		return entities.containsKey(ent);
	}
	
	/**
	 * Returns true if the tile contains an entity of a given type
	 * @return boolean
	 * 	 */
	public boolean containsEntityType(String type){
		return entities.values().stream()
				.anyMatch(ent -> ent.getType().equals(type));
	}
	
	/**
	 * Removes an {@link Entity} from this Tile
	 * @param ent
	 */
	public void removeEntity(Entity ent) {
		entities.remove(ent.getID());
		Container.super.removeEntity(ent);
	}

	/**
	 * Returns whether this tile is empty or occupied by at least one {@link Entity}
	 * @return whether this tile is empty
	 */
	public boolean isEmpty() { return entities.isEmpty();}

	@Override
	public Map<String,String> getProperties() {
		return properties;
	}

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
		board.accept(new TileStateUpdateEvent(location));
	}

	/** returns a JSON representation of this tile and its properties
	 */
	@Override
	public String toString() {
		var gsonBuilder = new GsonBuilder();
		var gson = gsonBuilder.create();
		var m = new HashMap<String,Object>();
		m.put("row", getLocation().getRow());
		m.put("column", getLocation().getColumn());
		m.put("board", getBoard().getName());
		m.put("type", type);
		m.put("properties", properties);
		return gson.toJson(m);
	}

}
