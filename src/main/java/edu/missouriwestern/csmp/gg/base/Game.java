package edu.missouriwestern.csmp.gg.base;

import java.util.*;

/** Base class for representing games */
public abstract class Game {

	private Map<String,Board> boards = new HashMap<>();
	private int nextEntityID = 0;

	//game state and control fields
	private boolean running = false;

	private Map<Integer, Entity> registeredEntities = new HashMap<>();
	private Map<Integer, Player> allPlayers = new HashMap<>();
	private double gameTime;


	public void addPlayer(Player p) {
		allPlayers.put(p.getID(), p);
	}

	public void removePlayer(Player player) {
		allPlayers.remove(player);
	}
	public Player getPlayer(int id) {
		return allPlayers.get(id);
	}
	/**
	 * Returns {@link Entity}s associated with this game
	 * @return Set of associated Entities
	 */
	public HashSet<Entity> getEntities() { return new HashSet<Entity>(registeredEntities.values()); }
	/**
	 * Returns an {@link Entity} with the specified id
	 * @param id entity id
	 * @return entity 
	 */
	public Entity getEntity(int id) { return registeredEntities.get(id); }

	/**
	 * Returns whether the game is running
	 * @return running state
	 */
	public boolean isRunning() { return running; }

	public int getNumPlayers() { return allPlayers.size(); }
	/**
	 * Returns the set of all {@link Player}s
	 * @return connected Players
	 */
	public Set<Player> getAllPlayers() { return new HashSet<Player>(allPlayers.values()); }
	/**
	 * Returns the {@link Board} associated with this Game
	 * @return associated Board
	 */
	public Board getBoard(String name) { return boards.get(name); }
	/**
	 * Registers given {@link Entity} with the Game
	 * @param ent registering Entity
	 * @return id given to Entity
	 */
	public int registerEntity(Entity ent) {
		int id = nextEntityID++;
		registeredEntities.put(id, ent);
		return id;
	}
	/**
	 * Removes a registered {@link Entity} and every reference to it.
	 * @param ent Entity to be removed
	 */
	public void removeEntity(Entity ent) {

		for (Player p : allPlayers.values()) {//removes entity from players.
			if (p.getEntities().contains(ent)) {
				if (ent instanceof Container)
					for (Entity e : ((Container) ent).getEntities())
						p.addEntity(e);//transfers control of entities held by ent to the player.
				p.removeEntity(ent);
			}
		}
		for (Entity entity : registeredEntities.values()) {//removes entity from any other entities.
			if (entity instanceof Container) {
				Container c = (Container) entity;
				if (c.getEntities().contains(ent)) {
					if (ent instanceof Container)
						for (Entity e : ((Container) ent).getEntities())
							c.addEntity(e);//transfers control of entities held by ent to entity.
					c.removeEntity(ent);
				}
			}
		}
		for(Board board : boards.values()) {
			if (board.getTile(ent) == null) continue;

			Tile t = board.getTile(ent);
			if (t != null) {//removes entity from tiles.
				if (t.containsEntity(ent)) {
					if (ent instanceof Container)
						for (Entity e : ((Container) ent).getEntities())
							t.addEntity(e);//transfers control of entities held by ent to the tile.
					t.removeEntity(ent);
				}
			}
		}
		registeredEntities.remove(ent.getID());
	}

}
