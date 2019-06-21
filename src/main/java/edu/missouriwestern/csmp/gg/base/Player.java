package edu.missouriwestern.csmp.gg.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/** represents a player within the game
 * not an entity as a player may potentially comprise multiple entities within the game
 *
 * Instanciating classes must include implementation of accept(Event) and forward
 * serialized events to clients.
 */
public abstract class Player implements Container, HasProperties, EventListener {

	private final Set<Entity> entities = new HashSet<>();
	private final String id;
	private final Map<String,String> properties;

	public Player(String id, Map<String,String> properties){
		this.id = id;
		this.properties = new HashMap<>(properties);
	}


	public Player(String id, String name){
		this(id, new HashMap<>());
	}

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
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
	public String getID(){ return id; }

	@Override
	public String toString() {
		return "{ \"id\": " + getID() +
				" \"properties\": " + serializeProperties() +
				" \"inventory\": {" +
				getEntities()
						.map(Entity::toString)
						.reduce((s1, s2) -> s1 + ", " + s2)
						.orElse("") + "}" +
				"}";
	}
}
