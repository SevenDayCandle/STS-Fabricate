package pinacolada.commands;

import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Prefs;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Adapted from STS-AnimatorMod
public class UnlockAscensionCommand extends ConsoleCommand {
    public UnlockAscensionCommand() {
        this.requiresPlayer = false;
        this.simpleCheck = true;
        this.minExtraTokens = 1;
        this.maxExtraTokens = 2;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        int level = ConvertHelper.tryParseInt(tokens[1]);

        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
            Prefs prefs = p.getPrefs();
            if (prefs != null) {
                GameUtilities.unlockAscension(prefs, level);
            }
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        complete = true;
        return new ArrayList<>();
    }
}
