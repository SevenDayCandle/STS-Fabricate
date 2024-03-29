package pinacolada.patches.card;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.PGR;

// Copied and modified from STS-AnimatorMod
public class SoulPatches {
    @SpirePatch(clz = Soul.class, method = "discard", paramtypez = {AbstractCard.class, boolean.class})
    public static class SoulPatches_Discard {
        @SpirePostfixPatch
        public static void postfix(Soul soul, AbstractCard card, boolean visualOnly) {
            if (PCLCardTag.Loyal.has(card)) {
                soul.isReadyForReuse = true;
                AbstractDungeon.player.discardPile.moveToDeck(card, true);
                PCLCardTag.Loyal.tryProgress(card);
            }
        }
    }

    @SpirePatch(clz = Soul.class, method = "obtain", paramtypez = {AbstractCard.class})
    public static class SoulPatches_Obtain {
        @SpirePostfixPatch
        public static void postfix(Soul __instance, AbstractCard card) {
            PGR.dungeon.onCardObtained(card);
        }
    }

    @SpirePatch(clz = SoulGroup.class, method = "obtain", paramtypez = {AbstractCard.class, boolean.class})
    public static class SoulGroupPatches_Obtain {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SoulGroup __instance, AbstractCard card, boolean obtain) {
            if (obtain && !PGR.dungeon.tryObtainCard(card)) {
                PCLSFX.play(PCLSFX.CARD_BURN, 0.8f, 1.2f, 0.5f);
                PCLEffects.TopLevelQueue.add(new CardPoofEffect(card.current_x, card.current_y));
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = Soul.class, method = "update", optional = true)
    public static class SoulPatches_Update {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("applyPowers")) {
                        m.replace("pinacolada.dungeon.CombatManager.refreshHandLayout();");
                    }
                }
            };
        }
    }
}
