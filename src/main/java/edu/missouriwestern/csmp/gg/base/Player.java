package edu.missouriwestern.csmp.gg.base;

import java.net.URL;
import java.util.HashSet;

public class Player implements Container{

	private HashSet<Entity> entities = new HashSet<Entity>();
	private int id;
	private int score = 0; //Score starts at zero.
	private String name;
	private URL imgURL;
	private Colour color;
	
	public Player(int id, String name, Colour color, URL imgURL){
		this.id = id;
		this.name = name;
		this.color = color;
		this.imgURL = imgURL;
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
		return entities.remove(e)?true:false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashSet<Entity> getEntities() { return entities; }
	
	/**
	 * Sets the Player's score to new value
	 * @param score - new value for the Player's score
	 */
	public void setScore(int score){ this.score = score; }
	
	/**
	 * Returns a Player's current score
	 * @return
	 */
	public int getScore() { return score; }
	
	/**
	 * Returns the ID of the Player
	 * @return
	 */
	public int getID(){ return id; }

	/**
	 * Returns the Player's name
	 * @return
	 */
	public String getName(){ return name; }
	
	/**
	 * Returns player image {@link URL}
	 * @return image URL
	 */
	public URL getImageURL() { return imgURL; }
	
	public Colour getColor() { return color; }
	
	@Override
	public String toString(){
		return getName() + " " + getID() + " " + getColor();
	}

	
}
