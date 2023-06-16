package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;

import java.util.ArrayList;

public class ObtainCustomCardCommand extends ConsoleCommand {

    public ObtainCustomCardCommand() {
        this.requiresPlayer = true;
        this.minExtraTokens = 1;
        this.maxExtraTokens = 4;
        this.simpleCheck = true;
    }

    public static ArrayList<String> getCustoms() {
        return EUIUtils.map(PCLCustomCardSlot.getCards(null), slot -> slot.ID);
    }

    protected void doAction(PCLCard copy) {
        PCLActions.bottom.makeCardInHand(copy);
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        PCLCustomCardSlot slot = PCLCustomCardSlot.get(tokens[1]);

        if (slot != null) {
            int count = 1;
            if (tokens.length > 2 && ConvertHelper.tryParseInt(tokens[2]) != null) {
                count = ConvertHelper.tryParseInt(tokens[2], 0);
            }

            int upgradeCount = 0;
            if (tokens.length > 3) {
                upgradeCount = ConvertHelper.tryParseInt(tokens[3], 0);
            }

            int form = 0;
            if (tokens.length > 4) {
                form = Math.min(ConvertHelper.tryParseInt(tokens[4], 0), slot.builders.size() - 1);
            }

            DevConsole.log("Obtained " + count + (count == 1 ? " copy of " : " copies of ") + tokens[1] + " with " + upgradeCount + " upgrade(s) and form " + form);

            for (int i = 0; i < count; ++i) {
                PCLCard copy = slot.builders.get(form).createImplWithForms(true);

                for (int j = 0; j < upgradeCount; ++j) {
                    copy.upgrade();
                }

                doAction(copy);
            }
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
