package pinacolada.relics;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;
import java.util.HashMap;

public class PCLDynamicRelicData extends PCLRelicData implements EditorMaker<PCLDynamicRelic> {
    private static final TypeToken<HashMap<Settings.GameLanguage, RelicStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, RelicStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, RelicStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PSkill<?>> powers = new ArrayList<>();
    public Texture portraitImage;

    public PCLDynamicRelicData(String cardID) {
        super(PCLDynamicRelic.class, PGR.core, cardID);
    }

    public PCLDynamicRelicData(String cardID, PCLResources<?, ?, ?, ?> resources) {
        super(PCLDynamicRelic.class, resources, cardID);
    }

    public PCLDynamicRelicData(PCLResources<?, ?, ?, ?> resources, String cardID, RelicStrings strings) {
        super(PCLDynamicRelic.class, resources, cardID, strings);
    }

    public PCLDynamicRelicData(PCLRelicData original) {
        this(original.ID, original.resources);
        setImagePath(original.imagePath);
        setColor(original.cardColor);
        setSfx(original.sfx);
        setTier(original.tier);
        setMaxUpgrades(original.maxUpgradeLevel);
        setBranchFactor(original.branchFactor);
        setCounter(original.counter.clone(), original.counterUpgrade.clone());
        setLoadoutValue(original.getLoadoutValue());
        if (original.replacementIDs != null) {
            setReplacementIDs(original.replacementIDs.clone());
        }
    }

    public PCLDynamicRelicData(PCLDynamicRelicData original) {
        this(original.ID, original.resources);
        setImagePath(original.imagePath);
        setImage(original.portraitImage);
        setColor(original.cardColor);
        setSfx(original.sfx);
        setTier(original.tier);
        setMaxUpgrades(original.maxUpgradeLevel);
        setBranchFactor(original.branchFactor);
        setCounter(original.counter.clone(), original.counterUpgrade.clone());
        setLoadoutValue(original.getLoadoutValue());
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
        if (original.replacementIDs != null) {
            setReplacementIDs(original.replacementIDs.clone());
        }
    }

    public PCLDynamicRelicData(PCLCustomRelicSlot data, PCLCustomEditorLoadable.EffectItemForm f) {
        this(data.ID);
        safeLoadValue(() -> setColor(data.slotColor));
        safeLoadValue(() -> setSfx(AbstractRelic.LandingSound.valueOf(data.sfx)));
        safeLoadValue(() -> setTier(AbstractRelic.RelicTier.valueOf(data.tier)));
        safeLoadValue(() -> setLanguageMap(parseLanguageStrings(data.languageStrings)));
        safeLoadValue(() -> counter = data.counter.clone());
        safeLoadValue(() -> counterUpgrade = data.counterUpgrade.clone());
        safeLoadValue(() -> setMaxUpgrades(data.maxUpgradeLevel));
        safeLoadValue(() -> setBranchFactor(data.branchUpgradeFactor));
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(f.powerEffects, pe -> EUIUtils.safeCast(PSkill.get(pe), PTrigger.class))));
        safeLoadValue(() -> setLoadoutValue(data.loadoutValue));
        safeLoadValue(() -> {
            if (data.replacementIDs != null) {
                setReplacementIDs(data.replacementIDs);
            }
        });
    }

    protected static RelicStrings getInitialStrings() {
        RelicStrings retVal = new RelicStrings();
        retVal.NAME = EUIUtils.EMPTY_STRING;
        retVal.DESCRIPTIONS = new String[]{};
        retVal.FLAVOR = EUIUtils.EMPTY_STRING;
        return retVal;
    }

    public static RelicStrings getStringsForLanguage(HashMap<Settings.GameLanguage, RelicStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static RelicStrings getStringsForLanguage(HashMap<Settings.GameLanguage, RelicStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, RelicStrings> parseLanguageStrings(String languageStrings) {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    public PCLDynamicRelic create() {
        setTextForLanguage();
        if (imagePath == null) {
            imagePath = PCLCoreImages.CardAffinity.unknown.path();
        }
        return new PCLDynamicRelic(this);
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
    public ArrayList<PSkill<?>> getPowers() {
        return powers;
    }

    public RelicStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
        this.imagePath = PCLCoreImages.CardAffinity.unknown.path();
    }

    @Override
    public PCLDynamicRelicData makeCopy() {
        return new PCLDynamicRelicData(this);
    }

    public PCLDynamicRelicData setColor(AbstractCard.CardColor color) {
        super.setColor(color);
        return this;
    }

    public PCLDynamicRelicData setID(String fullID) {
        this.ID = fullID;
        return this;
    }

    public PCLDynamicRelicData setImage(Texture portraitImage) {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicRelicData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicRelicData setLanguageMap(HashMap<Settings.GameLanguage, RelicStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicRelicData setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicRelicData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicRelicData setText(String name, String[] descriptions) {
        return setText(name, descriptions, EUIUtils.EMPTY_STRING);
    }

    public PCLDynamicRelicData setText(String name, String[] descriptions, String flavor) {
        this.strings.NAME = name;
        this.strings.DESCRIPTIONS = descriptions;
        this.strings.FLAVOR = flavor;

        return this;
    }

    public PCLDynamicRelicData setText(RelicStrings relicStrings) {
        return setText(relicStrings.NAME, relicStrings.DESCRIPTIONS, relicStrings.FLAVOR);
    }

    public PCLDynamicRelicData setText(String name) {
        return setText(name, new String[0], EUIUtils.EMPTY_STRING);
    }

    public PCLDynamicRelicData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicRelicData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }
}
