package edu.missouriwestern.csmp.gg.base;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/** Class for managing the state of subgames using the 2D API
 */
public abstract class Game implements Container, EventProducer {

	private final Map<String,Board> boards = new HashMap<>();
	private final AtomicInteger nextEntityID = new AtomicInteger(1);
	private final Map<EventListener,Object> listeners = new ConcurrentHashMap<>();
	// no concurrent set, so only keys used to mimic set
	private final Map<Integer, Entity> registeredEntities = new ConcurrentHashMap<>();
	private final Map<Integer, Player> allPlayers = new ConcurrentHashMap<>();

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
	}

}
