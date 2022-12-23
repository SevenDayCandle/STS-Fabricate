package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUISearchableDropdown;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.skills.special.moves.PMove_StackCustomPower;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.ui.cardEditor.PCLCustomCardEffectPage.*;

public class PCLCustomCardEffectEditor extends PCLCustomCardEditorPage
{
    public static final float MAIN_OFFSET = MENU_WIDTH * 1.58f;
    public static final float AUX_OFFSET = MENU_WIDTH * 2.43f;
    protected final PCLCustomCardEffectPage editor;
    protected PSkill.PCLEffectType effectType = PSkill.PCLEffectType.General;
    protected EUISearchableDropdown<PSkill> effects;
    protected EUIDropdown<PCLAffinity> affinities;
    protected EUIDropdown<PCLCardTarget> targets;
    protected EUIDropdown<AbstractCard.CardRarity> rarities;
    protected EUIDropdown<AbstractCard.CardType> types;
    protected EUIDropdown<PCLCardGroupHelper> piles;
    protected EUIDropdown<Integer> customPowers;
    protected EUISearchableDropdown<PCLPowerHelper> powers;
    protected EUISearchableDropdown<PCLOrbHelper> orbs;
    protected EUISearchableDropdown<PCLStanceHelper> stances;
    protected EUISearchableDropdown<PCLCardTag> tags;
    protected EUISearchableDropdown<AbstractCard> cardData;
    protected EUIToggle altToggle;
    protected PCLCustomCardUpgradableEditor valueEditor;
    protected PCLCustomCardUpgradableEditor extraEditor;
    protected EUIHitbox hb;
    protected int priority;
    protected int index;

    public PCLCustomCardEffectEditor(PCLCustomCardEffectPage editor, EUIHitbox hb, int priority, int index)
    {
        this.editor = editor;
        this.priority = priority;
        this.index = index;
        this.hb = hb;
        effects = (EUISearchableDropdown<PSkill>) new EUISearchableDropdown<PSkill>(hb, skill -> StringUtils.capitalize(skill.getSampleText()))
                .setOnChange(effects -> {
                    if (!effects.isEmpty())
                    {
                        setEffectAt(effects.get(0));
                    }
                    else
                    {
                        setEffectAt(null);
                    }
                    editor.constructEffect();
                })
                .setClearButtonOptions(true, true)
                .setCanAutosizeButton(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, getTitleForPriority())
                .setItems(getEffectsForPriority(this.priority))
                .setOnClear(__ -> editor.toRemove.add(() -> editor.removeEffectSlot(this.priority, this.index)));

        valueEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 5, MENU_HEIGHT,MAIN_OFFSET, OFFSET_AMOUNT)
                , EUIRM.strings.uiAmount, (val, upVal) -> {
            if (getEffectAt() != null)
            {
                getEffectAt().setAmount(val, upVal);
                editor.constructEffect();
            }
        })
                .setLimits(-999, 999);
        extraEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb,MENU_WIDTH / 5, MENU_HEIGHT, MAIN_OFFSET * 1.3f, OFFSET_AMOUNT)
                , PGR.core.strings.cardEditor.extraValue, (val, upVal) -> {
            if (getEffectAt() != null)
            {
                getEffectAt().setExtra(val, upVal);
                editor.constructEffect();
            }
        })
                .setLimits(-999, 999);

        targets = new EUIDropdown<PCLCardTarget>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (getEffectAt() != null && !targets.isEmpty())
                    {
                        getEffectAt().setTarget(targets.get(0));
                        editor.constructEffect();
                    }
                })
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.cardTarget)
                .setCanAutosizeButton(true)
                .setItems(PCLCardTarget.getAll());
        piles = new EUIDropdown<PCLCardGroupHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    getEffectAt().setCardGroup(targets);
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(c -> c.name, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.cardTarget)
                .setCanAutosizeButton(true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setItems(PCLCardGroupHelper.getAll());

        affinities = new EUIDropdown<PCLAffinity>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0))
                .setOnChange(types -> {
                    getEffectAt().setAffinity(types);
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(item -> item.getFormattedSymbolForced(editor.builder.cardColor) + " " + item.getTooltip().title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.seriesUI.affinities)
                .setCanAutosize(true, true)
                .setItems(PCLAffinity.getAvailableAffinities(editor.builder.cardColor));
        affinities.setLabelFunctionForButton((list, __) -> affinities.makeMultiSelectString(item -> item.getFormattedSymbol(editor.builder.cardColor)), null, true);

        powers = (EUISearchableDropdown<PCLPowerHelper>) new EUISearchableDropdown<PCLPowerHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setOnChange(types -> {
                    getEffectAt().setPower(types);
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.powers)
                .setCanAutosize(false, false)
                .setItems(PCLPowerHelper.sortedCommons());
        powers.setLabelFunctionForButton((list, __) -> powers.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), null, true);

        orbs = (EUISearchableDropdown<PCLOrbHelper>) new EUISearchableDropdown<PCLOrbHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setOnChange(types -> {
                    getEffectAt().setOrb(types);
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.orbs)
                .setCanAutosize(false, false)
                .setItems(PCLOrbHelper.visibleValues());
        orbs.setLabelFunctionForButton((list, __) -> orbs.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), null, true);

        stances = (EUISearchableDropdown<PCLStanceHelper>) new EUISearchableDropdown<PCLStanceHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setOnChange(types -> {
                    getEffectAt().setStance(types);
                    editor.constructEffect();
                })
                .setLabelFunctionForButton((items, __) -> items.size() > 0 ? items.get(0).tooltip.title : PGR.core.tooltips.neutralStance.title, null, false)
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.tooltips.stance.title)
                .setIsMultiSelect(true)
                .setCanAutosize(false, false)
                .setClearButtonOptions(true, true)
                .setItems(PCLStanceHelper.values(editor.builder.cardColor));

        tags = (EUISearchableDropdown<PCLCardTag>) new EUISearchableDropdown<PCLCardTag>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setOnChange(types -> {
                    getEffectAt().setTag(types);
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(item -> item.getTip().getTitleOrIconForced() + " " + item.getTip().title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.tags)
                .setCanAutosize(false, false)
                .setItems(PCLCardTag.getAll());

        cardData = (EUISearchableDropdown<AbstractCard>) new EUISearchableDropdown<AbstractCard>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setOnChange(types -> {
                    getEffectAt().setCardIDs(EUIUtils.mapAsNonnull(types, t -> t.cardID));
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(item -> item.name, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, RunHistoryScreen.TEXT[9])
                .setCanAutosize(false, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setItems(GameUtilities.isPCLCardColor(editor.builder.cardColor) ? EUIUtils.mapAsNonnull(PCLCard.getAllData(false, true, editor.builder.cardColor), cd -> cd.makeCopy(false))
                         :
                        EUIUtils.filter(CardLibrary.getAllCards(),
                                c -> !(c instanceof PCLCard) && (c.color == AbstractCard.CardColor.COLORLESS || c.color == AbstractCard.CardColor.CURSE || c.color == editor.builder.cardColor)));


        customPowers = new EUIDropdown<Integer>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, hb.y))
                .setOnChange(types -> {
                    if (getEffectAt() instanceof PMove_StackCustomPower)
                    {
                        ((PMove_StackCustomPower) getEffectAt()).setIndexes(types);
                        editor.constructEffect();
                    }
                })
                .setLabelFunctionForOption(item -> EUIUtils.format(PGR.core.strings.cardEditor.powerX, item + 1), false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.powers)
                .setCanAutosize(true, true)
                .setItems(EUIUtils.range(0, 1));

        rarities = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET + MAIN_OFFSET * 2, 0)
                , EUIGameUtils::textForRarity)
                .setOnChange(types -> {
                    getEffectAt().setCardRarities(types);
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(EUIGameUtils::textForRarity, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setCanAutosize(true, true)
                .setItems(AbstractCard.CardRarity.values());

        types = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , EUIGameUtils::textForType)
                .setOnChange(types -> {
                    getEffectAt().setCardTypes(types);
                    editor.constructEffect();
                })
                .setLabelFunctionForOption(EUIGameUtils::textForType, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setCanAutosize(true, true)
                .setItems(AbstractCard.CardType.values());

        altToggle = new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, MENU_WIDTH * 5.8f, 0))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.strings.cardEditor.not)
                .setOnToggle(val -> {
                    getEffectAt().setAlt(val);
                    editor.constructEffect();
                });
    }

    protected PSkill getEffectAt()
    {
        switch (priority)
        {
            case PCond.CONDITION_PRIORITY:
                return editor.lowerConditions.get(index);
            case PMod.MODIFIER_PRIORITY:
                return editor.lowerModifiers.get(index);
            default:
                return editor.lowerEffects.get(index);
        }
    }

    protected PSkill setEffectAt(PSkill skill)
    {
        switch (priority)
        {
            case PCond.CONDITION_PRIORITY:
                return editor.lowerConditions.set(index, (PCond) skill);
            case PMod.MODIFIER_PRIORITY:
                return editor.lowerModifiers.set(index, (PMod) skill);
            default:
                return editor.lowerEffects.set(index, skill);
        }
    }

    protected List<PSkill> getEffectsForPriority(int priority)
    {
        switch (priority)
        {
            case PCond.CONDITION_PRIORITY:
                return EUIUtils.map(PCond.getEligibleConditions(editor.builder.cardColor, PCond.CONDITION_PRIORITY),
                        bc -> getEffectAt() != null && bc.effectID.equals(getEffectAt().effectID) ? getEffectAt() : bc.scanForTips());
            case PMod.MODIFIER_PRIORITY:
                return EUIUtils.map(PMod.getEligibleModifiers(editor.builder.cardColor, PMod.MODIFIER_PRIORITY),
                        bc -> getEffectAt() != null && bc.effectID.equals(getEffectAt().effectID) ? getEffectAt() : bc.scanForTips());
            default:
                return EUIUtils.map(PSkill.getEligibleEffects(editor.builder.cardColor, PSkill.DEFAULT_PRIORITY), bc -> getEffectAt() != null && bc.effectID.equals(getEffectAt().effectID) ? getEffectAt() : bc.scanForTips());
        }
    }

    public String getTitle()
    {
        return editor.getTitle();
    }

    @Override
    public void refresh()
    {
        PSkill curEffect = getEffectAt();
        PSkillData data = PSkill.getData(curEffect);
        effectType = data != null ? data.effectType : PSkill.PCLEffectType.General;
        int min = data != null ? data.minAmount : 0;
        int max = data != null ? data.maxAmount : 0;
        int eMin = data != null ? data.minExtra : 0;
        int eMax = data != null ? data.maxExtra : 0;

        effects.setSelection(curEffect, false);
        valueEditor
                .setLimits(min, max)
                .setValue(curEffect != null ? curEffect.amount : 0, curEffect != null ? curEffect.getUpgrade() : 0, false)
                .setActive(min != max);
        extraEditor
                .setLimits(eMin, eMax)
                .setValue(curEffect != null ? curEffect.extra : 0, curEffect != null ? curEffect.getUpgradeExtra() : 0, false)
                .setActive(eMin != eMax);
        targets
                .setItems(PSkill.getEligibleTargets(curEffect))
                .setActive(targets.getAllItems().size() > 1);
        piles.setItems(PSkill.getEligiblePiles(curEffect)).setActive(false);
        affinities.setActive(false);
        cardData.setActive(false);
        customPowers.setActive(false);
        orbs.setActive(false);
        powers.setActive(false);
        stances.setActive(false);
        tags.setActive(false);
        rarities.setActive(false);
        types.setActive(false);

        float xOff = AUX_OFFSET;
        if (targets.isActive)
        {
            xOff = position(targets, xOff, curEffect != null ? curEffect.target : PCLCardTarget.None);
        }

        switch (effectType)
        {
            case CardGroupFull:
                xOff = position(piles, xOff, curEffect.getGroups());
                xOff = position(types, xOff, curEffect.getCardTypes());
                xOff = position(rarities, xOff, curEffect.getCardRarities());
                if (GameUtilities.isPCLCardColor(editor.builder.cardColor))
                {
                    xOff = position(affinities, xOff, curEffect.getAffinities());
                }
                break;
            case CardGroupAffinity:
                xOff = position(piles, xOff, curEffect.getGroups());
            case Affinity:
                if (GameUtilities.isPCLCardColor(editor.builder.cardColor))
                {
                    xOff = position(affinities, xOff, curEffect.getAffinities());
                }
                break;
            case Card:
                xOff = position(piles, xOff, curEffect.getGroups());
                xOff = position(cardData, xOff, curEffect.getCards(), c -> c.cardID);
                break;
            case CustomPower:
                if (curEffect instanceof PMove_StackCustomPower)
                {
                    xOff = position(customPowers, xOff, ((PMove_StackCustomPower) curEffect).getIndexes());
                }
                break;
            case Delegate:
                xOff = position(types, xOff, curEffect.getCardTypes());
                xOff = position(rarities, xOff, curEffect.getCardRarities());
                if (GameUtilities.isPCLCardColor(editor.builder.cardColor))
                {
                    xOff = position(affinities, xOff, curEffect.getAffinities());
                }
                break;
            case Orb:
                xOff = position(orbs, xOff, curEffect.getOrbs());
                break;
            case Power:
                xOff = position(powers, xOff, curEffect.getPowers());
                break;
            case Stance:
                xOff = position(stances, xOff, curEffect.getStances());
                break;
            case Tag:
                xOff = position(piles, xOff, curEffect.getGroups());
                xOff = position(tags, xOff, curEffect.getTags());
                break;
            case CardGroupCardType:
                xOff = position(piles, xOff, curEffect.getGroups());
                xOff = position(types, xOff, curEffect.getCardTypes());
                break;
            case CardGroup:
                xOff = position(piles, xOff, curEffect.getGroups());
                break;
        }

        altToggle.setToggle(curEffect != null && curEffect.alt)
                .setPosition(Math.max(xOff, extraEditor.hb.cX + extraEditor.hb.width) + scale(70), hb.cY)
                .setActive(data != null && data.altText != null);
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PGR.core.images.editorEffect;
    }

    protected String getTitleForPriority()
    {
        switch (priority)
        {
            case PCond.CONDITION_PRIORITY:
                return EUIRM.strings.generic2(PGR.core.strings.cardEditor.condition, index + 1);
            case PMod.MODIFIER_PRIORITY:
                return EUIRM.strings.generic2(PGR.core.strings.cardEditor.modifier, index + 1);
            default:
                return EUIRM.strings.generic2(PGR.core.strings.cardEditor.effect, index + 1);
        }
    }

    protected <T> float position(EUIDropdown<T> element, float x, T items)
    {
        element.setSelection(items, false).setOffsetX(x).setActive(true);
        element.hb.update();
        return x + element.hb.width + scale(20);
    }

    protected <T> float position(EUIDropdown<T> element, float x, List<T> items)
    {
        element.setSelection(items, false).setOffsetX(x).setActive(true);
        element.hb.update();
        return x + element.hb.width + scale(20);
    }

    protected <T, U> float position(EUIDropdown<T> element, float x, List<U> items, FuncT1<U, T> convertFunc)
    {
        element.setSelection(items, convertFunc, false).setOffsetX(x).setActive(true);
        element.hb.update();
        return x + element.hb.width + scale(20);
    }

    @Override
    public void updateImpl()
    {
        this.effects.tryUpdate();
        this.targets.tryUpdate();
        this.piles.tryUpdate();
        this.affinities.tryUpdate();
        this.powers.tryUpdate();
        this.orbs.tryUpdate();
        this.stances.tryUpdate();
        this.tags.tryUpdate();
        this.cardData.tryUpdate();
        this.valueEditor.tryUpdate();
        this.extraEditor.tryUpdate();
        this.customPowers.tryUpdate();
        this.rarities.tryUpdate();
        this.types.tryUpdate();
        this.altToggle.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        this.effects.tryRender(sb);
        this.targets.tryRender(sb);
        this.piles.tryRender(sb);
        this.affinities.tryRender(sb);
        this.powers.tryRender(sb);
        this.orbs.tryRender(sb);
        this.stances.tryRender(sb);
        this.tags.tryRender(sb);
        this.cardData.tryRender(sb);
        this.valueEditor.tryRender(sb);
        this.extraEditor.tryRender(sb);
        this.customPowers.tryRender(sb);
        this.rarities.tryRender(sb);
        this.types.tryRender(sb);
        this.altToggle.tryRender(sb);
    }
}
