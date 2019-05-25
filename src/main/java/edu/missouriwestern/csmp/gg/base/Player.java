package edu.missouriwestern.csmp.gg.base;

import java.util.HashSet;

public class Player implements Container{

	private HashSet<Entity> entities = new HashSet<>();
	private int id;
	private String name;

	public Player(int id, String name){
		this.id = id;
		this.name = name;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addEntity(Entity e) { 
		return entities.add(e)? true: false;
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
	public boolean removeEntity(Entity e) {
		return entities.remove(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashSet<Entity> getEntities() { return entities; }
	

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
