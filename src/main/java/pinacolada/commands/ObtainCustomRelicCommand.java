package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import extendedui.EUIUtils;
import pinacolada.effects.PCLEffects;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLRelic;

import java.util.ArrayList;

public class ObtainCustomRelicCommand extends ConsoleCommand {

    public ObtainCustomRelicCommand() {
        this.requiresPlayer = true;
        this.minExtraTokens = 1;
        this.maxExtraTokens = 2;
        this.simpleCheck = true;
    }

    public static ArrayList<String> getCustoms() {
        return EUIUtils.map(PCLCustomRelicSlot.getRelics(null), slot -> slot.ID);
    }

    protected void doAction(PCLRelic copy) {
        PCLEffects.Queue.obtainRelic(copy);
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        PCLCustomRelicSlot slot = PCLCustomRelicSlot.get(tokens[1]);

        if (slot != null) {
            int form = 0;
            if (tokens.length > 2) {
                form = Math.min(ConvertHelper.tryParseInt(tokens[2], 0), slot.builders.size() - 1);
            }

            DevConsole.log("Obtained "  + tokens[1] + " with form " + form);

            PCLRelic copy = slot.builders.get(form).create();
            doAction(copy);
        }
        else {
            DevConsole.log("Could not find card " + tokens[1]);
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = getCustoms();
        if (options.contains(tokens[depth])) {
            if (tokens.length > depth + 1 && tokens[depth + 1].matches("\\d*")) {
                if (tokens.length > depth + 2) {
                    if (tokens[depth + 2].matches("\\d+")) {
                        ConsoleCommand.complete = true;
                    }
                    else if (tokens[depth + 2].length() > 0) {
                        tooManyTokensError();
                    }
                }
                return ConsoleCommand.smallNumbers();
            }
        }

        return options;
    }
}
