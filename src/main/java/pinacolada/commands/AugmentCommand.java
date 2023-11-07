package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class AugmentCommand extends ConsoleCommand {

    public AugmentCommand() {
        this.requiresPlayer = true;
        this.minExtraTokens = 1;
        this.maxExtraTokens = 4;
        this.simpleCheck = true;
    }

    public static ArrayList<String> getCustoms() {
        return new ArrayList<>(PCLAugmentData.getIDs());
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        PCLAugmentData augment = PCLAugmentData.getStaticData(tokens[1]);
        int timesUpgraded = tokens.length > 2 ? ConvertHelper.tryParseInt(tokens[2], 0) : 0;
        int form = tokens.length > 3 ? ConvertHelper.tryParseInt(tokens[3], 0) : 0;
        int amount = tokens.length > 4 ? ConvertHelper.tryParseInt(tokens[4], 1) : 1;
        if (augment != null) {
            PGR.dungeon.addAugment(new PCLAugment.SaveData(augment.ID, form, timesUpgraded));
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
