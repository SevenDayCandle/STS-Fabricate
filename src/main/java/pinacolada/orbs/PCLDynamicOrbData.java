package pinacolada.orbs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.localization.OrbStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

public class PCLDynamicOrbData extends PCLOrbData implements EditorMaker<PCLDynamicOrb, OrbStrings> {
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
        setBaseEvoke(original.baseEvokeValue, original.baseEvokeValueUpgrade);
        setBasePassive(original.basePassiveValue, original.basePassiveValueUpgrade);
        setFlareColor1(original.flareColor1);
        setFlareColor2(original.flareColor2);
        setMaxUpgrades(original.maxUpgradeLevel);
        setRotationSpeed(original.rotationSpeed);
        setSfx(original.sfx);
        setTiming(original.timing);
    }

    public PCLDynamicOrbData(PCLDynamicOrbData original) {
        this(original.resources, original.ID);
        setImagePath(original.imagePath);
        setApplyFocusToEvoke(original.applyFocusToEvoke);
        setApplyFocusToPassive(original.applyFocusToPassive);
        setBaseEvoke(original.baseEvokeValue, original.baseEvokeValueUpgrade);
        setBasePassive(original.basePassiveValue, original.basePassiveValueUpgrade);
        setFlareColor1(original.flareColor1);
        setFlareColor2(original.flareColor2);
        setMaxUpgrades(original.maxUpgradeLevel);
        setRotationSpeed(original.rotationSpeed);
        setSfx(original.sfx);
        setTiming(original.timing);
        setImage(original.portraitImage);
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
    }

    public PCLDynamicOrbData(PCLCustomOrbSlot data, PCLCustomOrbSlot.OrbForm form) {
        this(data.ID);
        safeLoadValue(() -> setApplyFocusToEvoke(form.applyFocusToEvoke));
        safeLoadValue(() -> setApplyFocusToPassive(form.applyFocusToPassive));
        safeLoadValue(() -> setBaseEvoke(data.baseEvokeValue, data.baseEvokeValueUpgrade));
        safeLoadValue(() -> setBasePassive(data.basePassiveValue, data.basePassiveValueUpgrade));
        safeLoadValue(() -> setFlareColor1(Color.valueOf(data.flareColor1)));
        safeLoadValue(() -> setFlareColor2(Color.valueOf(data.flareColor2)));
        safeLoadValue(() -> setMaxUpgrades(data.maxUpgrades));
        safeLoadValue(() -> setRotationSpeed(data.rotationSpeed));
        safeLoadValue(() -> setSfx(data.sfx));
        safeLoadValue(() -> setTiming(DelayTiming.valueOf(form.timing)));
        safeLoadValue(() -> languageMap.putAll(parseLanguageStrings(data.languageStrings)));
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
                        !languageMap.isEmpty() ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
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

    @Override
    public String[] getDescString(OrbStrings item) {
        return item.DESCRIPTION;
    }

    @Override
    public OrbStrings getDefaultStrings() {
        return getInitialStrings();
    }

    public String getEffectTextForPreview(int level) {
        String[] desc = strings.DESCRIPTION;
        final StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);
        for (int i = 0; i < moves.size(); i++) {
            PSkill<?> move = moves.get(i);
            if (!PSkill.isSkillBlank(move)) {
                move.recurse(m -> m.setTemporaryAmount(m.baseAmount + level * m.getUpgrade()));
                String pText = desc != null && desc.length > i && !StringUtils.isEmpty(desc[i]) ? move.getUncascadedPowerOverride(desc[i]) : move.getPowerTextForDisplay(this);
                if (!StringUtils.isEmpty(pText)) {
                    sj.add(StringUtils.capitalize(pText));
                }
                move.recurse(m -> m.setTemporaryAmount(m.baseAmount));
            }
        }
        return sj.toString();
    }

    public String getEffectTextForTip() {
        String[] desc = strings.DESCRIPTION;
        final StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);
        for (int i = 0; i < moves.size(); i++) {
            PSkill<?> skill = moves.get(i);
            if (!PSkill.isSkillBlank(skill)) {
                String s = desc != null && desc.length > i && !StringUtils.isEmpty(desc[i]) ? skill.getUncascadedPowerOverride(desc[i]) : StringUtils.capitalize(skill.getPowerTextForTooltip(this));
                if (s != null) {
                    sj.add(s);
                }
            }
        }
        return StringUtils.capitalize(sj.toString());
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

    @Override
    public OrbStrings getStrings() {
        return strings;
    }

    @Override
    public HashMap<Settings.GameLanguage, OrbStrings> getLanguageMap() {
        return languageMap;
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

    public PCLDynamicOrbData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicOrbData setText(String name, String[] descriptions) {
        this.strings.NAME = name;
        this.strings.DESCRIPTION = descriptions;

        return this;
    }

    @Override
    public PCLDynamicOrbData setText(OrbStrings str) {
        return setText(str.NAME, str.DESCRIPTION);
    }

    public PCLDynamicOrbData setText(String name) {
        return setText(name, new String[0]);
    }

    @Override
    public PCLDynamicOrbData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    @Override
    public PCLDynamicOrbData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }

    @Override
    public OrbStrings copyStrings(OrbStrings initial) {
        return copyStrings(initial, new OrbStrings());
    }

    @Override
    public OrbStrings copyStrings(OrbStrings initial, OrbStrings dest) {
        dest.NAME = initial.NAME;
        if (initial.DESCRIPTION != null) {
            dest.DESCRIPTION = initial.DESCRIPTION.clone();
        }
        else {
            dest.DESCRIPTION = null;
        }
        return dest;
    }

    public PCLDynamicOrbData updateTooltip() {
        setTextForLanguage();
        tooltip = EUIKeywordTooltip.findByIDTemp(ID);
        if (tooltip == null) {
            tooltip = new EUIKeywordTooltip(strings.NAME);
        }
        tooltip.title = strings.NAME;
        tooltip.setDescription(getEffectTextForTip());
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
