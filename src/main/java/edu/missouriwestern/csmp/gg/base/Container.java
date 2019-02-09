package edu.missouriwestern.csmp.gg.base;

import java.util.HashSet;

/**
 * Interface for a container that can hold entities.
 * @author mhays14
 *
 */
public interface Container {
	
	/**
	 * Adds Entity to container.
	 * @param e: Entity - Entity to add.
	 * @return True if entity is added. False otherwise.
	 */
	public boolean addEntity(Entity e);
	/**
	 * Gets Entity e from container. 
	 * @param id: int - id of entity.
	 * @return e: Entity
	 */
	public Entity getEntity(int id);
	/**
	 * Removes entity from container.
	 * @param e: Entity - Entity to remove.
	 * @return True if removed. False otherwise.
	 */
	public boolean removeEntity(Entity e);
	/**
	 * returns all entities in container.
	 * @return HashSet of all entities. 
	 */
	public HashSet<Entity> getEntities();
	

}
