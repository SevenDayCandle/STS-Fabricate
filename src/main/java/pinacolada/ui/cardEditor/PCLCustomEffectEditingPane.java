package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import extendedui.*;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.EUIHoverable;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.CostFilter;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLDungeon;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.cardEditor.nodes.PCLCustomEffectNode;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static pinacolada.ui.cardEditor.PCLCustomEffectPage.*;

public class PCLCustomEffectEditingPane extends PCLCustomGenericPage {
    public static final float CUTOFF = Settings.WIDTH * 0.4f;
    public static final float MAIN_OFFSET = MENU_WIDTH * 1.58f;
    public static final float AUX_OFFSET = MENU_WIDTH * 2.43f;
    protected static ArrayList<AbstractCard> availableCards;
    private PSkill<?> lastEffect;
    private float additionalHeight;
    protected ArrayList<EUIHoverable> activeElements = new ArrayList<>();
    protected EUISearchableDropdown<PSkill> effects;
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
    protected EUIImage backdrop;
    public EUIHitbox hb;
    public PCLCustomEffectPage editor;
    public PCLCustomEffectNode node;

    public PCLCustomEffectEditingPane(PCLCustomEffectPage editor, PCLCustomEffectNode node, EUIHitbox hb) {
        this.editor = editor;
        this.node = node;
        this.hb = hb;
        this.backdrop = new EUIBorderedImage(EUIRM.images.greySquare.texture(), new EUIHitbox(hb.x - scale(50), hb.y, CUTOFF * 1.1f, MAIN_OFFSET * 2));
        this.backdrop.setColor(Color.GRAY);
        initializeSelectors();
        refresh();
    }

    protected static void invalidateCards() {
        availableCards = null;
    }

    public void close() {
        editor.currentEditingSkill = null;
    }

    protected ArrayList<AbstractCard> getAvailableCards() {
        if (availableCards == null) {
            AbstractCard.CardColor cardColor = getColor();
            availableCards = GameUtilities.isPCLOnlyCardColor(cardColor) ? EUIUtils.mapAsNonnull(PCLCardData.getAllData(false, false, cardColor), cd -> cd.makeCardFromLibrary(0))
                    :
                    EUIUtils.filter(CardLibrary.getAllCards(),
                            c -> !PCLDungeon.isColorlessCardExclusive(c) && (c.color == AbstractCard.CardColor.COLORLESS || c.color == AbstractCard.CardColor.CURSE || c.color == cardColor));
            availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(cardColor), c -> c.getBuilder(0).create()));
            if (cardColor != AbstractCard.CardColor.COLORLESS) {
                availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS), c -> c.getBuilder(0).create()));
            }
            availableCards.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return availableCards;
    }

    public EditorMaker getBuilder() {
        return editor.screen.getBuilder();
    }

    public AbstractCard.CardColor getColor() {
        return getBuilder().getCardColor();
    }

    public Color getColorForEffect(PSkill<?> effect) {
        return editor.rootEffect == null || editor.rootEffect.isSkillAllowed(effect) ? Color.WHITE : Color.GRAY;
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorEffect;
    }

    @Override
    public String getTitle() {
        return editor.getTitle();
    }

    @Override
    public void refresh() {
        PSkillData<?> data = node.skill != null ? node.skill.data : null;
        int min = data != null ? data.minAmount : Integer.MIN_VALUE / 2;
        int max = data != null ? data.maxAmount : PSkill.DEFAULT_MAX;
        int eMin = data != null ? data.minExtra : Integer.MIN_VALUE / 2;
        int eMax = data != null ? data.maxExtra : PSkill.DEFAULT_MAX;

        effects.setSelection(node.skill, false);
        valueEditor
                .setLimits(min, max)
                .setValue(node.skill != null ? node.skill.amount : 0, node.skill != null ? node.skill.getUpgrade() : 0, false)
                .setActive(min != max);
        extraEditor
                .setLimits(eMin, eMax)
                .setValue(node.skill != null ? node.skill.extra : 0, node.skill != null ? node.skill.getUpgradeExtra() : 0, false)
                .setActive(eMin != eMax);
        if (node.skill != null && lastEffect != node.skill) {
            lastEffect = node.skill;
            activeElements.clear();
            targets
                    .setItems(PSkill.getEligibleTargets(node.skill))
                    .setActive(targets.getAllItems().size() > 1);
            piles.setItems(PSkill.getEligiblePiles(node.skill))
                    .setActive(piles.getAllItems().size() > 1);
            origins.setItems(PSkill.getEligibleOrigins(node.skill))
                    .setActive(origins.getAllItems().size() >= 1);
            piles.setItems(PSkill.getEligiblePiles(node.skill))
                    .setActive(piles.getAllItems().size() >= 1);
            node.skill.setupEditor(this);

            float xOff = 0;
            additionalHeight = -MENU_HEIGHT * 2.3f;
            if (targets.isActive) {
                targets.setSelection(node.skill.target, false);
                xOff = position(targets, xOff);
            }
            for (EUIHoverable element : activeElements) {
                xOff = position(element, xOff);
            }
            backdrop.hb.height = hb.height + additionalHeight * -1 + MENU_HEIGHT * 2;
            backdrop.hb.y = hb.y - backdrop.hb.height + MENU_HEIGHT * 2;
        }
    }

    public <T> EUIDropdown<T> initializeRegular(T[] items, FuncT1<String, T> labelFunc, String title) {
        return initializeRegular(Arrays.asList(items), labelFunc, title);
    }

    public <T> EUIDropdown<T> initializeRegular(Collection<T> items, FuncT1<String, T> labelFunc, String title) {
        return (EUIDropdown<T>) new EUIDropdown<T>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, 0, 0))
                .setLabelFunctionForOption(labelFunc, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(items);
    }

    public <T> EUISearchableDropdown<T> initializeSearchable(T[] items, FuncT1<String, T> labelFunc, String title) {
        return initializeSearchable(Arrays.asList(items), labelFunc, title);
    }

    public <T> EUISearchableDropdown<T> initializeSearchable(Collection<T> items, FuncT1<String, T> labelFunc, String title) {
        return (EUISearchableDropdown<T>) new EUISearchableDropdown<T>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, 0, 0))
                .setLabelFunctionForOption(labelFunc, false)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(items);
    }

    protected void initializeSelectors() {
        final AbstractCard.CardColor cardColor = getColor();
        effects = (EUISearchableDropdown<PSkill>) new EUISearchableDropdown<PSkill>(hb, skill -> StringUtils.capitalize(skill.getSampleText(editor.rootEffect)))
                .setOnChange(effects -> {
                    if (!effects.isEmpty()) {
                        node.replaceSkill(effects.get(0));
                    }
                    editor.updateRootEffect();
                })
                .setLabelColorFunctionForOption(this::getColorForEffect)
                .setClearButtonOptions(false, false)
                .setCanAutosizeButton(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, node.type.getTitle())
                .setItems(node.getEffects());
        valueEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 5, MENU_HEIGHT, MAIN_OFFSET, OFFSET_AMOUNT)
                , EUIRM.strings.uiAmount, (val, upVal) -> {
            if (node.skill != null) {
                node.skill.setAmount(val, upVal);
                editor.updateRootEffect();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX);
        extraEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 5, MENU_HEIGHT, MAIN_OFFSET * 1.3f, OFFSET_AMOUNT)
                , PGR.core.strings.cedit_extraValue, (val, upVal) -> {
            if (node.skill != null) {
                node.skill.setExtra(val, upVal);
                editor.updateRootEffect();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX);

        targets = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , PCLCardTarget::getTitle)
                .setOnChange(targets -> {
                    if (node.skill != null && !targets.isEmpty()) {
                        node.skill.setTarget(targets.get(0));
                        editor.updateRootEffect();
                    }
                })
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_cardTarget)
                .setCanAutosize(true, true)
                .setItems(PCLCustomCardAttributesPage.getEligibleTargets(cardColor));

        piles = initializeRegular(PCLCardGroupHelper.getStandard(), PCLCardGroupHelper::getCapitalTitle, PGR.core.strings.cedit_pile);
        origins = initializeRegular(PCLCardSelection.values(), PCLCardSelection::getTitle, PGR.core.strings.cedit_pile);
        affinities = initializeSmartSearchable(PCLCustomCardAttributesPage.getEligibleAffinities(cardColor), PGR.core.strings.sui_affinities);
        powers = initializeSmartSearchable(PCLPowerHelper.sortedValues(), PGR.core.strings.cedit_powers);
        orbs = initializeSmartSearchable(PCLOrbHelper.visibleValues(), PGR.core.strings.cedit_orbs);
        stances = initializeSmartSearchable(PCLStanceHelper.values(cardColor), PGR.core.tooltips.stance.title);
        tags = initializeSmartSearchable(PCLCardTag.getAll(), PGR.core.strings.cedit_tags);
        cardData = initializeSearchable(getAvailableCards(), c -> c.name, RunHistoryScreen.TEXT[9]);
        colors = initializeSearchable(AbstractCard.CardColor.values(), EUIGameUtils::getColorName, EUIRM.strings.uiColors);
        rarities = initializeSearchable(PCLCustomCardPrimaryInfoPage.getEligibleRarities(), EUIGameUtils::textForRarity, CardLibSortHeader.TEXT[0]);
        types = initializeSearchable(PCLCustomCardPrimaryInfoPage.getEligibleTypes(cardColor), EUIGameUtils::textForType, CardLibSortHeader.TEXT[1]);
        costs = initializeSearchable(CostFilter.values(), c -> c.name, CardLibSortHeader.TEXT[3]);
    }

    public <T extends TooltipProvider> EUISearchableDropdown<T> initializeSmartSearchable(T[] items, String title) {
        return initializeSmartSearchable(Arrays.asList(items), title);
    }

    public <T extends TooltipProvider> EUISearchableDropdown<T> initializeSmartSearchable(Collection<T> items, String title) {
        EUISearchableDropdown<T> dropdown = (EUISearchableDropdown<T>) new EUISearchableDropdown<T>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, 0, 0))
                .setLabelFunctionForOption(item -> item.getTooltip().getTitleOrIconForced() + " " + item.getTooltip().title, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(items);
        dropdown.setLabelFunctionForButton((list, __) -> dropdown.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), true);
        return dropdown;
    }

    protected <U> float position(EUIHoverable element, float x) {
        // Don't shift the positions if the element is not visible
        if (!element.isActive) {
            return x;
        }

        float setX = x;
        float end = x + element.hb.width;
        if (end > CUTOFF) {
            additionalHeight -= MENU_HEIGHT * 2f;
            setX = 0;
            end = element.hb.width;
        }
        element.setOffset(setX, additionalHeight);
        element.hb.update();
        return end + scale(20);
    }

    public void registerAffinity(List<PCLAffinity> items) {
        registerDropdown(affinities, items);
    }

    public void registerBoolean(String title, ActionT1<Boolean> onChange, boolean initial) {
        registerBoolean(title, null, onChange, initial);
    }

    public void registerBoolean(String title, String desc, ActionT1<Boolean> onChange, boolean initial) {
        registerBoolean(new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.62f, MENU_HEIGHT, MENU_WIDTH, 0))
                        .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                        .setText(title)
                        .setTooltip(desc != null ? new EUITooltip(title, desc) : null)
                , onChange, initial);
    }

    public void registerBoolean(EUIToggle toggle, ActionT1<Boolean> onChange, boolean initial) {
        activeElements.add(toggle);
        toggle.setToggle(initial);
        toggle.setOnToggle(val -> {
            onChange.invoke(val);
            editor.updateRootEffect();
        });
    }

    public void registerCard(List<String> cardIDs) {
        registerDropdown(cardData,
                cards -> {
                    cardIDs.clear();
                    cardIDs.addAll(EUIUtils.mapAsNonnull(cards, t -> t.cardID));
                },
                cardIDs,
                card -> card.cardID
        );
    }

    public <V> void registerCard(List<String> cardIDs, ActionT1<List<AbstractCard>> onChangeImpl) {
        registerDropdown(cardData,
                onChangeImpl,
                cardIDs,
                card -> card.cardID
        );
    }

    public void registerColor(List<AbstractCard.CardColor> items) {
        registerDropdown(colors, items);
    }

    public void registerCost(List<CostFilter> items) {
        registerDropdown(costs, items);
    }

    public <U, V> void registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, List<V> items, FuncT1<V, U> convertFunc) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(items, convertFunc, false);
        }
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, List<U> items) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(items, false);
        }
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, List<U> items) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                items.clear();
                items.addAll(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(items, false);
        }
    }

    public <U> void registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, U item) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(item, false);
        }
    }

    public <U> void registerDropdown(List<U> possibleItems, List<U> selectedItems, FuncT1<String, U> textFunc, String title, boolean smartText) {
        registerDropdown(possibleItems, selectedItems, textFunc, title, smartText, true, true);
    }

    public <U> void registerDropdown(List<U> possibleItems, List<U> selectedItems, FuncT1<String, U> textFunc, String title, boolean smartText, boolean multiSelect, boolean positionClearAtTop) {
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

    public void registerOrb(List<PCLOrbHelper> items) {
        registerDropdown(orbs, items);
    }

    public void registerOrigin(PCLCardSelection item, ActionT1<List<PCLCardSelection>> onChangeImpl) {
        registerDropdown(origins, onChangeImpl, item);
    }

    public void registerPile(List<PCLCardGroupHelper> items) {
        registerDropdown(piles, items);
    }

    public void registerPower(List<PCLPowerHelper> items) {
        registerDropdown(powers, items);
    }

    public void registerRarity(List<AbstractCard.CardRarity> items) {
        registerDropdown(rarities, items);
    }

    public void registerStance(List<PCLStanceHelper> items) {
        registerDropdown(stances, items);
    }

    public void registerTag(List<PCLCardTag> items) {
        registerDropdown(tags, items);
    }

    public void registerType(List<AbstractCard.CardType> items) {
        registerDropdown(types, items);
    }

    @Override
    public void updateImpl() {
        boolean wasBusy = EUI.doesActiveElementExist();
        this.backdrop.tryUpdate();
        this.effects.tryUpdate();
        this.targets.tryUpdate();
        this.valueEditor.tryUpdate();
        this.extraEditor.tryUpdate();
        for (EUIHoverable element : activeElements) {
            element.tryUpdate();
        }
        if (EUIInputManager.leftClick.isJustPressed() && !wasBusy &&
                !backdrop.hb.hovered && !effects.areAnyItemsHovered() && !targets.areAnyItemsHovered() && !valueEditor.hb.hovered && !extraEditor.hb.hovered && !EUIUtils.any(activeElements, e -> e.hb.hovered)) {
            close();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.backdrop.tryRender(sb);
        this.effects.tryRender(sb);
        this.targets.tryRender(sb);
        this.valueEditor.tryRender(sb);
        this.extraEditor.tryRender(sb);
        for (EUIHoverable element : activeElements) {
            element.tryRender(sb);
        }
    }
}
