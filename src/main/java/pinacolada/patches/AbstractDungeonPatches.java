package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
public class AbstractDungeonPatches {
/*    @SpirePatch(clz = AbstractDungeon.class, method = "getEvent", paramtypez = Random.class)
    public static class AbstractDungeonPatches_GetEvent
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractEvent> prefix(Random rng)
        {

            AbstractEvent event = PCLEvent.GenerateSpecialEvent(CardCrawlGame.dungeon, rng, GameUtilities.IsPCLPlayerClass() || PGR.core.Config.EnableEventsForOtherCharacters.Get());
            if (event != null)
            {
                return SpireReturn.Return(event);
            }


            return SpireReturn.Continue();
        }
    }*/

    @SpirePatch(clz = AbstractDungeon.class, method = "dungeonTransitionSetup")
    public static class AbstractDungeonPatches_DungeonTransitionSetup {
        @SpirePostfixPatch
        public static void postfix() {
            AbstractPlayer.PlayerClass pClass = GameUtilities.getPlayerClass();
            if (GameUtilities.isPCLPlayerClass(pClass)) {
                PCLResources<?, ?, ?, ?> resources = PGR.getResources(pClass);
                PCLCardData bane = resources.getAscendersBane();
                if (bane != null) {
                    final ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
                    for (int i = 0; i < cards.size(); i++) {
                        if (cards.get(i).cardID.equals(AscendersBane.ID)) {
                            cards.set(i, bane.makeCardFromLibrary(0));
                            UnlockTracker.markCardAsSeen(bane.ID);
                        }
                    }
                }
            }

        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "addCurseCards", optional = true)
    public static class AbstractDungeonPatches_AddCurseCards {
        @SpirePrefixPatch
        public static SpireReturn prefix() {
            for (Map.Entry<String, AbstractCard> entry : CardLibrary.cards.entrySet()) {
                AbstractCard c = entry.getValue();
                if (c.type == AbstractCard.CardType.CURSE && c.rarity != AbstractCard.CardRarity.SPECIAL) {
                    AbstractDungeon.curseCardPool.addToTop(c);
                }
            }

            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "onModifyPower", optional = true)
    public static class AbstractDungeonPatches_OnModifyPower {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasPower")) {
                        //onModifyPower checks if the player has focus to update orbs, it doesn't update them if focus is reduced to 0...
                        m.replace("$_ = true;");
                    }
                }
            };
        }
    }

    // The vanilla GetRandomCard from AbstractDungeon does an infinite loop (e.g. in the shop) if there are no uncommon and rare power cards in the pool...
    @SpirePatch(clz = AbstractDungeon.class, method = "getCardFromPool", optional = true)
    public static class AbstractDungeonPatches_GetCardFromPool {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean useRng) {
            return SpireReturn.Return(PGR.dungeon.getRandomCard(rarity, type, useRng ? AbstractDungeon.cardRng : null, true));
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getCard", paramtypez = {AbstractCard.CardRarity.class}, optional = true)
    public static class AbstractDungeonPatches_GetRewardCards {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractCard.CardRarity rarity) {
            // If no suitable card is found, create a dummy card because the method will crash if no card is actually found
            AbstractCard found = PGR.dungeon.getRandomCard(rarity, AbstractDungeon.cardRng, true);
            return SpireReturn.Return(found != null ? found : new QuestionMark());
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getCard", paramtypez = {AbstractCard.CardRarity.class, Random.class}, optional = true)
    public static class AbstractDungeonPatches_GetRewardCards2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractCard.CardRarity rarity, Random rng) {
            // If no suitable card is found, create a dummy card because the method will crash if no card is actually found
            AbstractCard found = PGR.dungeon.getRandomCard(rarity, rng, true);
            return SpireReturn.Return(found != null ? found : new QuestionMark());
        }
    }
}