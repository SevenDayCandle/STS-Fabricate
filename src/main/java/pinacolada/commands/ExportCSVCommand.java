package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.CustomCardLibraryScreen;
import pinacolada.misc.CSVExporter;

import java.util.ArrayList;

public class ExportCSVCommand extends ConsoleCommand {

    public ExportCSVCommand() {
        this.requiresPlayer = false;
        this.minExtraTokens = 1;
        this.simpleCheck = true;
    }

    @Override
    protected void execute(String[] strings, int depth) {
        try {
            AbstractCard.CardColor color = AbstractCard.CardColor.valueOf(strings[1]);
            CSVExporter.export(color);
            DevConsole.log("Exported cards for " + color.name());
        }
        catch (Exception e) {
            DevConsole.log("Could not export custom cards.");
            e.printStackTrace();
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = EUIUtils.map(CustomCardLibraryScreen.CardLists.keySet(), Enum::toString);
        if (options.contains(tokens[depth])) {
            if (tokens.length > depth + 1 && tokens[depth + 1].matches("\\d*")) {
                return ConsoleCommand.smallNumbers();
            }
        }

        return options;
    }
}
