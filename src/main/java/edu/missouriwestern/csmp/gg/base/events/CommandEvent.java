package edu.missouriwestern.csmp.gg.base.events;

import com.google.gson.JsonParser;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;

import java.util.Map;

/** represents a command sent by a player of the game */
public class CommandEvent extends Event {

    private String commandName;
    private String parameterValue;
    private String playerId;

    public CommandEvent(Game game, String playerId, String commandName, String parameter) {
        super(game, createProperies(playerId, commandName, parameter));
        this.commandName = commandName;
        this.parameterValue = parameter;
        this.playerId = playerId;
    }

    public String getCommandName() { return commandName; }
    public String getParameterValue() { return parameterValue; }
    public String getPlayerId() { return playerId; }

    public static Map<String,String> createProperies(String playerId, String commandName, String parameter) {
        var m = Map.of(
                "player", playerId,
                "command", commandName,
                "parameter", parameter
        );

        return m;
    }

    /** takes a JSON object with a "command" and "parameter" property representing a player's command.
     * Creates a new command event and propagates it to all event listeners in the game.
     * @param game
     * @param playerId
     * @param json
     */
    public static void issueCommandEventFromJson(Game game, String playerId, String json) {
         var element = new JsonParser().parse(json);
        var command = element.getAsJsonObject().get("command").getAsString();
        var parameter = element.getAsJsonObject().get("parameter").getAsString();
        game.accept(new CommandEvent(game, playerId, command, parameter));
    }
}
