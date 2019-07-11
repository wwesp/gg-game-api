package edu.missouriwestern.csmp.gg.base.events;

import com.google.gson.JsonParser;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;

import java.util.Map;

public class CommandEvent extends Event {
    public CommandEvent(Game game, int id, String playerId, String commandName, String parameter) {
        super(game, id, createProperies(playerId, commandName, parameter));
    }
    public CommandEvent(Game game, String playerId, String commandName, String parameter) {
        super(game, createProperies(playerId, commandName, parameter));
    }

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
