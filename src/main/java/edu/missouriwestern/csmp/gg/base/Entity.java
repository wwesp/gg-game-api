package edu.missouriwestern.csmp.gg.base;

import java.util.Collections;
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
		this.properties = Collections.unmodifiableMap(properties);
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

	public String toString() {
		return "{ \"id\": " + id +
				", \"type\": " + getClass().getSimpleName() +
				(getGame().getEntityLocation(this) != null ? // does this entity have a location?
						", \"location\": " + getGame().getEntityLocation(this) +
						", \"heading\": " + getHeading() :
						"") + // if not, don't include it or the heading in the JSON
				", \"properties\": " + this.serializeProperties() +
				"}";
	}
	
}
