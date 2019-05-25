package edu.missouriwestern.csmp.gg.base;

/** a class representing tile-occupying entities in the game */
public abstract class Entity {
	private int id;
	private Game game;
	private boolean dead = false;
	private Direction heading;
	private Player owner;

	/**
	 * Constructs Entity from a {@link Game} and player id for owner
	 * @param game associated Game
	 * @param owner id of owner
	 * @
	 */
	protected Entity(Game game, Player owner) {
		this.game = game;
		this.owner = owner;
		this.id = game.registerEntity(this);
	}
	
	/**
	 * Constructs Entity from a {@link Game}
	 * @param game associated Game
	 */
	protected Entity(Game game) {
		this.game = game;
		owner = null;
		this.id = game.registerEntity(this);
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
	 * Returns the facing {@link Direction} of the Entity
	 * @return facing Direction
	 */
	public boolean setHeading() {
		this.heading = heading;
		return true;
	}

	/** 
	 * returns the unique identifier for this robot
     * @return the unique identifier for this robot
	 */
	public int getID() { return id; }
	/**
	 * Returns {@link Game} associated with this Entity
	 * @return associated Game
	 */
	public Game getGame() { return game; }
	/**
	 * Returns true if Entity is not dead
	 * @return true if alive, false if dead
	 */
	public boolean isAlive() { return !dead; }
	/**
	 * Kills the Entity and removes it from the Game
	 */
	public void die() {
        if(dead) return;
		dead = true;
		game.removeEntity(this);
	}
	
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
