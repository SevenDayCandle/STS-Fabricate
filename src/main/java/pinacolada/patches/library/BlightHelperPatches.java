package pinacolada.patches.library;

import basemod.ReflectionHacks;
import basemod.devcommands.blight.Blight;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.BlightTier;
import pinacolada.annotations.VisibleBlight;
import pinacolada.blights.PCLBlightData;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class BlightHelperPatches {
    private static boolean TEMP_DISABLE_PATCH;
    private static final HashMap<String, Class<? extends AbstractBlight>> additionalBlights = new HashMap<>();

    public static Collection<String> getAdditionalBlightIDs() {
        return additionalBlights.keySet();
    }

    public static ArrayList<AbstractBlight> getAdditionalBlights() {
        return EUIUtils.map(getAdditionalBlightIDs(), EUIGameUtils::getSeenBlight);
    }

    public static AbstractBlight getDirectBlight(String id) {
        AbstractBlight blight = getDirectBlightInternal(id);
        if (blight != null) {
            return blight;
        }
        // Hardcoded stuff -_-
        TEMP_DISABLE_PATCH = true;
        blight = BlightHelper.getBlight(id);
        TEMP_DISABLE_PATCH = false;
        return blight;
    }

    private static AbstractBlight getDirectBlightInternal(String id) {
        PCLBlightData data = PCLBlightData.getStaticData(id);
        if (data != null) {
            return data.create();
        }
        final Class<? extends AbstractBlight> blight = additionalBlights.get(id);
        if (blight != null) {
            try {
                return blight.getConstructor().newInstance();
            }
            catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static AbstractBlight getRandomBlightForTier(BlightTier tier) {
        return getRandomBlightForTier(tier, AbstractDungeon.relicRng, AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS, Collections.emptyList(), PGR.dungeon.allowCustomBlights);
    }

    public static AbstractBlight getRandomBlightForTier(BlightTier tier, Random rng, AbstractCard.CardColor actingColor, Collection<String> exclusions, boolean allowCustom) {
        RandomizedList<String> blights = new RandomizedList<>();
        FuncT1<Boolean, AbstractCard.CardColor> colorFunc = GameUtilities.isColorlessCardColor(actingColor) ? c -> c == AbstractCard.CardColor.COLORLESS : c -> c == AbstractCard.CardColor.COLORLESS || c == actingColor;
        if (tier == null) {
            blights.addAll(BlightHelper.blights);
            for (String a : additionalBlights.keySet()) {
                if (PCLBlightData.getStaticData(a) == null) {
                    blights.add(a);
                }
            }
            for (PCLBlightData data : PCLBlightData.getAllData(false, false, b -> b.tier != BlightTier.SPECIAL && colorFunc.invoke(b.cardColor))) {
                blights.add(data.ID);
            }
            if (allowCustom) {
                for (PCLCustomBlightSlot slot : PCLCustomBlightSlot.getBlights(actingColor)) {
                    BlightTier t = BlightTier.valueOf(slot.tier);
                    if (t != BlightTier.SPECIAL) {
                        blights.add(slot.ID);
                    }
                }
                if (actingColor != AbstractCard.CardColor.COLORLESS) {
                    for (PCLCustomBlightSlot slot : PCLCustomBlightSlot.getBlights(AbstractCard.CardColor.COLORLESS)) {
                        BlightTier t = BlightTier.valueOf(slot.tier);
                        if (t != BlightTier.SPECIAL) {
                            blights.add(slot.ID);
                        }
                    }
                }
            }
        }
        else {
            switch (tier) {
                case BOSS:
                    blights.addAll(BlightHelper.chestBlights);
                    break;
                case BASIC:
                    blights.addAll(BlightHelper.blights);
                    blights.removeIf(b -> BlightHelper.chestBlights.contains(b));
                    for (String a : additionalBlights.keySet()) {
                        if (PCLBlightData.getStaticData(a) == null) {
                            blights.add(a);
                        }
                    }
            }
            for (PCLBlightData data : PCLBlightData.getAllData(false, false, b -> b.tier == tier && colorFunc.invoke(b.cardColor))) {
                blights.add(data.ID);
            }
            if (allowCustom) {
                for (PCLCustomBlightSlot slot : PCLCustomBlightSlot.getBlights(actingColor)) {
                    BlightTier t = BlightTier.valueOf(slot.tier);
                    if (t == tier) {
                        blights.add(slot.ID);
                    }
                }
                if (actingColor != AbstractCard.CardColor.COLORLESS) {
                    for (PCLCustomBlightSlot slot : PCLCustomBlightSlot.getBlights(AbstractCard.CardColor.COLORLESS)) {
                        BlightTier t = BlightTier.valueOf(slot.tier);
                        if (t == tier) {
                            blights.add(slot.ID);
                        }
                    }
                }
            }
        }

        blights.removeAll(exclusions);

        String res = blights.retrieve(rng, false);
        return res != null ? BlightHelper.getBlight(res) : null;
    }

    public static void loadCustomBlights() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleBlight.class)) {
            try {
                VisibleBlight a = ct.getAnnotation(VisibleBlight.class);
                Object data = ReflectionHacks.getPrivateStatic(ct, a.data());
                String id = data instanceof PCLBlightData ? ((PCLBlightData) data).ID : String.valueOf(data);
                additionalBlights.put(id, (Class<? extends AbstractBlight>) ct);
                EUIUtils.logInfoIfDebug(BlightHelper.class, "Adding blight " + id);
            }
            catch (Exception e) {
                EUIUtils.logError(PGR.class, "Failed to load blight " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    @SpirePatch(clz = BlightHelper.class, method = "getBlight", paramtypez = {String.class})
    public static class BlightHelperPatches_GetBlight {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method(String id) {
            if (!TEMP_DISABLE_PATCH) {
                AbstractBlight internal = getDirectBlightInternal(id);
                if (internal != null) {
                    return SpireReturn.Return(internal);
                }
                PCLCustomBlightSlot slot = PCLCustomBlightSlot.get(id);
                if (slot != null) {
                    return SpireReturn.Return(slot.make());
                }
            }
            return SpireReturn.Continue();
        }
    }

    // Despite the name, this only pulls chest blights :)
    @SpirePatch(clz = BlightHelper.class, method = "getRandomBlight", paramtypez = {})
    public static class BlightHelperPatches_GetRandomBlight {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method() {
            return SpireReturn.Return(getRandomBlightForTier(BlightTier.BOSS));
        }
    }

    @SpirePatch(clz = BlightHelper.class, method = "getRandomBlight", paramtypez = {Random.class})
    public static class BlightHelperPatches_GetRandomBlight2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method(Random rng) {
            return SpireReturn.Return(getRandomBlightForTier(null, rng, AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS, Collections.emptyList(), PGR.dungeon.allowCustomBlights));
        }
    }

    @SpirePatch(clz = BlightHelper.class, method = "getRandomChestBlight")
    public static class BlightHelperPatches_GetRandomChestBlight {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method(ArrayList<String> exclusions) {
            return SpireReturn.Return(getRandomBlightForTier(BlightTier.BOSS, AbstractDungeon.relicRng, AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS, exclusions, PGR.dungeon.allowCustomBlights));
        }
    }
}