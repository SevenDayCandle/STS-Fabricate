package pinacolada.augments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.blights.PCLDynamicBlightData;
import pinacolada.cards.base.fields.PCLCardDataAffinityGroup;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.misc.AugmentStrings;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.monsters.animations.pcl.PCLGeneralAllyAnimation;
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.orbs.PCLDynamicOrb;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

public class PCLDynamicAugmentData extends PCLAugmentData implements EditorMaker<PCLDynamicAugment> {
    private static final TypeToken<HashMap<Settings.GameLanguage, AugmentStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, AugmentStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, AugmentStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PSkill<?>> powers = new ArrayList<>();
    public Texture portraitImage;

    public PCLDynamicAugmentData(String cardID) {
        this(PGR.core, cardID);
    }

    public PCLDynamicAugmentData(PCLResources<?, ?, ?, ?> resources, String cardID) {
        super(PCLDynamicAugment.class, resources, PCLAugmentCategory.General, cardID);
    }

    public PCLDynamicAugmentData(PCLResources<?, ?, ?, ?> resources, String cardID, AugmentStrings strings) {
        super(PCLDynamicAugment.class, resources, PCLAugmentCategory.General, cardID, strings);
    }

    public PCLDynamicAugmentData(PCLAugmentData original) {
        this(original.resources, original.ID);
        setBranchFactor(original.branchFactor);
        setCategory(original.category);
        setColor(original.cardColor);
        setImagePath(original.imagePath);
        setMaxUpgrades(original.maxUpgradeLevel);
        setPermanent(original.permanent);
        setReqs(original.reqs != null ? new PCLAugmentReqs(original.reqs) : null);
        setTier(original.tier, original.tierUpgrade);
        setUnique(original.unique);
    }

    public PCLDynamicAugmentData(PCLDynamicAugmentData original) {
        this(original.resources, original.ID);
        setBranchFactor(original.branchFactor);
        setCategory(original.category);
        setColor(original.cardColor);
        setImagePath(original.imagePath);
        setMaxUpgrades(original.maxUpgradeLevel);
        setPermanent(original.permanent);
        setReqs(original.reqs != null ? new PCLAugmentReqs(original.reqs) : null);
        setTier(original.tier, original.tierUpgrade);
        setUnique(original.unique);
        setImage(original.portraitImage);
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
    }

    public PCLDynamicAugmentData(PCLCustomAugmentSlot data, PCLCustomEditorLoadable.EffectItemForm form) {
        this(data.ID);
        safeLoadValue(() -> setBranchFactor(data.branchUpgradeFactor));
        safeLoadValue(() -> setCategory(PCLAugmentCategory.valueOf(data.category)));
        safeLoadValue(() -> setColor(data.slotColor));
        safeLoadValue(() -> tier = data.tier.clone());
        safeLoadValue(() -> tierUpgrade = data.tierUpgrade.clone());
        safeLoadValue(() -> setMaxUpgrades(data.maxUpgradeLevel));
        safeLoadValue(() -> setPermanent(data.permanent));
        safeLoadValue(() -> setUnique(data.unique));
        safeLoadValue(() -> setReqs(EUIUtils.deserialize(data.reqs, PCLAugmentReqs.class)));
        safeLoadValue(() -> setLanguageMap(parseLanguageStrings(data.languageStrings)));
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(form.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(form.powerEffects, PSkill::get), true, true));
    }

    protected static AugmentStrings getInitialStrings() {
        AugmentStrings retVal = new AugmentStrings();
        retVal.NAME = EUIUtils.EMPTY_STRING;
        retVal.DESCRIPTION = new String[]{};
        return retVal;
    }

    public static AugmentStrings getStringsForLanguage(HashMap<Settings.GameLanguage, AugmentStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static AugmentStrings getStringsForLanguage(HashMap<Settings.GameLanguage, AugmentStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, AugmentStrings> parseLanguageStrings(String languageStrings) {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    @Override
    public PCLDynamicAugment create() {
        return create(new PCLAugment.SaveData(ID, 0, 0));
    }

    @Override
    public PCLDynamicAugment create(int form, int timesUpgraded) {
        return create(new PCLAugment.SaveData(ID, form, timesUpgraded));
    }

    @Override
    PCLDynamicAugment create(PCLAugment.SaveData save) {
        setTextForLanguage();
        if (imagePath == null) {
            imagePath = PCLCoreImages.CardAffinity.unknown.path();
        }
        return new PCLDynamicAugment(this, save);
    }

    public PCLAugmentRenderable createRenderable(int form, int timesUpgraded) {
        PCLDynamicAugment augment = create(form, timesUpgraded);
        return new PCLAugmentRenderable(augment);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return PGR.core.cardColor;
    }

    public String getEffectTextForPreview(int level) {
        final StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);
        for (PSkill<?> move : moves) {
            if (!PSkill.isSkillBlank(move)) {
                move.recurse(m -> m.setTemporaryAmount(m.baseAmount + level * m.getUpgrade()));
                String pText = move.getPowerText(this);
                if (!StringUtils.isEmpty(pText)) {
                    sj.add(StringUtils.capitalize(pText));
                }
                move.recurse(m -> m.setTemporaryAmount(m.baseAmount));
            }
        }
        return sj.toString();
    }

    @Override
    public Texture getImage() {
        return portraitImage;
    }

    @Override
    public ArrayList<PSkill<?>> getMoves() {
        return moves;
    }

    @Override
    public List<PSkill<?>> getPowers() {
        return powers;
    }

    public AugmentStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
        this.imagePath = PCLCoreImages.CardAffinity.unknown.path();
    }

    @Override
    public PCLDynamicAugmentData makeCopy() {
        return new PCLDynamicAugmentData(this);
    }

    public PCLDynamicAugmentData setColor(AbstractCard.CardColor color) {
        super.setColor(color);
        return this;
    }

    public PCLDynamicAugmentData setID(String fullID) {
        this.ID = fullID;
        return this;
    }

    public PCLDynamicAugmentData setImage(Texture portraitImage) {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicAugmentData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicAugmentData setLanguageMap(HashMap<Settings.GameLanguage, AugmentStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicAugmentData setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicAugmentData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicAugmentData setText(String name, String[] descriptions) {
        this.strings.NAME = name;
        this.strings.DESCRIPTION = descriptions;

        return this;
    }

    public PCLDynamicAugmentData setText(AugmentStrings str) {
        return setText(str.NAME, str.DESCRIPTION);
    }

    public PCLDynamicAugmentData setText(String name) {
        return setText(name, new String[0]);
    }

    public PCLDynamicAugmentData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicAugmentData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }
}
