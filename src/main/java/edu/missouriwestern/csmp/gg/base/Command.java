package edu.missouriwestern.csmp.gg.base;

import apg.game.factory.CommandFactory;
import apg.messages.NoFactoryFoundException;

import java.util.ArrayList;
/** Represents input from the player */
public abstract class Command {

	/**
	 * Map of Command types to their respective factories.
	 */
	private static HashMap<T2<? extends Class<? extends Game>, String>, CommandFactory> commandFactories =
		new HashMap<T2<? extends Class<? extends Game>, String>, CommandFactory>();
	/**
	 * Registers a {@link CommandFactory} by associating it with a Command name
	 * @param commandName name/type of the Command
	 * @param factory associated CommandFactory
	 */
	public static void registerCommandFactory(Class<? extends Game> game, String commandName, CommandFactory factory) {
		commandFactories.put(Tuple.makeTuple(
                                        game,
                                        commandName.toUpperCase().trim()),

                                factory);
	}

	private Game game;
	protected ArrayList<Entity> entities = new ArrayList<Entity>();
	protected ArrayList<Tile> tiles = new ArrayList<Tile>();
	
	/**
	 * Constructs a command given a {@link Game}
	 * @param game
	 */
	public Command(Game game) {
		this.game = game;
	}

	/**
	 * Returns the {@link Game} associated with this Command
	 * @return associated Game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Returns a List of {@link Entity}s associated with this Command
	 * @return List of associated Entities
	 */
	public ArrayList<Entity> getCommandEntities() { return entities; }
	/**
	 * Returns a List of {@link Tile}s associated with this Command
	 * @return List of associated Tiles
	 */
	public ArrayList<Tile> getCommandTiles() { return tiles; }
	
	/**
	 * Executes a given Command
	 * <p>
	 * Make use of {@link Actionable#takeAction()} also
	 */
	public abstract void execute(Entity e);
	
	/**
	 * Parses a command description returning a command
	 * @param game associated Game
	 * @param desc command description
	 * @return command parsed from description
	 * @throws NoFactoryFoundException if no associated {@link CommandFactory} was found
	 */
	public static Command parseCommand(Game game, String desc) throws NoFactoryFoundException{
		ArrayList<String> commandDesc = new ArrayList<String>();
		String allDescArray[] = desc.split(" ");
		for (int i = 0; i < allDescArray.length; i++)
		{
			commandDesc.add(allDescArray[i]);
		}			
		String commandName = commandDesc.remove(0).toUpperCase().trim();
                T2<? extends Class<? extends Game>, String> key = Tuple.makeTuple(game.getClass(), commandName);
		if(commandFactories.containsKey(key)) {
			return commandFactories.get(key).buildCommand(game, commandDesc);
                }
		throw new NoFactoryFoundException("Command Factory for '" + commandName + "' not found!");
	}
}
