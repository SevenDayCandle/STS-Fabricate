package pinacolada.powers;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

import java.util.*;

public class PCLDynamicPowerData extends PCLPowerData implements EditorMaker {
    private static final TypeToken<HashMap<Settings.GameLanguage, PowerStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, PowerStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, PowerStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public Texture portraitImage;

    public PCLDynamicPowerData(String cardID) {
        this(PGR.core, cardID);
    }

    public PCLDynamicPowerData(PCLResources<?, ?, ?, ?> resources, String cardID) {
        super(PCLDynamicPower.class, resources, cardID);
        this.tooltip = new EUIKeywordTooltip(strings.NAME);
    }

    public PCLDynamicPowerData(PCLResources<?, ?, ?, ?> resources, String cardID, PowerStrings strings) {
        super(PCLDynamicPower.class, resources, cardID, strings);
        this.tooltip = new EUIKeywordTooltip(strings.NAME);
    }

    public PCLDynamicPowerData(PCLPowerData original) {
        this(original.resources, original.ID);

        setImagePath(original.imagePath);
        setEndTurnBehavior(original.endTurnBehavior);
        setIsCommon(original.isCommon);
        setIsMetascaling(original.isMetascaling);
        setIsPostActionPower(original.isPostActionPower);
        setLimits(original.minAmount, original.maxAmount);
        setPriority(original.priority);
        setType(original.type);
    }

    public PCLDynamicPowerData(PCLDynamicPowerData original) {
        this(original.resources, original.ID);
        setImagePath(original.imagePath);
        setEndTurnBehavior(original.endTurnBehavior);
        setIsCommon(original.isCommon);
        setIsMetascaling(original.isMetascaling);
        setIsPostActionPower(original.isPostActionPower);
        setLimits(original.minAmount, original.maxAmount);
        setPriority(original.priority);
        setType(original.type);
        setImage(original.portraitImage);
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
    }

    public PCLDynamicPowerData(PCLCustomPowerSlot data, String[] effects) {
        this(data.ID);
        safeLoadValue(() -> setEndTurnBehavior(Behavior.valueOf(data.endTurnBehavior)));
        safeLoadValue(() -> setIsCommon(data.isCommon));
        safeLoadValue(() -> setIsMetascaling(data.isMetascaling));
        safeLoadValue(() -> setIsPostActionPower(data.isPostActionPower));
        safeLoadValue(() -> setLimits(data.minValue, data.maxValue));
        safeLoadValue(() -> setPriority(data.priority));
        safeLoadValue(() -> setLanguageMap(parseLanguageStrings(data.languageStrings)));
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(effects, PSkill::get), true, true));
    }

    protected static PowerStrings getInitialStrings() {
        PowerStrings retVal = new PowerStrings();
        retVal.NAME = GameUtilities.EMPTY_STRING;
        retVal.DESCRIPTIONS = new String[]{};
        return retVal;
    }

    public static PowerStrings getStringsForLanguage(HashMap<Settings.GameLanguage, PowerStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static PowerStrings getStringsForLanguage(HashMap<Settings.GameLanguage, PowerStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, PowerStrings> parseLanguageStrings(String languageStrings) {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    @Override
    public PCLDynamicPower create() {
        return create(AbstractDungeon.player, AbstractDungeon.player, 1);
    }

    public PCLDynamicPower create(AbstractCreature owner, AbstractCreature source, int amount) {
        setTextForLanguage();
        if (imagePath == null) {
            imagePath = PCLCoreImages.CardAffinity.unknown.path();
        }
        return new PCLDynamicPower(this, owner, source, amount);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return PGR.core.cardColor;
    }

    public String getEffectText() {
        return StringUtils.capitalize(EUIUtils.joinStringsMapNonnull(EUIUtils.SPLIT_LINE, PSkill::getPowerText, moves));
    }

    public String getEffectTextWithLevel(int level) {
        final StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);
        for (PSkill<?> move : moves) {
            if (move != null) {
                move.recurse(m -> m.setTemporaryAmount(m.baseAmount + level * m.getUpgrade()));
                String pText = move.getPowerText();
                if (!StringUtils.isEmpty(pText)) {
                    sj.add(pText);
                }
                move.recurse(m -> m.setTemporaryAmount(m.baseAmount));
            }
        }
        return StringUtils.capitalize(sj.toString());
    }

    @Override
    public ArrayList<PSkill<?>> getMoves() {
        return moves;
    }

    @Override
    public List<PTrigger> getPowers() {
        return Collections.emptyList();
    }

    public PowerStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
        this.imagePath = PCLCoreImages.CardAffinity.unknown.path();
    }

    @Override
    public PCLDynamicPowerData makeCopy() {
        return new PCLDynamicPowerData(this);
    }

    public PCLPowerRenderable makeRenderable() {
        return new PCLPowerRenderable(this);
    }

    public PCLPowerRenderable makeRenderableWithLevel(int level) {
        return new PCLPowerRenderable(this, new EUIKeywordTooltip(getName(), getEffectTextWithLevel(level)));
    }

    public PCLDynamicPowerData setID(String fullID) {
        this.ID = fullID;
        return this;
    }

    public PCLDynamicPowerData setImage(Texture portraitImage) {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicPowerData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicPowerData setLanguageMap(HashMap<Settings.GameLanguage, PowerStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicPowerData setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicPowerData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicPowerData setText(String name, String[] descriptions) {
        this.strings.NAME = name;
        this.strings.DESCRIPTIONS = descriptions;

        return this;
    }

    public PCLDynamicPowerData setText(PowerStrings PotionStrings) {
        return setText(PotionStrings.NAME, PotionStrings.DESCRIPTIONS);
    }

    public PCLDynamicPowerData setText(String name) {
        return setText(name, new String[0]);
    }

    public PCLDynamicPowerData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicPowerData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }

    public PCLDynamicPowerData updateTooltipText() {
        if (this.strings.DESCRIPTIONS.length > 0) {
            tooltip.setDescription(this.strings.DESCRIPTIONS[0]);
        }
        else {
            tooltip.setDescription(getEffectText());
        }
        return this;
    }
}
