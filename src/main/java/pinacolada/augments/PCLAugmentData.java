package pinacolada.augments;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.misc.AugmentStrings;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static extendedui.EUIUtils.array;

public class PCLAugmentData extends PCLGenericData<PCLAugment> {
    private static final HashMap<String, PCLAugmentData> AUGMENT_MAP = new HashMap<>();
    private static final ArrayList<PCLAugmentData> AVAILABLE_AUGMENTS = new ArrayList<>();

    public Integer[] tier = array(1);
    public Integer[] tierUpgrade = array(1);
    public PCLAugmentCategory category;
    public AugmentStrings strings;
    public PCLAugmentReqs reqs;
    public boolean permanent;
    public boolean unique;
    public int maxForms = 1;
    public int maxUpgradeLevel = 0;
    public int branchFactor = 0;

    public PCLAugmentData(Class<? extends PCLAugment> invokeClass, PCLResources<?, ?, ?, ?> resources, PCLAugmentCategory category) {
        this(invokeClass, resources, category, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLAugmentData(Class<? extends PCLAugment> invokeClass, PCLResources<?, ?, ?, ?> resources, PCLAugmentCategory category, String id) {
        super(id, invokeClass, resources);
        this.category = category;
        strings = PGR.getAugmentStrings(ID);
        initializeImage();
    }

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

    public boolean canApply(AbstractCard c) {
        return c instanceof PCLCard && canApplyImpl((PCLCard) c);
    }

    protected boolean canApplyImpl(PCLCard c) {
        return c != null
                && c.getFreeAugmentSlot() >= 0
                && (category.isTypeValid(c.type))
                && (reqs == null || reqs.check(c))
                && (unique || !EUIUtils.any(c.getAugments(), a -> a.data.ID.equals(ID)));
    }

    @Override
    public PCLAugment create() {
        return create(new PCLAugment.SaveData(ID, 0, 0));
    }

    public PCLAugment create(int form, int timesUpgraded) {
        return create(new PCLAugment.SaveData(ID, form, timesUpgraded));
    }

    // Should not get called by anything else other than the save data file
    PCLAugment create(PCLAugment.SaveData save) {
        try {
            if (constructor == null) {
                constructor = createConstructor(PCLAugment.SaveData.class);
                if (constructor == null) {
                    constructor = invokeClass.getConstructor();
                }
                constructor.setAccessible(true);
            }

            if (constructor.getParameterCount() > 0) {
                return constructor.newInstance(save);
            }
            else {
                return constructor.newInstance();
            }
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NullPointerException e) {
            throw new RuntimeException(ID, e);
        }
    }

    private Constructor<? extends PCLAugment> createConstructor(Class<?>... paramtypes) {
        try {
            return invokeClass.getDeclaredConstructor(paramtypes);
        }
        catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    public PCLAugmentRenderable createRenderable(int form, int timesUpgraded) {
        PCLAugment augment = create(form, timesUpgraded);
        return augment != null ? new PCLAugmentRenderable(augment) : null;
    }

    public String getName() {
        return strings.NAME;
    }

    public String getReqsString() {
        return reqs == null ? null : reqs.getString();
    }

    public Texture getTexture() {
        return EUIRM.getTexture(imagePath);
    }

    public Texture getTextureBase() {
        return category.getIcon();
    }

    public int getTier(int form) {
        return tier[Math.min(tier.length - 1, form)];
    }

    public int getTierUpgrade(int form) {
        return tierUpgrade[Math.min(tierUpgrade.length - 1, form)];
    }

    public void initializeImage() {
        this.imagePath = PGR.getAugmentImage(ID);
    }

    public PCLAugmentData setBranchFactor(int factor) {
        this.branchFactor = factor;

        return this;
    }

    public PCLAugmentData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLAugmentData setMaxForms(int maxForms) {
        this.maxForms = maxForms;

        return this;
    }

    public PCLAugmentData setMaxUpgrades(int maxUpgradeLevel) {
        this.maxUpgradeLevel = MathUtils.clamp(maxUpgradeLevel, -1, Integer.MAX_VALUE);

        return this;
    }

    public PCLAugmentData setReqs(PCLAugmentReqs reqs) {
        this.reqs = reqs;
        return this;
    }

    public PCLAugmentData setPermanent(boolean value) {
        this.permanent = value;
        return this;
    }

    public PCLAugmentData setTier(int heal) {
        this.tier[0] = heal;
        return this;
    }

    public PCLAugmentData setTier(int heal, int healUpgrade) {
        this.tier[0] = heal;
        this.tierUpgrade[0] = healUpgrade;
        return this;
    }
}
