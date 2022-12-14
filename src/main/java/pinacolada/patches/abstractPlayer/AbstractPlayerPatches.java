package pinacolada.patches.abstractPlayer;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CtBehavior;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.modifiers.OverrideSkillModifier;
import pinacolada.misc.CombatStats;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class AbstractPlayerPatches
{
    protected static ArrayList<AbstractMonster> targetsCache;

    @SpirePatch(clz = AbstractPlayer.class, method = "channelOrb")
    public static class AbstractPlayer_ChannelOrb
    {
        @SpireInsertPatch(rloc = 7)
        public static void insert(AbstractPlayer __instance, @ByRef AbstractOrb[] orbToSet)
        {
            AbstractOrb orb = orbToSet[0];

            // Replace the orb to be channeled according to any effects
            orbToSet[0] = CombatStats.onTryChannelOrb(orb);
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void insert2(AbstractPlayer __instance, @ByRef AbstractOrb[] orbToSet)
        {
            CombatStats.onChannel(orbToSet[0]);
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                final Matcher matcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "playChannelSFX");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class AbstractPlayer_UseCard
    {
        @SpireInsertPatch(rloc = 7)
        public static SpireReturn insertPre(AbstractPlayer __instance, AbstractCard c, AbstractMonster m, int energyOnUse)
        {
            // OverrideSkillModifier affects PCLCard skills directly so no need to invoke them below
            final PCLCard pCard = EUIUtils.safeCast(c, PCLCard.class);
            if (pCard != null)
            {
                CombatStats.onUsingCard(pCard, __instance, m);
                return SpireReturn.Return();
            }

            ArrayList<OverrideSkillModifier> wrappers = OverrideSkillModifier.getAll(c);
            if (!wrappers.isEmpty())
            {
                CombatStats.onUsingCardPostActions(c, __instance, m);
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer_UseCard.class, "Use");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "updateInput")
    public static class AbstractPlayer_UpdateInput
    {
        @SpirePostfixPatch
        public static void method(AbstractPlayer __instance)
        {
            if ((__instance.isDraggingCard || __instance.isHoveringDropZone) && __instance.hoveredCard instanceof PCLCard)
            {
                ((PCLCard) __instance.hoveredCard).onDrag(ReflectionHacks.getPrivate(__instance, AbstractPlayer.class, "hoveredMonster"));
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyPreCombatLogic")
    public static class AbstractPlayer_ApplyPreCombatLogic
    {
        @SpirePrefixPatch
        public static void method(AbstractPlayer __instance)
        {
            CombatStats.onStartup();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "renderPowerTips", paramtypez = {SpriteBatch.class})
    public static class AbstractPlayer_RenderPowerTips
    {
        @SpirePrefixPatch
        public static SpireReturn prefix(AbstractPlayer __instance, SpriteBatch sb)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                if (EUITooltip.canRenderTooltips())
                {
                    EUITooltip.queueTooltips(__instance);
                }
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage", paramtypez = {DamageInfo.class})
    public static class AbstractPlayer_Damage
    {
        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator.class)
        public static void insertPre(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount)
        {
            damageAmount[0] = Math.max(0, CombatStats.onModifyDamageFirst(__instance, info, damageAmount[0]));
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "decrementBlock");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator2.class)
        public static void insertPre2(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount)
        {
            damageAmount[0] = Math.max(0, CombatStats.onModifyDamageLast(__instance, info, damageAmount[0]));
        }

        private static class Locator2 extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                final Matcher matcher = new Matcher.MethodCallMatcher(Math.class, "min");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator3.class)
        public static void insertPre3(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount)
        {
            CombatStats.onAttack(info, damageAmount[0], __instance);
        }

        private static class Locator3 extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                final Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        @SpireInsertPatch(localvars = {"damageAmount"}, locator = Locator4.class)
        public static void insertPre4(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount)
        {
            damageAmount[0] = Math.max(0, CombatStats.onPlayerLoseHP(__instance, info, damageAmount[0]));
        }

        private static class Locator4 extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "lastDamageTaken");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "isCursed")
    public static class AbstractPlayer_IsCursed
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> method(AbstractPlayer __instance)
        {
            for (AbstractCard c : __instance.masterDeck.group)
            {
                if (c.type == AbstractCard.CardType.CURSE && c.rarity != AbstractCard.CardRarity.SPECIAL)
                {
                    return SpireReturn.Return(true);
                }
            }

            return SpireReturn.Return(false);
        }
    }

    protected static void replaceTargets(AbstractPlayer player)
    {
        final PCLCard card = EUIUtils.safeCast(player.hoveredCard, PCLCard.class);
        if (card != null && (card.pclTarget.targetsAllies() || card.type == PGR.Enums.CardType.SUMMON))
        {
            final MonsterGroup group = AbstractDungeon.getCurrRoom().monsters;
            final ArrayList<AbstractMonster> summons = CombatStats.summons.getSummons(card.type != PGR.Enums.CardType.SUMMON);
            if (card.pclTarget.targetsEnemies())
            {
                summons.addAll(group.monsters);
            }
            if (summons.size() > 0)
            {
                targetsCache = group.monsters;
                group.monsters = summons;

                // Summons should always target an available slot, regardless of whether it is occupied or not
                if (card.type == PGR.Enums.CardType.SUMMON)
                {
                    card.target = AbstractCard.CardTarget.ENEMY;
                    PCLCardAlly.emptyAnimation.highlight();
                }
                // For cards that can also target yourself, assume you are targeting yourself if nothing else is hovered
                else
                {
                    PCLCardAlly.emptyAnimation.unhighlight();
                    if (card.pclTarget.targetsSelf() && !EUIUtils.any(group.monsters, g -> g.hb.hovered))
                    {
                        card.target = AbstractCard.CardTarget.SELF;
                    }
                    else
                    {
                        card.target = card.pclTarget.cardTarget;
                    }
                }
                return;
            }
        }
        PCLCardAlly.emptyAnimation.unhighlight();
        targetsCache = null;
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "clickAndDragCards")
    public static class AbstractPlayer_ClickAndDragCards
    {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance)
        {
            replaceTargets(__instance);
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance)
        {
            if (targetsCache != null)
            {
                AbstractDungeon.getCurrRoom().monsters.monsters = targetsCache;
                targetsCache = null;
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
    public static class AbstractPlayer_UpdateSingleTargetInput
    {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance)
        {
            replaceTargets(__instance);
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance)
        {
            if (targetsCache != null)
            {
                AbstractDungeon.getCurrRoom().monsters.monsters = targetsCache;
                targetsCache = null;
            }
        }
    }
}
