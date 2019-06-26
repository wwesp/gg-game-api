package edu.missouriwestern.csmp.gg.base;

import edu.missouriwestern.csmp.gg.base.events.EntityCreation;
import edu.missouriwestern.csmp.gg.base.events.EntityDeletion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/** Class for managing the state of subgames using the 2D API
 */
public abstract class Game implements Container, EventProducer {

	private final Map<String,Board> boards = new HashMap<>();
	private final long startTime;    // time when game was started or restarted
	private final long elapsedTime;  // time elapsed in game since start or last restart
	private final AtomicInteger nextEntityID = new AtomicInteger(1);
	private final AtomicInteger nextEventID = new AtomicInteger(1);
	private final Map<EventListener,Object> listeners = new ConcurrentHashMap<>();
	// no concurrent set, so only keys used to mimic set
	private final Map<Integer, Entity> registeredEntities = new ConcurrentHashMap<>();
	private final Map<Entity, Location> entityLocations = new ConcurrentHashMap<>();
	private final Map<String, Player> allPlayers = new ConcurrentHashMap<>();

	public Game() {
		this.startTime = System.currentTimeMillis();
		this.elapsedTime = 0;
	}


	/** UNIX time of the last start or restart */
	public long getStartTime() {
		return startTime;
	}

	/** returns the number of milliseconds elapsed since the start of the game */
	public long getGameTime() {
		return System.currentTimeMillis() - startTime + elapsedTime;
	}

	@Override
	public void registerListener(EventListener listener) {
		listeners.put(listener, null);
	}

	@Override
	public void deregisterListener(EventListener listener) {
		listeners.put(listener, null);
	}

	@Override
	public Stream<EventListener> getListeners() {
		return listeners.keySet().stream();
	}

	/** add a player to the game
	 * @param player player to be added to the game
	 */
	public void addPlayer(Player player) {
		allPlayers.put(player.getID(), player);
	}

	/** remove player from the game
	 *
	 * @param player player to be removed from the game
	 */
	public void removePlayer(Player player) {
		allPlayers.remove(player);
	}

	/** find player with associated ID that has joined this game
	 * If the player has not joined this game, null will be returned
	 * @param id ID of player to be found
	 * @return player object with associated ID
	 */
	public Player getPlayer(int id) {
		return allPlayers.get(id);
	}

	/**
	 * Returns {@link Entity}s associated with this game
	 * @return Stream of associated Entities
	 */
	public Stream<Entity> getEntities() {
		return registeredEntities.values().stream();
	}

	/**
	 * Returns an {@link Entity} with the specified id
	 * @param id entity id
	 * @return entity 
	 */
	public Entity getEntity(int id) {
		return registeredEntities.get(id);
	}

	/**
	 * returns the id of the Entity ent
	 * If ent is not registered with this game, a runtime exception will be thrown.
	 * @param ent the entity whose ID is to be found
	 * @return the id of the supplied entity
	 */
	public int getEntityId(Entity ent) {
		return registeredEntities.entrySet().stream()
				.filter(e -> e.getValue() == ent)
				.mapToInt(e -> e.getKey())
				.findFirst().getAsInt();
	}

	/** determine the number of players currently in the game
	 *
	 * @return the number of players in the game
	 */
	public int getNumPlayers() {
		return allPlayers.size();
	}
	/**
	 * Returns the set of all {@link Player}s
	 * @return connected Players
	 */
	public Stream<Player> getAllPlayers() {
		return allPlayers.values().stream();
	}

	/**
	 * Returns the {@link Board} associated with this Game
	 * @return associated Board
	 */
	public Board getBoard(String name) {
		return boards.get(name);
	}

	/**
	 * Registers given {@link Entity} with the Game
	 * @param ent registering Entity
	 */
	public void addEntity(Entity ent) {
		var id = nextEntityID.getAndIncrement();
		registeredEntities.put(id, ent);
		accept(new EntityCreation(this, getNextEventId(), ent));
	}

	/**
	 * Removes a registered {@link Entity} and every reference to it.
	 * @param ent Entity to be removed
	 */
	public void removeEntity(Entity ent) {

		// remove entity from all players
		for (Player p : allPlayers.values()) {//removes entity from players.
			p.removeEntity(ent);
		}

		// remove entity from all tiles on all boards
		for(Board board : boards.values()) {
			board.getTileStream().forEach(t -> t.removeEntity(ent));
		}

		// remove entity from any entities that are not on a tile or with a player
		Container.super.removeEntity(ent);

		// remove entity from game
		registeredEntities.remove(ent);
		accept(new EntityDeletion(this, getNextEventId(), ent));
	}

	/** place an entity at a specified location */
	public void moveEntity(Entity ent, Location location) {
		entityLocations.put(ent, location);
	}

	/** locate an entity placed on a tile */
	public Location getEntityLocation(Entity ent) {
		return entityLocations.get(ent);
	}

	/** locate an entity contained by another entity */
	public Entity getContainingEntity(Entity ent) {
		return getEntities()
				.filter(e  -> e instanceof Container)
				.filter(c -> ((Container)c).containsEntity(ent))
				.findFirst().orElseGet(null);
	}

	public int getNextEventId() {
		return nextEventID.getAndIncrement();
	}

	public String toString() {
		return "{ \"type\": " + getClass().getSimpleName() +
				" \"elapsedTime\":" + getGameTime() +
				" \"entities\": {" + // serialize all current entities
					getEntities()
							.map(Entity::toString) // convert to strings
							.reduce((s1, s2) -> s1 + ", " + s2) // reduce to comma-separated string
							.orElse("") + "}" +  // empty string if no entities
				"}";
	}


}
