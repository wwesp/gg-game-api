package edu.missouriwestern.csmp.gg.base;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/** represents a player within the game
 * not an entity as a player may potentially comprise multiple entities within the game
 */
public class Player implements Container {

	private final Set<Entity> entities = new HashSet<>();
	private final int id;
	private final String name;

	public Player(int id, String name){
		this.id = id;
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addEntity(Entity e) {
		entities.add(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entity getEntity(int id) {
		for (Entity e: entities)
			if (e.getID() == id)
				return e;
		return null; //Entity not found.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeEntity(Entity ent) {
		entities.remove(ent);
		Container.super.removeEntity(ent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stream<Entity> getEntities() { return entities.stream(); }
	

	/**
	 * Returns the ID of the Player
	 * @return the ID of the Player
	 */
	public int getID(){ return id; }

	/**
	 * Returns the Player's name
	 * @return  the Player's name
	 */
	public String getName(){ return name; }
	
	@Override
	public String toString(){
		return getName() + " " + getID();
	}

	
}
