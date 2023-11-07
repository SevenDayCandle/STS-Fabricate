package pinacolada.orbs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLDynamicPower;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.PCLPowerRenderable;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

public class PCLDynamicOrbData extends PCLOrbData implements EditorMaker<PCLDynamicOrb> {
    private static final TypeToken<HashMap<Settings.GameLanguage, OrbStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, OrbStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, OrbStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PSkill<?>> powers = new ArrayList<>();
    public Texture portraitImage;

    public PCLDynamicOrbData(String cardID) {
        this(PGR.core, cardID);
    }

    public PCLDynamicOrbData(PCLResources<?, ?, ?, ?> resources, String cardID) {
        super(PCLDynamicOrb.class, resources, cardID);
    }

    public PCLDynamicOrbData(PCLResources<?, ?, ?, ?> resources, String cardID, OrbStrings strings) {
        super(PCLDynamicOrb.class, resources, cardID, strings);
    }

    public PCLDynamicOrbData(PCLOrbData original) {
        this(original.resources, original.ID);
        setImagePath(original.imagePath);
        setApplyFocusToEvoke(original.applyFocusToEvoke);
        setApplyFocusToPassive(original.applyFocusToPassive);
        setBaseEvokeValue(original.baseEvokeValue);
        setBasePassiveValue(original.basePassiveValue);
        setFlareColor1(original.flareColor1);
        setFlareColor2(original.flareColor2);
        setRotationSpeed(original.rotationSpeed);
        setSfx(original.sfx);
        setTiming(original.timing);
    }

    public PCLDynamicOrbData(PCLDynamicOrbData original) {
        this(original.resources, original.ID);
        setImagePath(original.imagePath);
        setApplyFocusToEvoke(original.applyFocusToEvoke);
        setApplyFocusToPassive(original.applyFocusToPassive);
        setBaseEvokeValue(original.baseEvokeValue);
        setBasePassiveValue(original.basePassiveValue);
        setFlareColor1(original.flareColor1);
        setFlareColor2(original.flareColor2);
        setRotationSpeed(original.rotationSpeed);
        setSfx(original.sfx);
        setTiming(original.timing);
        setImage(original.portraitImage);
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
    }

    public PCLDynamicOrbData(PCLCustomOrbSlot data, PCLCustomEditorLoadable.EffectItemForm form) {
        this(data.ID);
        safeLoadValue(() -> setApplyFocusToEvoke(data.applyFocusToEvoke));
        safeLoadValue(() -> setApplyFocusToPassive(data.applyFocusToPassive));
        safeLoadValue(() -> setBaseEvokeValue(data.baseEvokeValue));
        safeLoadValue(() -> setBasePassiveValue(data.basePassiveValue));
        safeLoadValue(() -> setBaseEvokeValue(data.baseEvokeValue));
        safeLoadValue(() -> setFlareColor1(Color.valueOf(data.flareColor1)));
        safeLoadValue(() -> setFlareColor2(Color.valueOf(data.flareColor2)));
        safeLoadValue(() -> setRotationSpeed(data.rotationSpeed));
        safeLoadValue(() -> setSfx(data.sfx));
        safeLoadValue(() -> setTiming(DelayTiming.valueOf(data.timing)));
        safeLoadValue(() -> setLanguageMap(parseLanguageStrings(data.languageStrings)));
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(form.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(form.powerEffects, PSkill::get), true, true));
    }

    protected static OrbStrings getInitialStrings() {
        OrbStrings retVal = new OrbStrings();
        retVal.NAME = EUIUtils.EMPTY_STRING;
        retVal.DESCRIPTION = new String[]{};
        return retVal;
    }

    public static OrbStrings getStringsForLanguage(HashMap<Settings.GameLanguage, OrbStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static OrbStrings getStringsForLanguage(HashMap<Settings.GameLanguage, OrbStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, OrbStrings> parseLanguageStrings(String languageStrings) {
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    @Override
    public PCLDynamicOrb create() {
        setTextForLanguage();
        if (imagePath == null) {
            imagePath = PCLCoreImages.CardAffinity.unknown.path();
        }
        return new PCLDynamicOrb(this);
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

    public String getEffectTextForTip() {
        return StringUtils.capitalize(EUIUtils.joinStringsMapNonnull(EUIUtils.SPLIT_LINE, move -> !PSkill.isSkillBlank(move) ? StringUtils.capitalize(move.getPowerTextForTooltip(this)) : null, moves));
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

    public OrbStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    @Override
    public void initializeImage() {
        this.imagePath = PCLCoreImages.CardAffinity.unknown.path();
    }

    @Override
    public PCLDynamicOrbData makeCopy() {
        return new PCLDynamicOrbData(this);
    }

    public PCLOrbRenderable makeRenderable() {
        return new PCLOrbRenderable(this);
    }

    public PCLOrbRenderable makeRenderableWithLevel(int level) {
        return new PCLOrbRenderable(this, new EUIKeywordTooltip(getName(), getEffectTextForPreview(level)));
    }

    public PCLDynamicOrbData setID(String fullID) {
        this.ID = fullID;
        return this;
    }

    public PCLDynamicOrbData setImage(Texture portraitImage) {
        this.portraitImage = portraitImage;

        return this;
    }

    public PCLDynamicOrbData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLDynamicOrbData setLanguageMap(HashMap<Settings.GameLanguage, OrbStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return setTextForLanguage();
    }

    public PCLDynamicOrbData setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }

    public PCLDynamicOrbData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicOrbData setText(String name, String[] descriptions) {
        this.strings.NAME = name;
        this.strings.DESCRIPTION = descriptions;

        return this;
    }

    public PCLDynamicOrbData setText(OrbStrings str) {
        return setText(str.NAME, str.DESCRIPTION);
    }

    public PCLDynamicOrbData setText(String name) {
        return setText(name, new String[0]);
    }

    public PCLDynamicOrbData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    public PCLDynamicOrbData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }

    public PCLDynamicOrbData updateTooltip() {
        tooltip = EUIKeywordTooltip.findByIDTemp(ID);
        if (tooltip == null) {
            tooltip = new EUIKeywordTooltip(strings.NAME);
        }
        tooltip.title = strings.NAME;
        if (this.strings.DESCRIPTION.length > 0) {
            tooltip.setDescription(this.strings.DESCRIPTION[0]);
        }
        else {
            tooltip.setDescription(getEffectTextForTip());
        }
        Texture tex = EUIRM.getTexture(imagePath);
        if (tex != null) {
            tooltip.setIcon(PCLRenderHelpers.generateIcon(tex));
        }
        else {
            tooltip.setIcon(PCLCoreImages.CardAffinity.unknown.texture());
        }
        EUIKeywordTooltip.registerIDTemp(ID, tooltip);
        return this;
    }
}
