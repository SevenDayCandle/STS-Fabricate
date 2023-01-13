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
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUISearchableDropdown;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

import static pinacolada.ui.cardEditor.PCLCustomCardEffectPage.*;

public class PCLCustomCardEffectEditor<T extends PSkill> extends PCLCustomCardEditorPage
{
    public static final float CUTOFF = Settings.WIDTH * 0.75f;
    public static final float MAIN_OFFSET = MENU_WIDTH * 1.58f;
    public static final float AUX_OFFSET = MENU_WIDTH * 2.43f;
    protected final PCLCustomCardEffectPage editor;
    protected ArrayList<EUIHoverable> activeElements = new ArrayList<>();
    protected EUISearchableDropdown<T> effects;
    protected EUIDropdown<PCLAffinity> affinities;
    protected EUIDropdown<PCLCardTarget> targets;
    protected EUIDropdown<AbstractCard.CardRarity> rarities;
    protected EUIDropdown<AbstractCard.CardType> types;
    protected EUIDropdown<PCLCardGroupHelper> piles;
    protected EUISearchableDropdown<PCLPowerHelper> powers;
    protected EUISearchableDropdown<PCLOrbHelper> orbs;
    protected EUISearchableDropdown<PCLStanceHelper> stances;
    protected EUISearchableDropdown<PCLCardTag> tags;
    protected EUISearchableDropdown<AbstractCard> cardData;
    protected PCLCustomCardUpgradableEditor valueEditor;
    protected PCLCustomCardUpgradableEditor extraEditor;
    protected EUIHitbox hb;
    protected PCLCustomCardEffectPage.EffectEditorGroup<T> group;
    protected int index;
    private T lastEffect;
    private float additionalHeight;

    public PCLCustomCardEffectEditor(PCLCustomCardEffectPage.EffectEditorGroup<T> group, EUIHitbox hb, int index)
    {
        this.group = group;
        this.editor = group.editor;
        this.index = index;
        this.hb = hb;
        effects = (EUISearchableDropdown<T>) new EUISearchableDropdown<T>(hb, skill -> StringUtils.capitalize(skill.getSampleText()))
                .setOnChange(effects -> {
                    if (!effects.isEmpty())
                    {
                        setEffectAt(effects.get(0));
                    }
                    else
                    {
                        setEffectAt(null);
                    }
                    editor.scheduleConstruct();
                })
                .setClearButtonOptions(true, true)
                .setCanAutosizeButton(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, getTitleForPriority())
                .setItems(getEffects())
                .setOnClear(__ -> editor.scheduleUpdate(() -> this.group.removeEffectSlot(this.index)));

        valueEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 5, MENU_HEIGHT,MAIN_OFFSET, OFFSET_AMOUNT)
                , EUIRM.strings.uiAmount, (val, upVal) -> {
            if (getEffectAt() != null)
            {
                getEffectAt().setAmount(val, upVal);
                editor.scheduleConstruct();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX);
        extraEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb,MENU_WIDTH / 5, MENU_HEIGHT, MAIN_OFFSET * 1.3f, OFFSET_AMOUNT)
                , PGR.core.strings.cardEditor.extraValue, (val, upVal) -> {
            if (getEffectAt() != null)
            {
                getEffectAt().setExtra(val, upVal);
                editor.scheduleConstruct();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX);

        targets = new EUIDropdown<PCLCardTarget>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (getEffectAt() != null && !targets.isEmpty())
                    {
                        getEffectAt().setTarget(targets.get(0));
                        editor.scheduleConstruct();
                    }
                })
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.cardTarget)
                .setCanAutosizeButton(true)
                .setItems(PCLCardTarget.getAll());
        piles = new EUIDropdown<PCLCardGroupHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setLabelFunctionForOption(c -> c.name, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.cardTarget)
                .setCanAutosizeButton(true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setItems(PCLCardGroupHelper.getAll());

        affinities = new EUIDropdown<PCLAffinity>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.getFormattedSymbolForced(editor.builder.cardColor) + " " + item.getTooltip().title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.seriesUI.affinities)
                .setCanAutosize(true, true)
                .setItems(PCLAffinity.getAvailableAffinities(editor.builder.cardColor));
        affinities.setLabelFunctionForButton((list, __) -> affinities.makeMultiSelectString(item -> item.getFormattedSymbol(editor.builder.cardColor)), null, true);

        powers = (EUISearchableDropdown<PCLPowerHelper>) new EUISearchableDropdown<PCLPowerHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.powers)
                .setCanAutosize(false, false)
                .setItems(PCLPowerHelper.sortedValues());
        powers.setLabelFunctionForButton((list, __) -> powers.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), null, true);

        orbs = (EUISearchableDropdown<PCLOrbHelper>) new EUISearchableDropdown<PCLOrbHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.orbs)
                .setCanAutosize(false, false)
                .setItems(PCLOrbHelper.visibleValues());
        orbs.setLabelFunctionForButton((list, __) -> orbs.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), null, true);

        stances = (EUISearchableDropdown<PCLStanceHelper>) new EUISearchableDropdown<PCLStanceHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForButton((items, __) -> items.size() > 0 ? items.get(0).tooltip.title : PGR.core.tooltips.neutralStance.title, null, false)
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.tooltips.stance.title)
                .setIsMultiSelect(true)
                .setCanAutosize(false, false)
                .setClearButtonOptions(true, true)
                .setItems(PCLStanceHelper.values(editor.builder.cardColor));

        tags = (EUISearchableDropdown<PCLCardTag>) new EUISearchableDropdown<PCLCardTag>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.getTip().getTitleOrIconForced() + " " + item.getTip().title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cardEditor.tags)
                .setCanAutosize(false, false)
                .setItems(PCLCardTag.getAll());

        cardData = (EUISearchableDropdown<AbstractCard>) new EUISearchableDropdown<AbstractCard>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.name, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, RunHistoryScreen.TEXT[9])
                .setCanAutosize(false, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setItems(GameUtilities.isPCLCardColor(editor.builder.cardColor) ? EUIUtils.mapAsNonnull(PCLCard.getAllData(false, true, editor.builder.cardColor), cd -> cd.makeCopy(false))
                         :
                        EUIUtils.filter(CardLibrary.getAllCards(),
                                c -> !(c instanceof PCLCard) && (c.color == AbstractCard.CardColor.COLORLESS || c.color == AbstractCard.CardColor.CURSE || c.color == editor.builder.cardColor)));

        rarities = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET + MAIN_OFFSET * 2, 0)
                , EUIGameUtils::textForRarity)
                .setLabelFunctionForOption(EUIGameUtils::textForRarity, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setCanAutosize(true, true)
                .setItems(AbstractCard.CardRarity.values());

        types = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , EUIGameUtils::textForType)
                .setLabelFunctionForOption(EUIGameUtils::textForType, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setCanAutosize(true, true)
                .setItems(AbstractCard.CardType.values());
    }

    protected T getEffectAt()
    {
        return group.lowerEffects.get(index);
    }

    protected T setEffectAt(T skill)
    {
        return group.lowerEffects.set(index, skill);
    }

    protected List<T> getEffects()
    {
        return EUIUtils.map(group.getEffects(),
                bc -> getEffectAt() != null && bc.effectID.equals(getEffectAt().effectID) ? getEffectAt() : (T) bc.scanForTips());
    }

    public float getAdditionalHeight()
    {
        return additionalHeight;
    }

    public String getTitle()
    {
        return editor.getTitle();
    }

    @Override
    public void refresh()
    {
        T curEffect = getEffectAt();
        PSkillData data = curEffect != null ? curEffect.data : null;
        int min = data != null ? data.minAmount : Integer.MIN_VALUE / 2;
        int max = data != null ? data.maxAmount : PSkill.DEFAULT_MAX;
        int eMin = data != null ? data.minExtra : Integer.MIN_VALUE / 2;
        int eMax = data != null ? data.maxExtra : PSkill.DEFAULT_MAX;

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
        piles.setItems(PSkill.getEligiblePiles(curEffect));

        float xOff = AUX_OFFSET;
        additionalHeight = 0;
        if (targets.isActive)
        {
            targets.setSelection(curEffect != null ? curEffect.target : PCLCardTarget.None, false);
            xOff = position(targets, xOff);
        }

        if (curEffect != null && lastEffect != curEffect)
        {
            lastEffect = curEffect;
            activeElements.clear();
            curEffect.fields.setupEditor(this);

            for (EUIHoverable element : activeElements)
            {
                xOff = position(element, xOff);
            }
        }
    }

    public void registerBoolean(String title, ActionT1<Boolean> onChange, boolean initial)
    {
        registerBoolean(new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, MENU_WIDTH, 0))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(title), onChange, initial);
    }
    public void registerBoolean(EUIToggle toggle, ActionT1<Boolean> onChange, boolean initial)
    {
        activeElements.add(toggle);
        toggle.setToggle(initial);
        toggle.setOnToggle(val -> {
            onChange.invoke(val);
            editor.scheduleConstruct();
        });
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, List<U> items)
    {
        activeElements.add(dropdown);
        dropdown.setOnChange(targets -> {
            onChangeImpl.invoke(targets);
            editor.scheduleConstruct();
        });
        dropdown.setSelection(items, false);
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, List<U> items)
    {
        activeElements.add(dropdown);
        dropdown.setOnChange(targets -> {
            items.clear();
            items.addAll(targets);
            editor.scheduleConstruct();
        });
        dropdown.setSelection(items, false);
    }

    public <U> void registerDropdown(List<U> possibleItems, List<U> selectedItems, FuncT1<String, U> textFunc, String title, boolean smartText)
    {
        EUIDropdown<U> dropdown = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , textFunc)
                .setLabelFunctionForOption(textFunc, smartText)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(possibleItems);
        registerDropdown(dropdown, selectedItems);
    }

    public void registerPile(List<PCLCardGroupHelper> items)
    {
        registerDropdown(piles, items);
    }

    public void registerAffinity(List<PCLAffinity> items)
    {
        registerDropdown(affinities, items);
    }

    public void registerOrb(List<PCLOrbHelper> items)
    {
        registerDropdown(orbs, items);
    }

    public void registerPower(List<PCLPowerHelper> items)
    {
        registerDropdown(powers, items);
    }

    public void registerRarity(List<AbstractCard.CardRarity> items)
    {
        registerDropdown(rarities, items);
    }

    public void registerStance(List<PCLStanceHelper> items)
    {
        registerDropdown(stances, items);
    }

    public void registerTag(List<PCLCardTag> items)
    {
        registerDropdown(tags, items);
    }

    public void registerType(List<AbstractCard.CardType> items)
    {
        registerDropdown(types, items);
    }

    public void registerCard(List<String> cardIDs)
    {
        registerDropdown(cardData,
                cards -> {
                    cardIDs.clear();
                    cardIDs.addAll(EUIUtils.mapAsNonnull(cards, t -> t.cardID));
                },
                GameUtilities.isPCLCardColor(editor.builder.cardColor) ? EUIUtils.mapAsNonnull(PCLCard.getAllData(false, true, editor.builder.cardColor), cd -> cd.makeCopy(false))
                        :
                        EUIUtils.filter(CardLibrary.getAllCards(),
                                c -> !(c instanceof PCLCard) && (c.color == AbstractCard.CardColor.COLORLESS || c.color == AbstractCard.CardColor.CURSE || c.color == editor.builder.cardColor))
        );
    }

    public PCLCardBuilder getBuilder()
    {
        return editor.builder;
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PGR.core.images.editorEffect;
    }

    protected String getTitleForPriority()
    {
        return EUIRM.strings.generic2(group.title, index + 1);
    }

    public void updateIndex(int index)
    {
        this.index = index;
        effects.setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, getTitleForPriority());
    }

    protected <U> float position(EUIHoverable element, float x)
    {
        float setX = x;
        float end = x + element.hb.width;
        if (end > CUTOFF)
        {
            additionalHeight -= MENU_HEIGHT * 1.5;
            setX = AUX_OFFSET;
            end = AUX_OFFSET + element.hb.width;
        }
        element.setOffset(setX, additionalHeight);
        element.hb.update();
        return end + scale(20);
    }

    @Override
    public void updateImpl()
    {
        this.effects.tryUpdate();
        this.targets.tryUpdate();
        this.valueEditor.tryUpdate();
        this.extraEditor.tryUpdate();
        for (EUIHoverable element : activeElements)
        {
            element.tryUpdate();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        this.effects.tryRender(sb);
        this.targets.tryRender(sb);
        this.valueEditor.tryRender(sb);
        this.extraEditor.tryRender(sb);
        for (EUIHoverable element : activeElements)
        {
            element.tryRender(sb);
        }

    }
}
