package pinacolada.patches.relic;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.metrics.MetricData;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import pinacolada.dungeon.CombatManager;

public class RelicObtainedPatches {
    protected static void onRelicObtain(AbstractRelic relic) {
        CombatManager.onRelicObtained(relic);
    }

    @SpirePatch(clz = AbstractRelic.class, method = "onEquip")
    public static class AbstractRelic_OnEquip {
        @SpirePostfixPatch
        public static void postfix(AbstractRelic relic) {
            onRelicObtain(relic);
        }
    }

    @SpirePatch(clz = BossRelicSelectScreen.class, method = "relicObtainLogic", paramtypez = {AbstractRelic.class})
    public static class BossRelicSelectScreenPatch {
        @SpirePrefixPatch
        public static void prefix(BossRelicSelectScreen __instance, AbstractRelic relic) {
            onRelicObtain(relic);
        }
    }

    @SpirePatch(clz = AbstractRelic.class, method = "obtain")
    public static class AbstractRelic_Obtain {
        @SpirePostfixPatch
        public static void postfix(AbstractRelic relic) {
            onRelicObtain(relic);
        }
    }

    @SpirePatch(clz = MetricData.class, method = "addRelicObtainData", paramtypez = {AbstractRelic.class})
    public static class MetricData_AddRelicObtainData {
        @SpirePrefixPatch
        public static void prefix(MetricData __instance, AbstractRelic relic) {
            onRelicObtain(relic);
        }
    }
}