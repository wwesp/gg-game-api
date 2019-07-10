package edu.missouriwestern.csmp.gg.base;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** represents a player within the game
 * not an entity as a player may potentially comprise multiple entities within the game
 *
 * Instanciating classes must include implementation of accept(Event) and forward
 * serialized events to clients.
 */
public abstract class Player implements Container, HasProperties, EventListener {

	private final String id;
	private final Map<String,String> properties;
	private final Game game;

	public Player(String id, Game game, Map<String,String> properties){
		this.id = id;
		this.game = game;
		this.properties = new HashMap<>(properties);
	}

	public Player(String id, Game game){
		this(id, game, new HashMap<>());
	}

	@Override
	public final Game getGame() { return game; }

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * Returns the ID of the Player
	 * @return the ID of the Player
	 */
	public String getID(){ return id; }


	/** returns a JSON representation of this player and its properties
	 */
	@Override
	public String toString() {
		var gsonBuilder = new GsonBuilder();
		var gson = gsonBuilder.create();
		var m = new HashMap<String,Object>();
		m.put("id", getID());
		m.put("inventory", getEntities().map(Entity::getID).collect(Collectors.toList()));
		m.put("properties", properties);
		return gson.toJson(m);
	}
}
