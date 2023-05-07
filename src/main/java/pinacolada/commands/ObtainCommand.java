package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;

import java.util.ArrayList;

public class ObtainCommand extends ConsoleCommand {
    public ObtainCommand() {
        this.requiresPlayer = true;
        this.minExtraTokens = 2;
        this.maxExtraTokens = 4;
        this.simpleCheck = true;
    }

    public static ArrayList<String> getPCLCards() {
        return EUIUtils.mapAsNonnull(PCLCardData.getAllData(), d -> d.ID);
    }

    protected void createCards(PCLCardData data, int count, int upgradeCount, int form) {
        for (int i = 0; i < count; ++i) {
            PCLCard copy = data.create(form, upgradeCount);
            doAction(copy);
        }
    }

    protected void doAction(PCLCard copy) {
        PCLActions.bottom.makeCardInHand(copy);
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        PCLCardData data = PCLCardData.getStaticData(tokens[1]);

        if (data != null) {
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
                form = Math.min(ConvertHelper.tryParseInt(tokens[4], 0), data.maxForms - 1);
            }

            DevConsole.log("Obtained " + count + (count == 1 ? " copy of " : " copies of ") + tokens[1] + " with " + upgradeCount + " upgrade(s) and form " + form);

            createCards(data, count, upgradeCount, form);
        }
        else {
            DevConsole.log("Could not find card " + tokens[1]);
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = getPCLCards();
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
