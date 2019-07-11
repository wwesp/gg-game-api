package edu.missouriwestern.csmp.gg.base;

import com.google.common.collect.*;
import com.google.gson.GsonBuilder;
import edu.missouriwestern.csmp.gg.base.events.EntityCreationEvent;
import edu.missouriwestern.csmp.gg.base.events.EntityDeletionEvent;
import edu.missouriwestern.csmp.gg.base.events.EntityMovedEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/** Class for managing the state of subgames using the 2D API
 */
public abstract class Game implements Container, EventProducer {

	private final Map<String,Board> boards = new ConcurrentHashMap<>();
	private final long startTime;    // time when game was started or restarted
	private final long elapsedTime;  // time elapsed in game since start or last restart
	private final AtomicInteger nextEntityID = new AtomicInteger(1);
	private final AtomicInteger nextEventID = new AtomicInteger(1);
	private final Map<EventListener,Object> listeners = new ConcurrentHashMap<>();

	// no concurrent set, so only keys used to mimic set
	private final BiMap<Integer, Entity> registeredEntities;
	private final BiMap<String, Player> allPlayers;

	// access must be protected by monitor
	private final Multimap<Container, Entity> containerContents;
	private final Map<Entity, Container> entityLocations;


	public Game() {
		this.startTime = System.currentTimeMillis();
		this.elapsedTime = 0;
		registeredEntities = Maps.synchronizedBiMap(HashBiMap.create());
		allPlayers = Maps.synchronizedBiMap(HashBiMap.create());
		// access must be protected by monitor
		containerContents = HashMultimap.create();
		entityLocations = new HashMap<>();
	}

	@Override
	public final Game getGame() { return this; }

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
		listeners.put(listener, "thing");
	}

	@Override
	public void deregisterListener(EventListener listener) {
		listeners.put(listener, "thing");
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
	public Player getPlayer(String id) {
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

	public void addBoard(String boardId, Board board) {
		boards.put(boardId, board);
	}

	/**
	 * Registers given {@link Entity} with the Game
	 * @param ent registering Entity
	 */
	public void addEntity(Entity ent) {
		assert ent != null;

		var id = nextEntityID.getAndIncrement();
		registeredEntities.put(id, ent);
		synchronized (this) { // add entity to the game's contents as default
			entityLocations.put(ent, this);
			containerContents.put(this, ent);
		}
		if(ent instanceof EventListener) {
			registerListener((EventListener)ent);
		}
		accept(new EntityCreationEvent(this, ent));
	}

	/**
	 * Removes a registered {@link Entity} and every reference to it.
	 * @param ent Entity to be removed
	 */
	public void removeEntity(Entity ent) {
		synchronized(this) {
			if(ent instanceof EventListener) {
				deregisterListener((EventListener)ent);
			}
			moveEntity(ent, this); // generate an entity moved event

			var currentContainer = entityLocations.get(ent);
			entityLocations.remove(ent);
			if(currentContainer != null) {
				containerContents.remove(currentContainer, ent);
			}
			// remove entity from game
			registeredEntities.remove(ent);
		}

		// alert other game components to entity removal
		accept(new EntityDeletionEvent(this, ent));
	}

	public void moveEntity(Entity ent, Container container) {
		assert ent != null;
		assert container != null;
		assert registeredEntities.containsKey(ent.getID());

		Container prev = getGame().getEntityLocation(ent);

		synchronized (this) {
			// move entity to new location
			var currentLocation = getEntityLocation(ent);
			if(currentLocation != null) {
				entityLocations.remove(ent);
				containerContents.remove(currentLocation, ent);
			}
			entityLocations.put(ent, container);
			containerContents.put(container, ent);
		}
		accept(new EntityMovedEvent(ent, prev));
	}

	public synchronized boolean containsEntity(Container container, Entity ent) {
		assert ent != null;
		assert container != null;
		assert registeredEntities.containsKey(ent.getID());

		return containerContents.containsEntry(container, ent);
	}

	/** locate an entity */
	public synchronized Container getEntityLocation(Entity ent) {
		assert ent != null;
		assert registeredEntities.containsKey(ent.getID());

		return entityLocations.get(ent);
	}

	public synchronized Stream<Entity> getContainerContents(Container container) {
		assert container != null;

		return new HashSet<Entity>(containerContents.get(container)).stream();
	}

	/** determine what tile contains an entity.
	 * Returns null if no tile contains this entity */
	public synchronized Container getTopLevelEntityLocation(Entity ent) {
		assert ent != null;

		var location = getEntityLocation(ent);
		if(location == null) return null;
		if(location instanceof Entity)
			return getEntityLocation((Entity)location);
		return location instanceof Tile ? (Tile)location : null;
	}

	protected int getNextEventId() {
		return nextEventID.getAndIncrement();
	}

	/** returns a JSON representation of this game
	 */
	@Override
	public String toString() {
		var gsonBuilder = new GsonBuilder();
		var gson = gsonBuilder.create();
		var m = new HashMap<String,Object>();
		m.put("type", getClass().getSimpleName());
		m.put("elapsed-time", getGameTime());
		return gson.toJson(m);
	}

}
