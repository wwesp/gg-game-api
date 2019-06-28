package edu.missouriwestern.csmp.gg.base;

import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** a class representing tile-occupying entities in the game */
public abstract class Entity implements HasProperties {
	private final int id;
	private final Game game;
	private Direction heading;
	private final Map<String,String> properties;

	/**
	 * Constructs Entity from a {@link Game}
	 * @param game associated Game
	 */
	protected Entity(Game game, Map<String,String> properties) {
		this.game = game;
		game.addEntity(this);
		this.id = game.getEntityId(this);
		this.properties = properties;
	}

	@Override
	public Map<String,String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Returns the facing {@link Direction} of the Entity
	 * @return facing Direction
	 */
	public Direction getHeading() { return heading; }

	/**
	 * Updates the facing {@link Direction} of the Entity
	 * @param heading facing Direction
	 */
	public void setHeading(Direction heading) {
		this.heading = heading;
	}

	/** 
	 * returns the unique identifier within this game for this entity
     * @return this entity's ID
	 */
	public int getID() { return id; }

	/**
	 * Returns {@link Game} associated with this Entity
	 * @return associated Game
	 */
	public Game getGame() { return game; }

	/**
	 * Returns the String representation of the Entity type
	 * @return type
	 */
	public abstract String getType();

	/** returns a JSON representation of this tile and its properties
	 */
	@Override
	public String toString() {
		var gsonBuilder = new GsonBuilder();
		var gson = gsonBuilder.create();
		var m = new HashMap<String,Object>();
		var location = game.getEntityLocation(this);
		var container = game.getContainingEntity(this);
		if(location != null) {
			m.put("board", location.getBoard().getName());
			m.put("column", location.getColumn());
			m.put("row", location.getRow());
			m.put("heading", getHeading());
		} else if(container != null) {
			m.put("container", container.getID());
		}
		m.put("id", getID());
		m.put("type", getType());
		m.put("properties", properties);
		return gson.toJson(m);
	}
	
}
