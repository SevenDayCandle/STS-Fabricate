package pinacolada.potions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;

public class PCLDynamicPotionData extends PCLPotionData implements EditorMaker {
    private static final TypeToken<HashMap<Settings.GameLanguage, PotionStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, PotionStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, PotionStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PTrigger> powers = new ArrayList<>();
    public Texture portraitImage;

    public PCLDynamicPotionData(String cardID) {
        super(PCLDynamicPotion.class, PGR.core, cardID);
    }

    public PCLDynamicPotionData(String cardID, PCLResources<?, ?, ?, ?> resources) {
        super(PCLDynamicPotion.class, resources, cardID);
    }

    public PCLDynamicPotionData(PCLResources<?, ?, ?, ?> resources, String cardID, PotionStrings strings) {
        super(PCLDynamicPotion.class, resources, cardID, strings);
    }

    public PCLDynamicPotionData(PCLPotionData original) {
        this(original.ID, original.resources);

        setImagePath(original.imagePath);
        setColor(original.cardColor);
        setEffect(original.effect);
        setBottleColor(original.liquidColor.cpy(), original.hybridColor.cpy(), original.spotsColor.cpy());
        setRarity(original.rarity);
        setSize(original.size);
        setMaxUpgrades(original.maxUpgradeLevel);
        setBranchFactor(original.branchFactor);
    }

    public PCLDynamicPotionData(PCLDynamicPotionData original) {
        this(original.ID, original.resources);
        setImagePath(original.imagePath);
        setImage(original.portraitImage);
        setColor(original.cardColor);
        setEffect(original.effect);
        setBottleColor(original.liquidColor.cpy(), original.hybridColor.cpy(), original.spotsColor.cpy());
        setRarity(original.rarity);
        setSize(original.size);
        setMaxUpgrades(original.maxUpgradeLevel);
        setBranchFactor(original.branchFactor);
        setLanguageMap(original.languageMap);
        setCounter(original.counter.clone(), original.counterUpgrade.clone());
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
    }

    public PCLDynamicPotionData(PCLCustomPotionSlot data, PCLCustomPotionSlot.PotionForm f) {
        this(data.ID);
        safeLoadValue(() -> setColor(data.slotColor));
        safeLoadValue(() -> setRarity(AbstractPotion.PotionRarity.valueOf(data.rarity)));
        safeLoadValue(() -> setEffect(AbstractPotion.PotionEffect.valueOf(data.effect)));
        safeLoadValue(() -> setSize(AbstractPotion.PotionSize.valueOf(data.size)));
        safeLoadValue(() -> setBottleColor(Color.valueOf(data.liquidColor), Color.valueOf(data.hybridColor), Color.valueOf(data.spotsColor)));
        safeLoadValue(() -> setLanguageMap(parseLanguageStrings(data.languageStrings)));
        safeLoadValue(() -> counter = data.counter.clone());
        safeLoadValue(() -> counterUpgrade = data.counterUpgrade.clone());
        safeLoadValue(() -> setMaxUpgrades(data.maxUpgradeLevel));
        safeLoadValue(() -> setBranchFactor(data.branchUpgradeFactor));
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(f.powerEffects, pe -> EUIUtils.safeCast(PSkill.get(pe), PTrigger.class))));
    }

    protected static PotionStrings getInitialStrings() {
        PotionStrings retVal = new PotionStrings();
        retVal.NAME = GameUtilities.EMPTY_STRING;
        retVal.DESCRIPTIONS = new String[]{};
        return retVal;
    }

    public static PotionStrings getStringsForLanguage(HashMap<Settings.GameLanguage, PotionStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static PotionStrings getStringsForLanguage(HashMap<Settings.GameLanguage, PotionStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, PotionStrings> parseLanguageStrings(String languageStrings) {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    public PCLDynamicPotion create() {
        setTextForLanguage();
        if (imagePath == null) {
            imagePath = PCLCoreImages.CardAffinity.unknown.path();
        }
        return new PCLDynamicPotion(this);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return cardColor;
    }

    @Override
    public ArrayList<PSkill<?>> getMoves() {
        return moves;
    }

    @Override
    public ArrayList<PTrigger> getPowers() {
        return powers;
    }

    public PotionStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
        this.imagePath = PCLCoreImages.CardAffinity.unknown.path();
    }

    @Override
    public PCLDynamicPotionData makeCopy() {
        return new PCLDynamicPotionData(this);
    }

    public PCLDynamicPotionData setColor(AbstractCard.CardColor color) {
        super.setColor(color);
        return this;
    }

    public PCLDynamicPotionData setID(String fullID) {
        this.ID = fullID;
        return this;
    }

    public PCLDynamicPotionData setImage(Texture portraitImage) {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicPotionData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicPotionData setLanguageMap(HashMap<Settings.GameLanguage, PotionStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicPotionData setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicPotionData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicPotionData setText(String name, String[] descriptions) {
        this.strings.NAME = name;
        this.strings.DESCRIPTIONS = descriptions;

        return this;
    }

    public PCLDynamicPotionData setText(PotionStrings PotionStrings) {
        return setText(PotionStrings.NAME, PotionStrings.DESCRIPTIONS);
    }

    public PCLDynamicPotionData setText(String name) {
        return setText(name, new String[0]);
    }

    public PCLDynamicPotionData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicPotionData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }
}
