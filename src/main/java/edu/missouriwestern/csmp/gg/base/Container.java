package edu.missouriwestern.csmp.gg.base;
import java.util.stream.Stream;

/**
 * Interface for a container that can hold entities.
 */
public interface Container {
	
	/**
	 * Adds Entity to container.
	 * @param e Entity to add.
	 */
	public void addEntity(Entity e);

	/**
	 * Gets Entity e from container. 
	 * @param id  id of entity.
	 * @return e Entity with associated id
	 */
	public Entity getEntity(int id);

	/**
	 * Removes entity from container.
	 * By default simply removes entity from all contained container entities, recursively.
	 * Should be overridden and called from implementing class.
	 * @param ent Entity to remove.
	 */
	public default void removeEntity(Entity ent) {
		getEntities()
				.filter(e -> e instanceof Container)
				.forEach(e -> ((Container)e).removeEntity(e));
	}

	/**
	 * returns all entities in container.
	 * @return HashSet of all entities. 
	 */
	public Stream<Entity> getEntities();
	

}
