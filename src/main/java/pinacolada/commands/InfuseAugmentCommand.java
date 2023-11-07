package pinacolada.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cardmods.AugmentModifier;

import java.util.ArrayList;

public class InfuseAugmentCommand extends ConsoleCommand {

    public InfuseAugmentCommand() {
        this.requiresPlayer = true;
        this.minExtraTokens = 2;
        this.maxExtraTokens = 2;
        this.simpleCheck = true;
    }

    public static ArrayList<String> getCustoms() {
        return new ArrayList<>(PCLAugmentData.getIDs());
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        PCLAugmentData augment = PCLAugmentData.getStaticData(tokens[1]);
        String targetCard = tokens[2];
        AbstractCard c = AbstractDungeon.player.masterDeck.findCardById(targetCard);
        if (augment != null && c != null) {
            AugmentModifier.apply(augment.create(), c);
            DevConsole.log("Applied " + tokens[1] + " to " + tokens[2]);
        }
        else if (augment == null) {
            DevConsole.log("Could not find augment " + tokens[1]);
        }
        else {
            DevConsole.log("Could not find card " + tokens[2]);
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = getCustoms();
        if (options.contains(tokens[depth])) {
            if (tokens.length > depth + 1) {
                return ConsoleCommand.getCardOptionsFromCardGroup(AbstractDungeon.player.masterDeck);
            }
        }

        return options;
    }
}
