package pinacolada.commands;

import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.patches.screens.RelicViewScreenPatches;

import java.util.ArrayList;

// Adapted from STS-AnimatorMod
public class UnlockAllCommand extends ConsoleCommand {
    public UnlockAllCommand() {
        this.requiresPlayer = false;
        this.simpleCheck = true;
    }

    @Override
    protected void execute(String[] strings, int depth) {
        for (AbstractCard c : CardLibrary.getAllCards()) {
            String key = c.cardID;
            //UnlockTracker.unlockCard(), without flushing after every card.
            UnlockTracker.seenPref.putInteger(key, 1);
            UnlockTracker.unlockPref.putInteger(key, 2);
            UnlockTracker.lockedCards.remove(key);
            AbstractCard card = CardLibrary.getCard(key);
            if (card != null) {
                card.isSeen = true;
                card.unlock();
            }
        }

        for (AbstractRelic r : RelicViewScreenPatches.getAllRelics()) {
            String key = r.relicId;
            UnlockTracker.relicSeenPref.putInteger(key, 1);
            UnlockTracker.lockedRelics.remove(key);
            AbstractRelic relic = RelicLibrary.getRelic(key);
            if (relic != null) {
                relic.isSeen = true;
            }
        }

        UnlockTracker.unlockPref.flush();
        UnlockTracker.seenPref.flush();

        EUI.customLibraryScreen.refreshGroups();
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        complete = true;
        return new ArrayList<>();
    }
}
