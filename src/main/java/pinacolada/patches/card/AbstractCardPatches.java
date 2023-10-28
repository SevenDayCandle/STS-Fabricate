package pinacolada.patches.card;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.actions.PCLActions;
import pinacolada.blights.PCLBlight;
import pinacolada.cardmods.TemporaryCostModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.EphemeralField;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.CombatManager;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class AbstractCardPatches {
    public static boolean forcePlay;

    @SpirePatch(clz = AbstractCard.class, method = "applyPowersToBlock")
    public static class AbstractCard_ApplyPowersToBlock {
        @SpireInsertPatch(localvars = {"tmp"}, locator = Locator.class)
        public static void insertPre(AbstractCard __instance, @ByRef float[] tmp) {
            // Updating cost here because this gets called by all vanilla card calculation update methods
            TemporaryCostModifier.tryRefresh(__instance, AbstractDungeon.player, __instance.costForTurn, 0);

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof PCLRelic) {
                    tmp[0] = ((PCLRelic) r).atBlockModify(tmp[0], __instance);
                }
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof PCLRelic) {
                    tmp[0] = ((PCLRelic) r).atBlockLastModify(tmp[0], __instance);
                }
            }

            for (AbstractBlight r : AbstractDungeon.player.blights) {
                if (r instanceof PCLBlight) {
                    tmp[0] = ((PCLBlight) r).atBlockModify(tmp[0], __instance);
                }
            }
            for (AbstractBlight r : AbstractDungeon.player.blights) {
                if (r instanceof PCLBlight) {
                    tmp[0] = ((PCLBlight) r).atBlockLastModify(tmp[0], __instance);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class PlayerDamageGivePatches {

        @SpireInsertPatch(locator = MonsterMultiDamageFinalReceiveLocator.class, localvars = {"tmp", "i"})
        public static void multiFinalReceive(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i) {
            tmp[i] = CombatManager.onModifyDamageReceiveLast(tmp[i], __instance.damageTypeForTurn, GameUtilities.getCardOwner(__instance), AbstractDungeon.getMonsters().monsters.get(i), __instance);
        }

        @SpireInsertPatch(locator = MonsterMultiDamageReceiveLocator.class, localvars = {"tmp", "i"})
        public static void multiReceive(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i) {
            tmp[i] = CombatManager.onModifyDamageReceiveFirst(tmp[i], __instance.damageTypeForTurn, GameUtilities.getCardOwner(__instance), AbstractDungeon.getMonsters().monsters.get(i), __instance);
        }

        @SpireInsertPatch(locator = MonsterDamageFinalReceiveLocator.class, localvars = "tmp")
        public static void singleFinalReceive(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            tmp[0] = CombatManager.onModifyDamageReceiveLast(tmp[0], __instance.damageTypeForTurn, GameUtilities.getCardOwner(__instance), mo, __instance);
        }

        @SpireInsertPatch(locator = MonsterDamageReceiveLocator.class, localvars = "tmp")
        public static void singleReceive(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            tmp[0] = CombatManager.onModifyDamageReceiveFirst(tmp[0], __instance.damageTypeForTurn, GameUtilities.getCardOwner(__instance), mo, __instance);
        }

        private static class MonsterDamageReceiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0]};
            }
        }

        private static class MonsterDamageFinalReceiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[1]};
            }
        }

        private static class MonsterMultiDamageReceiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[2]};
            }
        }

        private static class MonsterMultiDamageFinalReceiveLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[3]};
            }
        }

    }

    @SpirePatch(clz = AbstractCard.class, method = "canUse")
    public static class AbstractCard_CardPlayable {
        @SpirePostfixPatch
        public static boolean method(boolean retVal, AbstractCard __instance, AbstractPlayer p, AbstractMonster m) {
            return CombatManager.canPlayCard(__instance, p, m, retVal);
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "hasEnoughEnergy"
    )
    public static class AbstractCard_HasEnoughEnergy {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Boolean> Insert(AbstractCard __instance) {
            if (CombatManager.hasEnoughEnergyForCard(__instance)) {
                return SpireReturn.Return(true);
            }
            else if (!CombatManager.hasEnoughEnergyBlocker(__instance)) {
                return SpireReturn.Return(false);
            }
            else {
                return SpireReturn.Continue();
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                // Check for reading calls only because otherwise we'll end up overwriting the writing calls as well
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(GameActionManager.class.getName()) && m.getFieldName().equals("turnHasEnded")) {
                        m.replace("{ $_ = !(pinacolada.patches.card.AbstractCardPatches.forcePlay) && $proceed($$); }");
                    }
                }
            };
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "costForTurn");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "resetAttributes")
    public static class AbstractCard_ResetAttributes {
        @SpirePostfixPatch
        public static void method(AbstractCard __instance) {
            if (PGR.isLoaded()) {
                CombatManager.onCardReset(__instance);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnOtherCardPlayed", paramtypez = {AbstractCard.class})
    public static class CardGroupPatches_TriggerOnOtherCardPlayed {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard __instance, AbstractCard c) {
            if (PCLCardTag.Fragile.has(__instance)) {
                PCLActions.last.exhaust(__instance);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnEndOfPlayerTurn")
    public static class CardGroupPatches_TriggerOnEndOfPlayerTurn {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard __instance) {
            // Call the field directly instead of querying card tag, because card tag also checks for purgeOnUse
            if (EphemeralField.value.get(__instance)) {
                PCLActions.top.purge(__instance);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}