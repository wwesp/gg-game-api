package edu.missouriwestern.csmp.gg.base;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.*;

import net.sourcedestination.funcles.tuple.*;
import java.net.URL;

/** Base class for representing games */
public abstract class Game implements Runnable {


	//Server Parameters
	// game type -> (set of {param name, param type}...)
	private static HashMap<String, HashSet<T2<String,ConfigurationType>>> defaultParams = 
			new HashMap<>();
	
	@Deprecated private int START_PORT = 10200;
	@Deprecated private int END_PORT = 18200;
	protected HashSet<String> extensionClasses = new HashSet<String>();
	private Configuration config = new Configuration();
    @Deprecated private HashMap<String, URL> defaultEntityImages = new HashMap<String, URL>();
	private Server server;
        private ArrayList<GameEventListener> defferedListenerRegistrations = new ArrayList<GameEventListener>();

        boolean currentlySendingEvent;
        ArrayList<GameEvent> eventQueue = new ArrayList<GameEvent>();

	private Board board;
	private int nextEntityID = 0;

	//game state and control fields
	private boolean running = false;
	private boolean isOver = false;

	//event handling fields
	private ArrayList<GameEvent> events = new ArrayList<GameEvent>();
	private HashSet<GameEventListener> listeners = new HashSet<GameEventListener>();
	private static HashSet<GameEventListener> globalListeners = new HashSet<GameEventListener>();

	private LinkedList<Tuple2<Actionable, Double>> actionQueue = new LinkedList<Tuple2<Actionable, Double>>();
	private HashMap<Integer, Entity> registeredEntities = new HashMap<Integer, Entity>();
	private double gameTime;
	protected HashSet<Player> allPlayers = new HashSet<Player>();

	/**
	 * Map of Game types with Game factories
	 */
	private static HashMap<String, GameFactory> registeredGameTypes =
		new HashMap<String, GameFactory>();
	/**
	 * Registers new {@link GameFactory} by associating it with a game type/name
	 * @param typeid type
	 * @param factory GameFactory to created Game of given type
	 */
	public static void registerGameType(String typeid, GameFactory factory) {
		registeredGameTypes.put(typeid, factory);
	}
	/**
	 * Returns registered game types
	 * @return Collection of registered game types
	 */
	public static Collection<String> getGameTypes() { return registeredGameTypes.keySet(); }
	/**
	 * Returns {@link GameFactory} for given game type
	 * @param gameType type
	 * @return associated GameFactory if one exists otherwise null
	 */
	public static GameFactory getGameFactory(String gameType) { return registeredGameTypes.get(gameType); }

	/**
	 * Returns a {@link Board} if one can be obtained from known games
	 * @param cfgPath config path
	 * @return board or null
	 */
	@Deprecated public static String recognizeConfiguration(String cfgPath) {
		for(String t : registeredGameTypes.keySet()) {
			try{
				Game g = getGameFactory(t).buildGame(cfgPath);
				//if(g.validate()) {
					return t;
				//}
			}catch(Exception ex) {}
		}
		return null;
	}
	
	/** adds a configuration parameter to every instance of the specified game type */
	public static void addConfigurationParameter(String gt, String name, ConfigurationType type) {
		if(!defaultParams.containsKey(gt)) 
			defaultParams.put(gt, new HashSet<T2<String,ConfigurationType>>());
		defaultParams.get(gt).add(Tuple.makeTuple(name, type));
	}

	public static HashSet<String> getParameterNames(String gt) {
		HashSet<String> names = new HashSet<String>();
		if(defaultParams.containsKey(gt)) 
			for(T2<String, ConfigurationType> t : defaultParams.get(gt)) 
				names.add(t.a1());
		return names;
	}

	public static ConfigurationType getParameterType(String gt, String name) {
		if(defaultParams.containsKey(gt)) 
			for(T2<String, ConfigurationType> t : defaultParams.get(gt))
				if(t.a1().equals(name)) 
					return t.a2();
		throw new IllegalArgumentException("no paramter with name '"+name+" for class: " + gt);
	}
	
	

	public Configuration getConfig(){
		return config;
	}
	
	/**
	 * Returns the game {@link Server}
	 * @return server
	 */
	public Server getServer() { return server; }
	/**
	 * Returns an unused port
	 * @return new unused port
	 */
	//TODO: Separate this from game class.
	@Deprecated private int getNewPort() {
		int p=0;
		do {
			p = (int)(Math.random()*(END_PORT-START_PORT) + START_PORT);
		} while(Server.getPortsInUse().contains(p));
		return p;
	}
	/**
	 * Launches the game {@link Server} on a new port
	 * @return whether Server and Game were successfully started
	 */
	//TODO: Seperate this from game class.
	@Deprecated public boolean launchServer() { 
		//if(!validate())
		//	return false;
		try { 
			server = new Server(this, getNewPort()); 
			Thread t = new Thread(server);
			t.start();
		}
		catch(Exception e) { System.out.println("Error starting server"); e.printStackTrace(); return false;}
		return true;
	}
	/**
	 * Loads an extension class
	 * @param className package path to the Class
	 */
	public void loadExtension(String packagePath, String className, boolean external) {
		try {
			System.out.println("Loading: " + className);
			Class.forName(packagePath + "." + className);
			if(external)
				extensionClasses.add(className);
		} catch (ClassNotFoundException e) {}
	}
	
	
	/**
	 * Returns a collection of known extensions
	 * @return collection of extensions
	 */
	public synchronized Collection<String> getExtensions() { return (Collection<String>) extensionClasses.clone(); }


	
	/**
	 * Connects a {@link PlayerConnection} to this game
	 * @param p connecting Player
	 */
	public void addPlayer(Player p) {allPlayers.add(p);}
	
	/**
	 * Removes given {@link PlayerConnection} from active players
	 * @param playerConnection disconnecting Player
	 */
	public void removePlayer(Player player) {
		Iterator<Player> playerIt = allPlayers.iterator();
		if(playerIt.hasNext()) {
			Player p = playerIt.next();
			if(p.equals(player)) {
				p.getConnection().disconnect();
				
				allPlayers.remove(player);
				return;
			}
		}
	}
	public Player getPlayer(int id) {
		for(Player p : allPlayers)
			if(p.getID() == id)
				return p;
		return null;
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
	 * Constructs the game from a config file
	 * @param cfgPath path to config file within the jar
	 */
	protected Game(String cfgPath) {
		if(defaultParams.containsKey(getClass().getSimpleName()))
			for(T2<String,ConfigurationType> t : defaultParams.get(getClass().getSimpleName()))
				config.addParameter(t.a1(), t.a2());
		try {
			config.addParameter("TIME_LIMIT", new IntegerConfigType());//infinite turn time
			config.setValue("TIME_LIMIT", "0");//infinite turn time
			if(cfgPath == null) {
				board = loadConfigFile(null);
				if(board == null) //if a Board was not created by loadConfigFile when you passed loadConfigFile 'null'
					IllegalMessageException.badInput("Board was not Loaded");
				board.setGame(this);
			}
			else {
				BufferedReader in = new BufferedReader(new InputStreamReader(Game.class.getResourceAsStream(cfgPath)));
				board = loadConfigFile(in);
				if(board == null) IllegalMessageException.badInput("Board was not Loaded");
				board.setGame(this);
				postLoadConfig(in);
			}
			for(GameEventListener gel : globalListeners) 
				registerEventListener(gel);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Runs the Game Loop
	 * <p>
	 * Before loop is entered the following is done:
	 * <ul>
	 * <li>Sends {@link GameStartEvent}</li>
	 * <li>Calls {@link Game#beginGameCall()} </li>
	 * </ul>
	 * Inside the game loop: 
	 * <ul>
	 * <li> Calls {@link Game#beforeActionCall()} </li>
	 * <li> An {@link Actionable} with the next closest time executes {@link Actionable#takeAction()}</li>
	 * <li> Sends {@link TimerEvent} </li>
	 * <li> Calls {@link Game#afterActionCall()} <li>
	 * </ul>
	 * After the game loop {@link GameEndEvent} is sent
	 */
	@Override
	public void run() {
		System.out.println("Starting game");
		running = true;
		sendEvent(new GameStartEvent(this, null));//sends to all players
		System.out.println("Start game event sent");
		beginGameCall();
		System.out.println("BeginGameCall has been run");
		while(running) {
			if(actionQueue.size() == 0 && running) {
				synchronized(this) { 
					System.out.println("Synchronized on game, Game 317");
					try { wait(); } catch (InterruptedException e) {} 
				}
			} else if(running) {
				T2<Actionable,Double> args = actionQueue.poll();
				beforeActionCall(args.a1());
				gameTime = args.a2();
				args.a1().takeAction();
				sendEvent(new TimerEvent(this, null, gameTime));//TODO: Should this go to all players?
				afterActionCall(args.a1());
			}
		}
	}

	/**
	 * Returns number of {@link Actionable}s in the turn queue
	 * @return number of actionable objects in queue
	 */
	public int getNumInQueue() { return actionQueue.size(); }

	/**
	 * Returns whether the game is running
	 * @return running state
	 */
	public boolean isRunning() { return running; }
	/**
	 * Returns whether the game is over
	 * @return whether game has ended
	 */
	public boolean isOver() { return isOver; }
	/**
	 * Returns the number of active {@link PlayerConnection}s
	 * @return active players
	 */
	public int getNumPlayers() { return allPlayers.size(); }
	/**
	 * Returns the set of all {@link Player}s
	 * @return connected Players
	 */
	public HashSet<Player> getAllPlayers() { return allPlayers; }
	/**
	 * Returns the {@link Board} associated with this Game
	 * @return associated Board
	 */
	public Board getBoard() { return board; }
	/**
	 * Registers given {@link Entity} with the Game
	 * @param ent registering Entity
	 * @return id given to Entity
	 */
	public int registerEntity(Entity ent) {
		int id = nextEntityID++;
		registeredEntities.put(id, ent);
                if(ent.getImageURL() != null) {
                   
                } 
		return id;
	}
	/**
	 * Removes a registered {@link Entity} and every reference to it.
	 * @param ent Entity to be removed
	 */
	public void removeEntity(Entity ent) {
		Tile t = board.getTile(ent);
		for (Player p: allPlayers){//removes entity from players.
			if (p.getEntities().contains(ent)){
				if(ent instanceof Container)
					for(Entity e: ((Container) ent).getEntities())
						p.addEntity(e);//transfers control of entities held by ent to the player.
				p.removeEntity(ent);
			}
		}
		for(Entity entity: registeredEntities.values()){//removes entity from any other entities.
			if(entity instanceof Container){
				Container c = (Container) entity;
				if (c.getEntities().contains(ent)){
					if(ent instanceof Container)
						for(Entity e: ((Container) ent).getEntities())
							c.addEntity(e);//transfers control of entities held by ent to entity.
					c.removeEntity(ent);
				}
			}
		}
		if(t != null){//removes entity from tiles.
            if(t.containsEntity(ent)){
            	if(ent instanceof Container)
					for(Entity e: ((Container) ent).getEntities())
						t.addEntity(e);//transfers control of entities held by ent to the tile.
              	t.removeEntity(ent);
            }
		}
		registeredEntities.remove(ent.getID());
	}
	
	/**
	 * Registers an {@link Actionable} to execute an action
	 * <p>
	 * The {@link Actionable} will be placed in a queue according to the time
	 * given as a delta from the current game time.
	 * @param t delta from current game time
	 * @param a Actionable to be placed in queue
	 * @see Actionable#takeAction()
	 */
	public synchronized void registerTimedAction(double t, Actionable a) {
		double time = gameTime+t;
		T2<Actionable,Double> action = new T2<Actionable,Double>(a, time);
		for(int i = 0; i < actionQueue.size(); i++) {
			if(actionQueue.get(i).a2() > time) {
				actionQueue.add(i, action);
				notify();
				return;
			}
		}
		actionQueue.offer(action);
		notify();
	}

	/**
	 * Adds a global {@link GameEventListener}
	 * @param listener 
	 */
	public static void addGlobalGameEventListener(GameEventListener listener) {
		globalListeners.add(listener);
	}
	/**
	 * Removes a global {@link GameEventListener}
	 * @param listener
	 */
	public static void removeGlobalGameEventListener(GameEventListener listener) {
		globalListeners.remove(listener);
	}

	/**
	 * Registers a {@link GameEventListener} to receive Game notification events
	 * @param ge registering listener
	 */
	public synchronized void registerEventListener(GameEventListener ge) {
		if(currentlySendingEvent) {
			defferedListenerRegistrations.add(ge);
            return;
        }
        currentlySendingEvent = true;
		for(GameEvent evt : events)  //send all old events
			ge.processGameEvent(evt);
		listeners.add(ge);
        currentlySendingEvent = false;
	}

	/**
	 * Send the given {@link GameEvent} to all registered {@link GameEventListener}s.
     *
     * If an additional event is sent by a listener via this method, it will be queued and sent after
     * each listener has had a chance to process the initial event.
     * 
	 * @param evt event to be sent
	 */
	@SuppressWarnings("unchecked")
	public synchronized void sendEvent(GameEvent evt) {
		if(currentlySendingEvent) {  //sendEvent was called from an event listener, mutual recursion
			eventQueue.add(evt);
            return;
        }
        currentlySendingEvent = true;//TODO:Do we need this since it is synchronized?
		events.add(evt);
		//tempListener created to prevent any problems due to modification of the original HashSet.
		HashSet<GameEventListener> tempListener = (HashSet<GameEventListener>) listeners.clone();
		if (evt.getWhiteList() == null)//If null then all are white-listed.
			for(GameEventListener gel : tempListener)										  
				gel.processGameEvent(evt);
		else
			for(GameEventListener gel : tempListener)
				if(evt.getWhiteList().contains(gel))//Checks if it is okay to send event to listener.
					gel.processGameEvent(evt);
       currentlySendingEvent = false;
       //tempList created to prevent any problems due to modification of the original HashSet.
       ArrayList<GameEventListener> tempList = (ArrayList<GameEventListener>) defferedListenerRegistrations.clone(); 
       for(GameEventListener ge : tempList) {
    	   defferedListenerRegistrations.remove(ge);
           registerEventListener(ge);
       }
       if(eventQueue.size() > 0)
    	   sendEvent(eventQueue.remove(0));
	}
        
	/**
	 * Sends an event to a specific {@link GameEventListener}
	 * @param evt game event
	 * @param listener game event listener
	 */
	@Deprecated public void sendEvent(GameEvent evt, GameEventListener listener) {
		listener.processGameEvent(evt);
	}
	/**
	 * Stops the game, disconnects {@link PlayerConnection}s, and stops {@link Server}
	 */
	public void stopGame() {
		running = false;
		isOver = true;
		HashSet<Player> pls = null;
		sendEvent(new GameEndEvent(this, null)); //sends to all players.
		synchronized(this) {pls = (HashSet<Player>) allPlayers.clone();}
		for(Player p : pls) {
			synchronized(p) { p.getConnection().notifyAll(); }
			p.getConnection().disconnect();
		}
		if(server != null)
			server.stopServer();

		synchronized(this) { notifyAll(); }
	}
	
	/**
	 * Registers a command from given {@link PlayerConnection}
	 * PlayerConnections can be gotten from players by calling getConnection().
	 * @param playerConnection Player sending Command
	 * @param cmd Command to be registered
	 */
	public abstract void registerPlayerCommand(PlayerConnection playerConnection, Command cmd);
	/**
	 * Confirms a connecting {@link PlayerConnection}
	 * <p>
	 * Use {@link PlayerConnection#getHandshakingData()} to retrieve handshaking data sent by the Player.
	 *  
	 * @param playerConnection conecting Player
	 * @return whether Player was confirmed
	 */
	public abstract boolean confirmPlayer(Player player);
	/**
	 * Called in the game loop before an action is taken
	 * @param a object taking action
	 */
	// TODO: Remove from framework.
	@Deprecated public abstract void beforeActionCall(Actionable a);
	/**
	 * Called in the game loop after an action is taken
	 * @param a object taking action
	 */
	/* TODO: Remove from framework.
	 * TODO: Look at how it is used in other games and
	 *       move that code into server.
	 */
	@Deprecated public abstract void afterActionCall(Actionable a);
	/**
	 * Called immediately after the game starts before the game loop
	 */
	public abstract void beginGameCall();
	/**
	 * Sends the initial configuration data needed by the {@link PlayerConnection}
	 * @param playerConnection Player the configuration will be sent to
	 * @param out out Stream to Player
	 */
	public abstract void sendInitialConfiguration(Player player, PrintWriter out);
	/**
	 * Loads the config from given {@link Reader} and creates the {@link Board}
	 * @param in config Stream
	 * @return created Board 
	 * @throws IOException
	 */
	protected abstract Board loadConfigFile(BufferedReader in) throws IOException;
	/**
	 * Loads anything after the {@link Board} description in the config file
	 * @param in config Stream
	 * @throws IOException
	 */
	protected abstract void postLoadConfig(BufferedReader in) throws IOException;
	/**
	 * Given a player id returns an informative String to be displayed on the web page
	 * @param pId player id
	 * @return description string
	 */
	public abstract String getPlayerDescription(int pId);
}
