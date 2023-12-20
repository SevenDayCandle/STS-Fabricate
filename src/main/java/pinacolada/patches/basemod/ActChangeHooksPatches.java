package pinacolada.patches.basemod;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.ActChangeHooks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ActChangeHooksPatches {

    // Have to do these patches manually instead of using BaseMod's subscriber classes to avoid having the card pool getting initialized twice on game start
    // Also ensures that this comes after any start game subscribers that manipulate the card pool
    @SpirePatch(
            clz = ActChangeHooks.InGameConstructor.class,
            method = "Postfix"
    )
    public static class AbstractDungeonPatches_StartGame {

        public static void Postfix(AbstractDungeon __instance,
                                   String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
            if (levelId.equals(Exordium.ID) && AbstractDungeon.floorNum == 0) {
                PGR.dungeon.initializeData();
            }
            PGR.dungeon.initializeCardPool();
        }

    }

    @SpirePatch(
            clz = ActChangeHooks.SavedGameConstructor.class,
            method = "Postfix"
    )
    public static class AbstractDungeonPatches_ContinueGame {

        public static void Postfix(Object __obj_instance,
                                   String name, AbstractPlayer p, SaveFile saveFile) {
            PGR.dungeon.initializeData();
            PGR.dungeon.initializeCardPool();
        }

    }
}
