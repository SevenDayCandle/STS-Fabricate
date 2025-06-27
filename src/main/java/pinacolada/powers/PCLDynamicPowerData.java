package pinacolada.powers;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.providers.ValueProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.moves.PMove_Draw;
import pinacolada.ui.PCLPowerRenderable;
import pinacolada.utilities.PCLRenderHelpers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

@JsonAdapter(PCLPowerData.PCLPowerDataAdapter.class)
public class PCLDynamicPowerData extends PCLPowerData implements EditorMaker<PCLDynamicPower, PowerStrings>, ValueProvider {
    private static final TypeToken<HashMap<Settings.GameLanguage, PowerStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, PowerStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, PowerStrings> languageMap = new HashMap<>();
    public final ArrayList<PSkill<?>> moves = new ArrayList<>();
    public final ArrayList<PSkill<?>> powers = new ArrayList<>();
    public Texture portraitImage;

    public PCLDynamicPowerData(String cardID) {
        this(PGR.core, cardID);
    }

    public PCLDynamicPowerData(PCLResources<?, ?, ?, ?> resources, String cardID) {
        super(PCLDynamicPower.class, resources, cardID);
    }

    public PCLDynamicPowerData(PCLResources<?, ?, ?, ?> resources, String cardID, PowerStrings strings) {
        super(PCLDynamicPower.class, resources, cardID, strings);
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
        setTurns(original.turns);
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
        setTurns(original.turns);
        setType(original.type);
        setImage(original.portraitImage);
        setLanguageMap(original.languageMap);
        setPSkill(original.moves, true, true);
        setPPower(original.powers, true, true);
    }

    public PCLDynamicPowerData(PCLCustomPowerSlot data, PCLCustomEditorLoadable.EffectItemForm f) {
        this(data.ID);
        safeLoadValue(() -> setEndTurnBehavior(Behavior.valueOf(data.endTurnBehavior)));
        safeLoadValue(() -> setType(AbstractPower.PowerType.valueOf(data.type)));
        safeLoadValue(() -> setIsCommon(data.isCommon));
        safeLoadValue(() -> setIsMetascaling(data.isMetascaling));
        safeLoadValue(() -> setIsPostActionPower(data.isPostActionPower));
        safeLoadValue(() -> setLimits(data.minValue, data.maxValue));
        safeLoadValue(() -> setPriority(data.priority));
        safeLoadValue(() -> setTurns(data.turns));
        safeLoadValue(() -> parseLanguageStrings(data.languageStrings, f));
        safeLoadValue(() -> setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true));
        safeLoadValue(() -> setPPower(EUIUtils.mapAsNonnull(f.powerEffects, PSkill::get), true, true));
    }

    protected static PowerStrings getInitialStrings() {
        PowerStrings retVal = new PowerStrings();
        retVal.NAME = EUIUtils.EMPTY_STRING;
        retVal.DESCRIPTIONS = new String[]{};
        return retVal;
    }

    public static PowerStrings getStringsForLanguage(HashMap<Settings.GameLanguage, PowerStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static PowerStrings getStringsForLanguage(HashMap<Settings.GameLanguage, PowerStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        !languageMap.isEmpty() ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
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

    @Override
    public String[] getDescString(PowerStrings item) {
        return item.DESCRIPTIONS;
    }

    @Override
    public PowerStrings getDefaultStrings() {
        return getInitialStrings();
    }

    public String getEffectTextForPreview(int level) {
        String[] desc = strings.DESCRIPTIONS;
        final StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);
        for (int i = 0; i < moves.size(); i++) {
            PSkill<?> move = moves.get(i);
            if (!PSkill.isSkillBlank(move)) {
                move.recurse(m -> {
                    m.amount = m.baseAmount + level * m.getUpgrade();
                    m.extra = m.baseExtra + level * m.getUpgradeExtra();
                });
                String pText = desc != null && desc.length > i && !StringUtils.isEmpty(desc[i]) ? move.getUncascadedPowerOverride(desc[i], level) : move.getPowerTextForDisplay(this);
                if (!StringUtils.isEmpty(pText)) {
                    sj.add(StringUtils.capitalize(pText));
                }
                move.recurse(m -> {
                    m.amount = m.baseAmount;
                    m.extra = m.baseExtra;
                });
            }
        }
        return sj.toString();
    }

    public String getEffectTextForTip() {
        String[] desc = strings.DESCRIPTIONS;
        final StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);
        for (int i = 0; i < moves.size(); i++) {
            PSkill<?> skill = moves.get(i);
            Object oldSource = skill.source;
            skill.setSource(this);
            if (!PSkill.isSkillBlank(skill)) {
                skill.recurse(m -> {
                    m.amount = m.baseAmount + m.getUpgrade();
                    m.extra = m.baseExtra + m.getUpgradeExtra();
                });
                String s = desc != null && desc.length > i && !StringUtils.isEmpty(desc[i]) ? skill.getUncascadedPowerOverride(desc[i], null) : StringUtils.capitalize(skill.getPowerTextForTooltip(this));
                if (!StringUtils.isEmpty(s)) {
                    sj.add(s);
                }
                skill.recurse(m -> {
                    m.amount = m.baseAmount;
                    m.extra = m.baseExtra;
                });
            }
            skill.setSource(oldSource);
        }
        String base = StringUtils.capitalize(sj.toString());
        String add = endTurnBehavior.getAddendum(turns);
        return add != null ? base + " " + add : base;
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
    public PowerStrings getStrings() {
        return strings;
    }

    @Override
    public HashMap<Settings.GameLanguage, PowerStrings> getLanguageMap() {
        return languageMap;
    }

    @Override
    public Type typeToken() {
        return TStrings.getType();
    }

    @Override
    public void initializeImage() {
        this.imagePath = PCLCoreImages.CardAffinity.unknown.path();
    }

    @Override
    public PCLDynamicPowerData makeCopy() {
        return new PCLDynamicPowerData(this);
    }

    public PCLPowerRenderable makeRenderableWithLevel(int level) {
        return new PCLPowerRenderable(this, new EUIKeywordTooltip(getName(), getEffectTextForPreview(level)));
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

    public PCLDynamicPowerData setName(String name) {
        this.strings.NAME = name;

        return this;
    }

    public PCLDynamicPowerData setText(String name, String[] descriptions) {
        this.strings.NAME = name;
        this.strings.DESCRIPTIONS = descriptions;

        return this;
    }

    @Override
    public PCLDynamicPowerData setText(PowerStrings PotionStrings) {
        return setText(PotionStrings.NAME, PotionStrings.DESCRIPTIONS);
    }

    public PCLDynamicPowerData setText(String name) {
        return setText(name, new String[0]);
    }

    @Override
    public PCLDynamicPowerData setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    @Override
    public PCLDynamicPowerData setTextForLanguage(Settings.GameLanguage language) {
        return setText(getStringsForLanguage(language));
    }

    // Used for power keyword tooltips
    @Override
    public int timesUpgraded() {
        return 1;
    }

    @Override
    public PowerStrings copyStrings(PowerStrings initial) {
        return copyStrings(initial, new PowerStrings());
    }

    @Override
    public PowerStrings copyStrings(PowerStrings initial, PowerStrings dest) {
        dest.NAME = initial.NAME;
        if (initial.DESCRIPTIONS != null) {
            dest.DESCRIPTIONS = initial.DESCRIPTIONS.clone();
        }
        else {
            dest.DESCRIPTIONS = null;
        }
        return dest;
    }

    public PCLDynamicPowerData updateTooltip() {
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
