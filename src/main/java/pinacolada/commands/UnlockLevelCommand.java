package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;

import java.util.ArrayList;

// Adapted from STS-AnimatorMod
public class UnlockLevelCommand extends ConsoleCommand {
    public UnlockLevelCommand() {
        this.requiresPlayer = false;
        this.simpleCheck = true;
        this.minExtraTokens = 2;
        this.maxExtraTokens = 2;
    }

    protected static int getProperCost(int level) {
        switch (level) {
            case 1:
                return 750;
            case 2:
            case 3:
                return 1000;
            case 4:
                return 1500;
            case 5:
                return 2000;
            case 6:
                return 2500;
            case 7:
            case 8:
                return 3000;
            case 9:
                return 4000;
        }
        return 300;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        int level = ConvertHelper.tryParseInt(tokens[2]);
        UnlockTracker.unlockProgress.putInteger(tokens[1] + "UnlockLevel", level);
        UnlockTracker.unlockProgress.putInteger(tokens[1] + "Progress", 0);
        UnlockTracker.unlockProgress.putInteger(tokens[1] + "CurrentCost", getProperCost(level));
        UnlockTracker.unlockProgress.flush();
        DevConsole.log("Set unlock level of " + tokens[1] + " to " + tokens[2]);
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = EUIUtils.map(AbstractPlayer.PlayerClass.values(), Enum::name);
        if (options.contains(tokens[depth])) {
            if (tokens.length > depth + 1) {
                return ConsoleCommand.smallNumbers();
            }
        }

        return options;
    }
}
