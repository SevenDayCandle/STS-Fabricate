package pinacolada.augments;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.misc.AugmentStrings;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.PMultiTrait;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.WeightedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class PCLAugmentData extends PCLGenericData<PCLAugment> {
    private static final HashMap<String, PCLAugmentData> AUGMENT_MAP = new HashMap<>();
    private static final ArrayList<PCLAugmentData> AVAILABLE_AUGMENTS = new ArrayList<>();

    public int tier;
    public PCLAugmentCategory category;
    public PCLAugmentCategorySub lineage;
    public AugmentStrings strings;
    public PSkill<?> skill;
    public PCLAugmentReqs reqs;
    public boolean isSpecial;

    public static PCLAugmentData get(String id) {
        return AUGMENT_MAP.get(id);
    }

    public static Collection<PCLAugmentData> getAvailable() {
        return AVAILABLE_AUGMENTS;
    }

    public static Set<String> getIDs() {
        return AUGMENT_MAP.keySet();
    }

    public static Collection<PCLAugmentData> getValues() {
        return AUGMENT_MAP.values();
    }

    public static int getWeight(PCLAugmentWeights weights, PCLAugmentData data) {
        return getWeight(weights, data, false);
    }

    public static int getWeight(PCLAugmentWeights weights, PCLAugmentData data, boolean allowSpecial) {
        return (data.isSpecial && !allowSpecial ? 0 : weights.getWeight(data.category) - Math.max(0, data.tier - weights.getRareModifier())) * PCLAugment.WEIGHT_MODIFIER;
    }

    public static PCLAugmentData getWeighted(Random rng, PCLAugmentWeights weights) {
        return getWeightedList(weights).retrieve(rng);
    }

    public static WeightedList<PCLAugmentData> getWeightedList(PCLAugmentWeights weights) {
        return getWeightedList(weights, false);
    }

    public static WeightedList<PCLAugmentData> getWeightedList(PCLAugmentWeights weights, boolean allowSpecial) {
        final WeightedList<PCLAugmentData> weightedList = new WeightedList<>();
        for (PCLAugmentData data : AUGMENT_MAP.values()) {
            int weight = getWeight(weights, data, allowSpecial);
            if (weight > 0) {
                weightedList.add(data, weight);
            }
        }
        return weightedList;
    }

    // Each ID must be called at least once to have it selectable in the console
    public static void initialize() {
        for (Class<?> augmentClass : GameUtilities.getClassesWithAnnotation(VisibleAugment.class)) {
            try {
                VisibleAugment a = augmentClass.getAnnotation(VisibleAugment.class);
                PCLAugmentData data = ReflectionHacks.getPrivateStatic(augmentClass, a.data());
                AVAILABLE_AUGMENTS.add(data);
                EUIUtils.logInfoIfDebug(PCLAugment.class, "Adding augment " + data.ID);
            }
            catch (Exception e) {
                EUIUtils.logError(PCLAugment.class, "Failed to load augment " + augmentClass + ": " + e.getLocalizedMessage());
            }
        }
    }

    protected static <T extends PCLAugmentData> T registerData(T cardData) {
        AUGMENT_MAP.put(cardData.ID, cardData);
        return cardData;
    }

    public PCLAugmentData(Class<? extends PCLAugment> invokeClass, PCLResources<?, ?, ?, ?> resources, PCLAugmentCategorySub lineage, int tier) {
        this(invokeClass, resources, lineage, tier, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLAugmentData(Class<? extends PCLAugment> invokeClass, PCLResources<?, ?, ?, ?> resources, PCLAugmentCategorySub lineage, int tier, String id) {
        super(id, invokeClass, resources);
        this.tier = tier;
        this.lineage = lineage;
        this.category = lineage.parent;
        strings = PGR.getAugmentStrings(ID);
    }

    public boolean canApply(AbstractCard c) {
        return c instanceof PCLCard && canApplyImpl((PCLCard) c);
    }

    protected boolean canApplyImpl(PCLCard c) {
        return c != null && (reqs == null || reqs.check(c)) && c.getFreeAugmentSlot() >= 0;
    }

    public PCLAugmentData setReqs(PCLAugmentReqs reqs) {
        this.reqs = reqs;
        return this;
    }

    public PCLAugmentData setSkill(PSkill<?>... skills) {
        this.skill = PMultiSkill.join(skills);
        return this;
    }

    public PCLAugmentData setSkill(PSkill<?> skill) {
        this.skill = skill;
        return this;
    }

    public PCLAugmentData setSkill(PTrait<?>... traits) {
        this.skill = PMultiTrait.join(traits);
        return this;
    }

    public PCLAugmentData setSpecial(boolean value) {
        this.isSpecial = value;
        return this;
    }
}
