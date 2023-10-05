package pinacolada.blights;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import extendedui.EUIUtils;
import extendedui.utilities.BlightTier;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.blights.PCLDynamicBlight;
import pinacolada.blights.PCLBlightData;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;
import java.util.HashMap;

public class PCLDynamicBlightData extends PCLBlightData implements EditorMaker {
    private static final TypeToken<HashMap<Settings.GameLanguage, BlightStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, BlightStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, BlightStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PTrigger> powers = new ArrayList<>();
    public Texture portraitImage;

    public PCLDynamicBlightData(String cardID) {
        super(PCLDynamicBlight.class, PGR.core, cardID);
    }

    public PCLDynamicBlightData(String cardID, PCLResources<?, ?, ?, ?> resources) {
        super(PCLDynamicBlight.class, resources, cardID);
    }

    public PCLDynamicBlightData(PCLResources<?, ?, ?, ?> resources, String cardID, BlightStrings strings) {
        super(PCLDynamicBlight.class, resources, cardID, strings);
    }

    public PCLDynamicBlightData(PCLBlightData original) {
        this(original.ID, original.resources);

        setImagePath(original.imagePath);
        setColor(original.cardColor);
        setTier(original.tier);
        setUnique(original.unique);
        setMaxUpgrades(original.maxUpgradeLevel);
        setBranchFactor(original.branchFactor);
    }

    public PCLDynamicBlightData(PCLDynamicBlightData original) {
        this(original.ID, original.resources);
        setImagePath(original.imagePath);
        setImage(original.portraitImage);
        setColor(original.cardColor);
        setTier(original.tier);
        setUnique(original.unique);
        setMaxUpgrades(original.maxUpgradeLevel);
        setBranchFactor(original.branchFactor);
        setLanguageMap(original.languageMap);
        setCounter(original.counter.clone(), original.counterUpgrade.clone());
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
    }

    public PCLDynamicBlightData(PCLCustomBlightSlot data, PCLCustomEditorLoadable.EffectItemForm f) {
        this(data.ID);
        safeLoadValue(() -> setColor(data.slotColor));
        safeLoadValue(() -> setTier(BlightTier.valueOf(data.tier)));
        safeLoadValue(() -> setUnique(data.unique));
        safeLoadValue(() -> setLanguageMap(parseLanguageStrings(data.languageStrings)));
        safeLoadValue(() -> counter = data.counter.clone());
        safeLoadValue(() -> counterUpgrade = data.counterUpgrade.clone());
        safeLoadValue(() -> setMaxUpgrades(data.maxUpgradeLevel));
        safeLoadValue(() -> setBranchFactor(data.branchUpgradeFactor));
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(f.powerEffects, pe -> EUIUtils.safeCast(PSkill.get(pe), PTrigger.class))));
    }

    protected static BlightStrings getInitialStrings() {
        BlightStrings retVal = new BlightStrings();
        retVal.NAME = EUIUtils.EMPTY_STRING;
        retVal.DESCRIPTION = new String[]{};
        return retVal;
    }

    public static BlightStrings getStringsForLanguage(HashMap<Settings.GameLanguage, BlightStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static BlightStrings getStringsForLanguage(HashMap<Settings.GameLanguage, BlightStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, BlightStrings> parseLanguageStrings(String languageStrings) {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    public PCLDynamicBlight create() {
        setTextForLanguage();
        if (imagePath == null) {
            imagePath = PCLCoreImages.CardAffinity.unknown.path();
        }
        return new PCLDynamicBlight(this);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return cardColor;
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
    public ArrayList<PTrigger> getPowers() {
        return powers;
    }

    public BlightStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
        this.imagePath = PCLCoreImages.CardAffinity.unknown.path();
    }

    @Override
    public PCLDynamicBlightData makeCopy() {
        return new PCLDynamicBlightData(this);
    }

    public PCLDynamicBlightData setColor(AbstractCard.CardColor color) {
        super.setColor(color);
        return this;
    }

    public PCLDynamicBlightData setID(String fullID) {
        this.ID = fullID;
        return this;
    }

    public PCLDynamicBlightData setImage(Texture portraitImage) {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicBlightData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicBlightData setLanguageMap(HashMap<Settings.GameLanguage, BlightStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicBlightData setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicBlightData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicBlightData setText(String name, String[] descriptions) {
        this.strings.NAME = name;
        this.strings.DESCRIPTION = descriptions;

        return this;
    }

    public PCLDynamicBlightData setText(BlightStrings blightStrings) {
        return setText(blightStrings.NAME, blightStrings.DESCRIPTION);
    }

    public PCLDynamicBlightData setText(String name) {
        return setText(name, new String[0]);
    }

    public PCLDynamicBlightData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicBlightData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }
}
