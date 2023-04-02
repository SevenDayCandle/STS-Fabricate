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
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.CostFilter;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.misc.PCLDungeon;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

import static pinacolada.ui.cardEditor.PCLCustomCardEffectPage.*;

public class PCLCustomCardEffectEditor<T extends PSkill<?>> extends PCLCustomCardEditorPage
{
    protected static ArrayList<AbstractCard> availableCards;
    public static final float CUTOFF = Settings.WIDTH * 0.7f;
    public static final float MAIN_OFFSET = MENU_WIDTH * 1.58f;
    public static final float AUX_OFFSET = MENU_WIDTH * 2.43f;
    protected final PCLCustomCardEffectPage editor;
    protected ArrayList<EUIHoverable> activeElements = new ArrayList<>();
    protected EUISearchableDropdown<T> effects;
    protected EUIDropdown<PCLAffinity> affinities;
    protected EUIDropdown<PCLCardTarget> targets;
    protected EUIDropdown<AbstractCard.CardColor> colors;
    protected EUIDropdown<AbstractCard.CardRarity> rarities;
    protected EUIDropdown<AbstractCard.CardType> types;
    protected EUIDropdown<CostFilter> costs;
    protected EUIDropdown<PCLCardGroupHelper> piles;
    protected EUIDropdown<PCLCardSelection> origins;
    protected EUISearchableDropdown<PCLPowerHelper> powers;
    protected EUISearchableDropdown<PCLOrbHelper> orbs;
    protected EUISearchableDropdown<PCLStanceHelper> stances;
    protected EUISearchableDropdown<PCLCardTag> tags;
    protected EUISearchableDropdown<AbstractCard> cardData;
    protected PCLCustomCardUpgradableEditor valueEditor;
    protected PCLCustomCardUpgradableEditor extraEditor;
    protected EUIHitbox hb;
    protected EffectEditorGroup<T> group;
    protected int index;
    private T lastEffect;
    private float additionalHeight;

    public PCLCustomCardEffectEditor(EffectEditorGroup<T> group, EUIHitbox hb, int index)
    {
        this.group = group;
        this.editor = group.editor;
        this.index = index;
        this.hb = hb;
        final AbstractCard.CardColor cardColor = getColor();
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
                , PGR.core.strings.cedit_extraValue, (val, upVal) -> {
            if (getEffectAt() != null)
            {
                getEffectAt().setExtra(val, upVal);
                editor.scheduleConstruct();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX);

        targets = new EUIDropdown<PCLCardTarget>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , PCLCardTarget::getTitle)
                .setOnChange(targets -> {
                    if (getEffectAt() != null && !targets.isEmpty())
                    {
                        getEffectAt().setTarget(targets.get(0));
                        editor.scheduleConstruct();
                    }
                })
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_cardTarget)
                .setCanAutosize(true, true)
                .setItems(PCLCustomCardPrimaryInfoPage.getEligibleTargets(cardColor));
        piles = new EUIDropdown<PCLCardGroupHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , PCLCardGroupHelper::getCapitalTitle)
                .setLabelFunctionForOption(PCLCardGroupHelper::getCapitalTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_pile)
                .setCanAutosize(true, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setItems(PCLCardGroupHelper.getStandard());
        origins = new EUIDropdown<PCLCardSelection>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , PCLCardSelection::getTitle)
                .setLabelFunctionForOption(PCLCardSelection::getTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_origins)
                .setCanAutosizeButton(true)
                .setShouldPositionClearAtTop(true)
                .setItems(PCLCardSelection.values());

        affinities = new EUIDropdown<PCLAffinity>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.getFormattedSymbolForced(cardColor) + " " + item.getTooltip().title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.sui_affinities)
                .setCanAutosize(true, true)
                .setItems(PCLAffinity.getAvailableAffinities(cardColor, PGR.config.showIrrelevantProperties.get()));
        affinities.setLabelFunctionForButton((list, __) -> affinities.makeMultiSelectString(item -> item.getFormattedSymbol(cardColor)), null, true);

        powers = (EUISearchableDropdown<PCLPowerHelper>) new EUISearchableDropdown<PCLPowerHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_powers)
                .setCanAutosize(true, true)
                .setItems(PCLPowerHelper.sortedValues());
        powers.setLabelFunctionForButton((list, __) -> powers.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), null, true);

        orbs = (EUISearchableDropdown<PCLOrbHelper>) new EUISearchableDropdown<PCLOrbHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_orbs)
                .setCanAutosize(true, true)
                .setItems(PCLOrbHelper.visibleValues());
        orbs.setLabelFunctionForButton((list, __) -> orbs.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), null, true);

        stances = (EUISearchableDropdown<PCLStanceHelper>) new EUISearchableDropdown<PCLStanceHelper>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForButton((items, __) -> items.size() > 0 ? items.get(0).tooltip.title : PGR.core.tooltips.neutralStance.title, null, false)
                .setLabelFunctionForOption(item -> item.tooltip.getTitleOrIconForced() + " " + item.tooltip.title, true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.tooltips.stance.title)
                .setIsMultiSelect(true)
                .setCanAutosize(true, true)
                .setClearButtonOptions(true, true)
                .setItems(PCLStanceHelper.values(cardColor));

        tags = (EUISearchableDropdown<PCLCardTag>) new EUISearchableDropdown<PCLCardTag>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.getTip().getTitleOrIconForced() + " " + item.getTip().title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_tags)
                .setCanAutosize(true, true)
                .setItems(PCLCardTag.getAll());

        cardData = (EUISearchableDropdown<AbstractCard>) new EUISearchableDropdown<AbstractCard>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.2f, MENU_HEIGHT, AUX_OFFSET, 0))
                .setLabelFunctionForOption(item -> item.name, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, RunHistoryScreen.TEXT[9])
                .setCanAutosize(true, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setItems(getAvailableCards());

        colors = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET + MAIN_OFFSET * 2, 0)
                , EUIGameUtils::getColorName)
                .setLabelFunctionForOption(EUIGameUtils::getColorName, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.uiColors)
                .setCanAutosize(true, true)
                .setItems(AbstractCard.CardColor.values());

        rarities = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET + MAIN_OFFSET * 2, 0)
                , EUIGameUtils::textForRarity)
                .setLabelFunctionForOption(EUIGameUtils::textForRarity, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setCanAutosize(true, true)
                .setItems(GameUtilities.getStandardRarities());

        types = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , EUIGameUtils::textForType)
                .setLabelFunctionForOption(EUIGameUtils::textForType, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setCanAutosize(true, true)
                .setItems(PCLCustomCardPrimaryInfoPage.getEligibleTypes(cardColor));

        costs = new EUIDropdown<CostFilter>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , c -> c.name)
                .setLabelFunctionForOption(c -> c.name, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[3])
                .setCanAutosize(true, true)
                .setItems(CostFilter.values());
    }

    public PCLDynamicData getBuilder()
    {
        return editor.screen.getBuilder();
    }

    protected AbstractCard.CardColor getColor()
    {
        return getBuilder().cardColor;
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
        return EUIUtils.map(group.listFunc.invoke(),
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
        PSkillData<?> data = curEffect != null ? curEffect.data : null;
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
        if (curEffect != null && lastEffect != curEffect)
        {
            lastEffect = curEffect;
            activeElements.clear();
            targets
                    .setItems(PSkill.getEligibleTargets(curEffect))
                    .setActive(targets.getAllItems().size() > 1);
            piles.setItems(PSkill.getEligiblePiles(curEffect))
                    .setActive(piles.getAllItems().size() > 1);
            origins.setItems(PSkill.getEligibleOrigins(curEffect))
                    .setActive(origins.getAllItems().size() > 1);
            curEffect.fields.setupEditor(this);

            float xOff = AUX_OFFSET;
            additionalHeight = 0;
            if (targets.isActive)
            {
                targets.setSelection(curEffect.target, false);
                xOff = position(targets, xOff);
            }
            for (EUIHoverable element : activeElements)
            {
                xOff = position(element, xOff);
            }
        }
    }

    public void registerBoolean(String title, ActionT1<Boolean> onChange, boolean initial)
    {
        registerBoolean(title, null, onChange, initial);
    }

    public void registerBoolean(String title, String desc, ActionT1<Boolean> onChange, boolean initial)
    {
        registerBoolean((EUIToggle) new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.62f, MENU_HEIGHT, MENU_WIDTH, 0))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(title)
                .setTooltip(desc != null ? new EUITooltip(title, desc) : null)
                , onChange, initial);
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

    public <U, V> void registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, List<V> items, FuncT1<V, U> convertFunc)
    {
        if (dropdown.size() > 0)
        {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.scheduleConstruct();
            });
            dropdown.setSelection(items, convertFunc, false);
        }
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, List<U> items)
    {
        if (dropdown.size() > 0)
        {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.scheduleConstruct();
            });
            dropdown.setSelection(items, false);
        }
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, U item)
    {
        if (dropdown.size() > 0)
        {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.scheduleConstruct();
            });
            dropdown.setSelection(item, false);
        }
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, List<U> items)
    {
        if (dropdown.size() > 0)
        {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                items.clear();
                items.addAll(targets);
                editor.scheduleConstruct();
            });
            dropdown.setSelection(items, false);
        }
    }

    public <U> void registerDropdown(List<U> possibleItems, List<U> selectedItems, FuncT1<String, U> textFunc, String title, boolean smartText)
    {
        registerDropdown(possibleItems, selectedItems, textFunc, title, smartText, true, true);
    }

    public <U> void registerDropdown(List<U> possibleItems, List<U> selectedItems, FuncT1<String, U> textFunc, String title, boolean smartText, boolean multiSelect, boolean positionClearAtTop)
    {
        EUIDropdown<U> dropdown = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , textFunc)
                .setLabelFunctionForOption(textFunc, smartText)
                .setIsMultiSelect(multiSelect)
                .setShouldPositionClearAtTop(positionClearAtTop)
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

    public void registerColor(List<AbstractCard.CardColor> items)
    {
        registerDropdown(colors, items);
    }

    public void registerOrb(List<PCLOrbHelper> items)
    {
        registerDropdown(orbs, items);
    }

    public void registerOrigin(PCLCardSelection item, ActionT1<List<PCLCardSelection>> onChangeImpl)
    {
        registerDropdown(origins, onChangeImpl, item);
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

    public void registerCost(List<CostFilter> items)
    {
        registerDropdown(costs, items);
    }

    public void registerCard(List<String> cardIDs)
    {
        registerDropdown(cardData,
                cards -> {
                    cardIDs.clear();
                    cardIDs.addAll(EUIUtils.mapAsNonnull(cards, t -> t.cardID));
                },
                cardIDs,
                card -> card.cardID
        );
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PCLCoreImages.Menu.editorEffect;
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
        // Don't shift the positions if the element is not visible
        if (!element.isActive)
        {
            return x;
        }

        float setX = x;
        float end = x + element.hb.width;
        if (end > CUTOFF)
        {
            additionalHeight -= MENU_HEIGHT * 2f;
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

    protected ArrayList<AbstractCard> getAvailableCards()
    {
        if (availableCards == null)
        {
            AbstractCard.CardColor cardColor = getColor();
            availableCards = GameUtilities.isPCLOnlyCardColor(cardColor) ? EUIUtils.mapAsNonnull(PCLCardData.getAllData(false, false, cardColor), cd -> cd.makeCopyFromLibrary(0))
                    :
                    EUIUtils.filter(CardLibrary.getAllCards(),
                            c -> !PCLDungeon.isColorlessCardExclusive(c) && (c.color == AbstractCard.CardColor.COLORLESS || c.color == AbstractCard.CardColor.CURSE || c.color == cardColor));
            availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(cardColor), c -> c.getBuilder(0).createImpl()));
            if (cardColor != AbstractCard.CardColor.COLORLESS)
            {
                availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS), c -> c.getBuilder(0).createImpl()));
            }
            availableCards.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return availableCards;
    }

    protected static void invalidateCards()
    {
        availableCards = null;
    }
}
