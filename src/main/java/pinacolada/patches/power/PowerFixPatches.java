package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FlightPower;
import com.megacrit.cardcrawl.powers.SadisticPower;
import com.megacrit.cardcrawl.powers.SharpHidePower;
import com.megacrit.cardcrawl.powers.watcher.BlockReturnPower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import extendedui.EUI;
import extendedui.patches.CardCrawlGamePatches;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.utilities.GameUtilities;

public class PowerFixPatches {
    // Make block return power give block to the attacker instead of always to the player
    @SpirePatch(clz = BlockReturnPower.class, method = "onAttacked")
    public static class BlockReturnPower_OnAttacked {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getFieldName().equals("player")) {
                        m.replace("{ $_ = info.owner; }");
                    }
                }
            };
        }
    }

    // Prevents Flight from crashing when removed from non-Byrds
    @SpirePatch(clz = FlightPower.class, method = "onRemove")
    public static class FlightPower_OnRemove {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(FlightPower __instance) {
            if (__instance.owner instanceof Byrd) {
                return SpireReturn.Continue();
            }
            return SpireReturn.Return();
        }
    }

    // Prevent Sadistic Nature from hurting summons
    @SpirePatch(clz = SadisticPower.class, method = "onApplyPower")
    public static class SadisticPower_OnApplyPower {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SadisticPower __instance, AbstractPower power, AbstractCreature target, AbstractCreature source) {
            if (target instanceof PCLCardAlly && source == AbstractDungeon.player && __instance.owner == AbstractDungeon.player) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    // Sharp Hide hurts the enemy target when on allied characters
    @SpirePatch(clz = SharpHidePower.class, method = "onUseCard")
    public static class SharpHidePower_OnUseCard {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SharpHidePower __instance, AbstractCard card, UseCardAction action) {
            if (GameUtilities.isEnemy(__instance.owner)) {
                return SpireReturn.Continue();
            }
            if (card.type == AbstractCard.CardType.ATTACK) {
                __instance.flash();
                AbstractCreature target = action.target != null ? action.target : GameUtilities.getRandomEnemy(true);
                if (target != null) {
                    PCLActions.bottom.dealDamage(__instance.owner, target, __instance.amount, DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                }
            }
            return SpireReturn.Return();
        }
    }

    // Prevent Vigor from being removed from non-player characters when you play a card. Also adds a power trigger catch
    @SpirePatch(clz = VigorPower.class, method = "onUseCard")
    public static class VigorPowerPatches_OnUseCard {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(VigorPower __instance, AbstractCard card, UseCardAction action) {
            if (__instance.owner != AbstractDungeon.player) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void insert(VigorPower __instance, AbstractCard card, UseCardAction action) {
            CombatManager.onSpecificPowerActivated(__instance, GameUtilities.getCardOwner(card), true);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(VigorPower.class, "addToBot");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
