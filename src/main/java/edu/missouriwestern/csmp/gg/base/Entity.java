package edu.missouriwestern.csmp.gg.base;

import java.util.Collections;
import java.util.Map;

/** a class representing tile-occupying entities in the game */
public abstract class Entity {
	private final int id;
	private final Game game;
	private Direction heading;
	private Player owner;
	private final String type;
	private final Map<String,String> properties;


	/**
	 * Constructs Entity from a {@link Game}
	 * @param game associated Game
	 */
	protected Entity(Game game, String type, Map<String,String> properties) {
		this.game = game;
		this.type = type;
		game.addEntity(this);
		this.id = game.getEntityId(this);
		this.properties = Collections.unmodifiableMap(properties);
	}

	/**
	 * Constructs Entity from a {@link Game} and player id for owner
	 * @param game associated Game
	 * @param owner id of owner
	 * @
	 */
	protected Entity(Game game, String type, Map<String,String> properties, Player owner) {
		this(game, type, properties);
		this.owner = owner;
	}
	
	/**
	 * returns the owner of the entity
	 * @return the owner of the entity
	 */
	public Player getOwner() { return owner; }
	
	/**
	 * Sets the owner of the entity
	 * @param owner the player
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
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

	/**
	 * Move the Entity to a given {@link Location}
	 * @param l location of move
	 * @return whether move was successful
	 */
	public abstract boolean move(Location l);

	/**
	 * Asks if the the entity can share the tile.
	 * @param ent pushing entity if any
	 * @return whether the entity allows sharing of the tile.
	 * 
	 */
	public abstract boolean shareTile(Entity ent);
	
}
