package pinacolada.patches.basemod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CommonKeywordIconsPatches;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.evacipated.cardcrawl.mod.stslib.patches.FlavorText;
import com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces.BetterOnUsePotionPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;

public class STSLibPatches {
    @SpirePatch(clz = FlavorText.FlavorIntoCardStrings.class, method = "postfix")
    public static class FlavorIntoCardStrings_Postfix {
        // Custom cards do not have existing flavor text so this call will cause the card to fail to load altogether
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard c) {
            if (c instanceof FabricateItem) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = BetterOnUsePotionPatch.class, method = "Do")
    public static class BetterOnUsePotionPatch_Do {
        @SpirePrefixPatch
        public static void prefix(AbstractPotion c) {
            CombatManager.onUsePotion(c);
        }
    }

    @SpirePatch(clz = CommonKeywordIconsPatches.class, method = "RenderBadges")
    public static class CommonKeywordIconsPatches_RenderBadges {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(SpriteBatch sb, AbstractCard card) {
            if (PGR.config.displayCardTagDescription.get()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CommonKeywordIconsPatches.SingleCardViewRenderIconOnCard.class, method = "patch")
    public static class CommonKeywordIconsPatches_SingleCardViewRenderIconOnCard {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix() {
            if (PGR.config.displayCardTagDescription.get()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CustomTargeting.SetFinalTarget.class, method = "setFinalTarget")
    public static class CustomTargetingPatches_SetFinalTarget {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasPower")) {
                        m.replace("{ $_ = $proceed($$) && pinacolada.patches.basemod.STSLibPatches.CustomTargetingPatches_SetFinalTarget.check($0, o) ; }");
                    }
                }
            };
        }

        // Do not flip for summons and allies
        public static boolean check(AbstractPlayer p, Object m) {
            return p.hoveredCard.type != PCLEnum.CardType.SUMMON && !(m instanceof PCLCardAlly);
        }
    }
}
