package edu.missouriwestern.csmp.gg.base;

import edu.missouriwestern.csmp.gg.base.event.EntityLeftEvent;
import edu.missouriwestern.csmp.gg.base.factory.EntityFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/** a class representing tile-occupying entities in the game */
public abstract class Entity {
	private int id;
	private Game game;
	private boolean dead = false;
	private BufferedImage img;
	private URL imgURL;
	private Direction heading;
	private Player owner;

	/**
	 * Map of Entity names to their factories
	 */
	private static final Map<String, EntityFactory> entityFactories = new HashMap<>();
	/**
	 * Registers {@link EntityFactory} by associating it with an Entity type
	 * @param type name/type of the Entity
	 * @param factory associated EntityFactory
	 */
	public static void registerEntityFactory(String type, EntityFactory factory) {
		entityFactories.put(type, factory);
	}
	
	/**
	 * Creates an entity from a given type, {@link Game}, and {@link PlayerConnection}
	 * @param type Entity type
	 * @param game associated Game
     * @param game associated Player
	 * @return created Entity
	 * @throws NoFactoryFoundException if no associated {@link EntityFactory} was found
	 */
	public static Entity createEntity(String type, Game game, Player player)  {
		if(entityFactories.containsKey(type))
			return entityFactories.get(type).buildEntity(game, player);
		throw new RuntimeException("Entity Factory of type '" + type + "'not found!");
	}
	
	/**
	 * Constructs Entity from a {@link Game} and {@link URL} of image and player id for owner
	 * @param game associated Game
	 * @param imgURL image URL
	 * @param owner id of owner
	 * @
	 */
	protected Entity(Game game, URL imgURL, Player owner) { 
		this.game = game;
		this.imgURL = imgURL;
		this.owner = owner;
		try {
			img = ImageIO.read(imgURL);
		} catch (IOException e) {System.out.println("bad URL");}
		this.id = game.registerEntity(this);
	}
	
	/**
	 * Constructs Entity from a {@link Game} and {@link URL} of image
	 * @param game associated Game
	 * @param imgURL image URL
	 */
	protected Entity(Game game, URL imgURL) { 
		this.game = game;
		this.imgURL = imgURL;
		owner = null;
		try {
			img = ImageIO.read(imgURL);
		} catch (IOException e) {}
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
	 * returns the unique identifier for this robot
     * @return the unique identifier for this robot
	 */
	public int getID() { return id; }
	/**
	 * Returns {@link BufferedImage} associated with this Entity
	 * @return associated image
	 */
	public BufferedImage getImage() { return img; }
	/**
	 * Returns image {@link URL}
	 * @return image URL
	 */
	public URL getImageURL() { return imgURL; }
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
	 * @see Game#removeEntity(Entity)
	 * @see EntityLeftEvent
	 * 
	 * TODO: Consider eliminating the "dead" field
	 * TODO: rename?
	 */
	public void die() {
        if(dead) return;
		dead = true;
		game.removeEntity(this);
		game.sendEvent(new EntityLeftEvent(game, null, this)); //sends to all players.
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
