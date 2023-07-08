package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import pinacolada.augments.PCLAugmentData;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class AugmentCommand extends ConsoleCommand {

    public AugmentCommand() {
        this.requiresPlayer = true;
        this.minExtraTokens = 1;
        this.maxExtraTokens = 2;
        this.simpleCheck = true;
    }

    public static ArrayList<String> getCustoms() {
        return new ArrayList<>(PCLAugmentData.getIDs());
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        PCLAugmentData augment = PCLAugmentData.get(tokens[1]);
        int amount = tokens.length > 2 ? ConvertHelper.tryParseInt(tokens[2], 1) : 1;
        if (augment != null) {
            PGR.dungeon.addAugment(augment.ID, amount);
            DevConsole.log("Obtained " + amount + " " + tokens[1]);
        }
        else {
            DevConsole.log("Could not find augment " + tokens[1]);
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = getCustoms();
        if (options.contains(tokens[depth])) {
            if (tokens.length > depth + 1 && tokens[depth + 1].matches("\\d*")) {
                return ConsoleCommand.smallNumbers();
            }
        }

        return options;
    }
}
