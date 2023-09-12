package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.*;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIHoverable;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
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
import pinacolada.monsters.PCLIntentType;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLDynamicPower;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.powers.PCLPowerData;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.editor.card.PCLCustomCardAttributesPage;
import pinacolada.ui.editor.card.PCLCustomCardEditCardScreen;
import pinacolada.ui.editor.card.PCLCustomCardPrimaryInfoPage;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static pinacolada.ui.editor.PCLCustomEffectPage.*;

public class PCLCustomEffectEditingPane extends PCLCustomGenericPage {
    public static final float CUTOFF = Settings.WIDTH * 0.4f;
    public static final float MAIN_OFFSET = MENU_WIDTH * 3.16f;
    public static final float AUX_OFFSET = MENU_WIDTH * 2.43f;
    protected static ArrayList<AbstractCard> availableCards;
    protected static ArrayList<AbstractPotion> availablePotions;
    protected static ArrayList<PCLPowerData> availablePowers;
    protected static ArrayList<AbstractRelic> availableRelics;
    private PSkill<?> lastEffect;
    private float additionalHeight;
    protected ArrayList<EUIHoverable> activeElements = new ArrayList<>();
    protected EUISearchableDropdown<PSkill<?>> effects;
    protected EUIDropdown<PCLCardGroupHelper> piles;
    protected EUIDropdown<PCLCardSelection> destinations;
    protected EUIDropdown<PCLCardSelection> origins;
    protected EUIDropdown<PCLCardTarget> targets;
    protected PCLCustomUpgradableEditor valueEditor;
    protected PCLCustomUpgradableEditor extraEditor;
    protected EUIImage backdrop;
    public EUIHitbox hb;
    public PCLCustomEffectPage editor;
    public PCLCustomEffectNode node;
    public boolean shouldOverrideTarget;

    public PCLCustomEffectEditingPane(PCLCustomEffectPage editor, PCLCustomEffectNode node, EUIHitbox hb) {
        this.editor = editor;
        this.node = node;
        this.hb = hb;
        this.backdrop = new EUIBorderedImage(EUIRM.images.greySquare.texture(), new EUIHitbox(hb.x - scale(85), hb.y, CUTOFF * 1.3f, MAIN_OFFSET));
        this.backdrop.setColor(Color.GRAY);
        initializeSelectors();
        refresh();
    }

    public static void invalidateItems() {
        availableCards = null;
        availablePotions = null;
        availablePowers = null;
        availableRelics = null;
    }

    public void changeAmountForSkill(PSkill<?> skill, int val, int upVal) {
        PCLCustomCardEditCardScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditCardScreen.class);
        if (sc != null) {
            switch (skill.getAmountSource()) {
                case Damage:
                    sc.modifyBuilder(e -> e.setDamageForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case Block:
                    sc.modifyBuilder(e -> e.setBlockForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case MagicNumber:
                    sc.modifyBuilder(e -> e.setMagicNumberForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case SecondaryNumber:
                    sc.modifyBuilder(e -> e.setHpForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case HitCount:
                    sc.modifyBuilder(e -> e.setHitCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case RightCount:
                    sc.modifyBuilder(e -> e.setRightCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
            }
        }
        skill.setAmount(val, upVal);
    }

    public void changeExtraForSkill(PSkill<?> skill, int val, int upVal) {
        PCLCustomCardEditCardScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditCardScreen.class);
        if (sc != null) {
            switch (skill.getExtraSource()) {
                case Damage:
                    sc.modifyBuilder(e -> e.setDamageForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case Block:
                    sc.modifyBuilder(e -> e.setBlockForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case MagicNumber:
                    sc.modifyBuilder(e -> e.setMagicNumberForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case SecondaryNumber:
                    sc.modifyBuilder(e -> e.setHpForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case HitCount:
                    sc.modifyBuilder(e -> e.setHitCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case RightCount:
                    sc.modifyBuilder(e -> e.setRightCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
            }
        }
        skill.setExtra(val, upVal);
    }

    public void close() {
        editor.currentEditingSkill = null;
    }

    public int getAmountForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditCardScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditCardScreen.class);
        if (sc != null) {
            switch (skill.getAmountSource()) {
                case Damage:
                    return sc.getBuilder().getDamage(0);
                case Block:
                    return sc.getBuilder().getBlock(0);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumber(0);
                case SecondaryNumber:
                    return sc.getBuilder().getHp(0);
                case HitCount:
                    return sc.getBuilder().getHitCount(0);
                case RightCount:
                    return sc.getBuilder().getRightCount(0);
            }
        }
        return skill.amount;
    }

    public int getAmountUpgradeForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditCardScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditCardScreen.class);
        if (sc != null) {
            switch (skill.getAmountSource()) {
                case Damage:
                    return sc.getBuilder().getDamageUpgrade(0);
                case Block:
                    return sc.getBuilder().getBlockUpgrade(0);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumberUpgrade(0);
                case SecondaryNumber:
                    return sc.getBuilder().getHpUpgrade(0);
                case HitCount:
                    return sc.getBuilder().getHitCountUpgrade(0);
                case RightCount:
                    return sc.getBuilder().getRightCountUpgrade(0);
            }
        }
        return skill.getUpgrade();
    }

    protected ArrayList<AbstractCard> getAvailableCards() {
        if (availableCards == null) {
            AbstractCard.CardColor cardColor = getColor();
            availableCards = GameUtilities.isPCLOnlyCardColor(cardColor) ? EUIUtils.mapAsNonnull(PCLCardData.getAllData(false, false, cardColor), cd -> cd.makeCardFromLibrary(0)) :
                    EUIUtils.filterInPlace(CardLibrary.getAllCards(),
                            c -> !PCLDungeon.isColorlessCardExclusive(c) && (c.color == AbstractCard.CardColor.COLORLESS || c.color == AbstractCard.CardColor.CURSE || c.color == cardColor));
            availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(cardColor), PCLCustomCardSlot::make));
            if (cardColor != AbstractCard.CardColor.COLORLESS) {
                availableCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS), PCLCustomCardSlot::make));
            }
            availableCards.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return availableCards;
    }

    protected ArrayList<AbstractPotion> getAvailablePotions() {
        if (availablePotions == null) {
            AbstractCard.CardColor cardColor = getColor();
            availablePotions = new ArrayList<>(GameUtilities.getPotions(null));
            availablePotions.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return availablePotions;
    }

    protected ArrayList<PCLPowerData> getAvailablePowers() {
        if (availablePowers == null) {
            availablePowers = new ArrayList<>(PCLPowerData.getAllData());
            availablePowers.addAll(EUIUtils.map(PCLCustomPowerSlot.getAll().values(), slot -> slot.getBuilder(0)));
            availablePowers.sort((a, b) -> StringUtils.compare(a.getName(), b.getName()));
        }
        return availablePowers;
    }

    protected ArrayList<AbstractRelic> getAvailableRelics() {
        if (availableRelics == null) {
            AbstractCard.CardColor cardColor = getColor();
            availableRelics = new ArrayList<>(GameUtilities.getRelics(cardColor).values());
            availableRelics.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(cardColor), PCLCustomRelicSlot::make));
            if (cardColor != AbstractCard.CardColor.COLORLESS) {
                availableRelics.addAll(GameUtilities.getRelics(AbstractCard.CardColor.COLORLESS).values());
                availableRelics.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(AbstractCard.CardColor.COLORLESS), PCLCustomRelicSlot::make));
            }
            availableRelics.sort((a, b) -> StringUtils.compare(a.name, b.name));
        }
        return availableRelics;
    }

    public EditorMaker getBuilder() {
        return editor.screen.getBuilder();
    }

    public AbstractCard.CardColor getColor() {
        return getBuilder().getCardColor();
    }

    public Color getColorForEffect(PSkill<?> effect) {
        return editor.rootEffect == null || effect instanceof PPrimary || editor.rootEffect.isSkillAllowed(effect) ? Color.WHITE : Color.GRAY;
    }

    public int getExtraForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditCardScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditCardScreen.class);
        if (sc != null) {
            switch (skill.getExtraSource()) {
                case Damage:
                    return sc.getBuilder().getDamage(0);
                case Block:
                    return sc.getBuilder().getBlock(0);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumber(0);
                case SecondaryNumber:
                    return sc.getBuilder().getHp(0);
                case HitCount:
                    return sc.getBuilder().getHitCount(0);
                case RightCount:
                    return sc.getBuilder().getRightCount(0);
            }
        }
        return skill.extra;
    }

    public int getExtraUpgradeForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditCardScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditCardScreen.class);
        if (sc != null) {
            switch (skill.getExtraSource()) {
                case Damage:
                    return sc.getBuilder().getDamageUpgrade(0);
                case Block:
                    return sc.getBuilder().getBlockUpgrade(0);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumberUpgrade(0);
                case SecondaryNumber:
                    return sc.getBuilder().getHpUpgrade(0);
                case HitCount:
                    return sc.getBuilder().getHitCountUpgrade(0);
                case RightCount:
                    return sc.getBuilder().getRightCountUpgrade(0);
            }
        }
        return skill.getUpgradeExtra();
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorEffect;
    }

    @Override
    public String getTitle() {
        return editor.getTitle();
    }

    public String getSmartSearchableLabel(TooltipProvider item) {
        EUITooltip tip = item.getTooltip();
        return tip instanceof EUIKeywordTooltip && ((EUIKeywordTooltip) tip).icon != null ? tip.getTitleOrIconForced() + " " + tip.title : tip.title;
    }

    public <T> EUIDropdown<T> initializeRegular(T[] items, FuncT1<String, T> labelFunc, String title, boolean multiselect) {
        return initializeRegular(Arrays.asList(items), labelFunc, title, multiselect);
    }

    public <T> EUIDropdown<T> initializeRegular(Collection<T> items, FuncT1<String, T> labelFunc, String title, boolean multiselect) {
        return new EUIDropdown<T>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, 0, 0))
                .setLabelFunctionForOption(labelFunc, false)
                .setIsMultiSelect(multiselect)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, title)
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
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(items);
    }

    protected void initializeSelectors() {
        final AbstractCard.CardColor cardColor = getColor();
        effects = (EUISearchableDropdown<PSkill<?>>) new EUISearchableDropdown<PSkill<?>>(hb, skill -> StringUtils.capitalize(skill.getSampleText(editor.rootEffect, node.parent != null ? node.parent.skill : null)))
                .setOnChange(effects -> {
                    if (!effects.isEmpty()) {
                        node.replaceSkill(effects.get(0));
                    }
                    editor.updateRootEffect();
                })
                .setLabelColorFunctionForOption(this::getColorForEffect)
                .setClearButtonOptions(false, false)
                .setCanAutosizeButton(true)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, node.type.getTitle())
                .setItems(node.getEffects());
        effects.sortByLabel();
        effects.setActive(effects.size() > 1);
        valueEditor = new PCLCustomUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f, MENU_HEIGHT, effects.isActive ? effects.hb.width + MENU_WIDTH * 0.2f : 0, OFFSET_AMOUNT)
                , EUIRM.strings.ui_amount, (val, upVal) -> {
            if (node.skill != null) {
                changeAmountForSkill(node.skill, val, upVal);
                editor.updateRootEffect();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX);
        extraEditor = new PCLCustomUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f, MENU_HEIGHT, effects.isActive ? effects.hb.width + MENU_WIDTH * 0.7f : MENU_WIDTH * 0.5f, OFFSET_AMOUNT)
                , PGR.core.strings.cedit_extraValue, (val, upVal) -> {
            if (node.skill != null) {
                changeExtraForSkill(node.skill, val, upVal);
                editor.updateRootEffect();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX);

        targets = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , PCLCardTarget::getTitle)
                .setOnChange(this::modifyTargets)
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_cardTarget)
                .setCanAutosize(true, true)
                .setItems(PCLCustomCardAttributesPage.getEligibleTargets(cardColor));

        destinations = initializeRegular(PCLCardSelection.values(), PCLCardSelection::getTitle, PGR.core.strings.cedit_destinations, false);
        origins = initializeRegular(PCLCardSelection.values(), PCLCardSelection::getTitle, PGR.core.strings.cedit_origins, false);
        piles = initializeRegular(PCLCardGroupHelper.getStandard(), PCLCardGroupHelper::getCapitalTitle, PGR.core.strings.cedit_pile, true);
    }

    protected void modifyTargets(List<PCLCardTarget> targets) {
        if (node.skill != null && !targets.isEmpty()) {
            node.skill.setTarget(targets.get(0));
            if (shouldOverrideTarget) {
                if (editor.screen instanceof PCLCustomCardEditCardScreen) {
                    ((PCLCustomCardEditCardScreen) editor.screen).modifyBuilder(e -> e.setTarget(targets.get(0)));
                    for (PCLCustomGenericPage page: editor.screen.pages) {
                        if (page != editor) {
                            page.refresh();
                        }
                    }
                }
            }
            editor.updateRootEffect();
        }
    }

    public <T extends TooltipProvider> EUISearchableDropdown<T> initializeSmartSearchable(T[] items, String title) {
        return initializeSmartSearchable(Arrays.asList(items), title);
    }

    public <T extends TooltipProvider> EUISearchableDropdown<T> initializeSmartSearchable(Collection<T> items, String title) {
        return initializeSmartSearchable(items, title, this::getSmartSearchableLabel);
    }

    public <T extends TooltipProvider> EUISearchableDropdown<T> initializeSmartSearchable(Collection<T> items, String title, FuncT1<String, T> labelFunc) {
        EUISearchableDropdown<T> dropdown = (EUISearchableDropdown<T>) new EUISearchableDropdown<T>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, 0, 0))
                .setLabelFunctionForOption(labelFunc, true)
                .setIsMultiSelect(true)
                .setShouldPositionClearAtTop(true)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, title)
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

    @Override
    public void refresh() {
        PSkillData<?> data = node.skill != null ? node.skill.data : null;
        int min = data != null ? data.minAmount : Integer.MIN_VALUE / 2;
        int max = data != null ? data.maxAmount : PSkill.DEFAULT_MAX;
        int eMin = data != null ? data.minExtra : Integer.MIN_VALUE / 2;
        int eMax = data != null ? data.maxExtra : PSkill.DEFAULT_MAX;
        shouldOverrideTarget = false;

        effects.setSelection(node.skill, false);
        valueEditor
                .setLimits(min, max)
                .setValue(getAmountForSkill(node.skill), getAmountUpgradeForSkill(node.skill), false)
                .setActive(min != max);
        extraEditor
                .setLimits(eMin, eMax)
                .setValue(getExtraForSkill(node.skill), getExtraUpgradeForSkill(node.skill), false)
                .setActive(eMin != eMax);
        if (node.skill != null && lastEffect != node.skill) {
            lastEffect = node.skill;
            activeElements.clear();
            valueEditor.setHeaderText(node.skill.getHeaderTextForAmount());
            extraEditor.setHeaderText(node.skill.getHeaderTextForExtra());
            targets
                    .setItems(PSkill.getEligibleTargets(node.skill))
                    .setActive(targets.getAllItems().size() > 1);
            piles.setItems(PSkill.getEligiblePiles(node.skill))
                    .setActive(piles.getAllItems().size() > 1);
            destinations.setItems(PSkill.getEligibleDestinations(node.skill))
                    .setActive(destinations.getAllItems().size() >= 1);
            origins.setItems(PSkill.getEligibleOrigins(node.skill))
                    .setActive(origins.getAllItems().size() >= 1);
            piles.setItems(PSkill.getEligiblePiles(node.skill))
                    .setActive(piles.getAllItems().size() >= 1);
            node.skill.setupEditor(this);

            float xOff = 0;
            additionalHeight = -MENU_HEIGHT * 2.7f;
            if (targets.isActive) {
                targets.setSelection(node.skill.target, false);
                xOff = position(targets, xOff);
            }
            for (EUIHoverable element : activeElements) {
                xOff = position(element, xOff);
            }
            backdrop.hb.height = hb.height + additionalHeight * -1 + MENU_HEIGHT * 4f;
            backdrop.hb.y = hb.y - backdrop.hb.height + MENU_HEIGHT * 3.3f;
        }
        else if (node.skill == null) {
            valueEditor.setHeaderText(PGR.core.strings.cedit_value);
            extraEditor.setHeaderText(PGR.core.strings.cedit_extraValue);
        }
    }

    public void registerAffinity(List<PCLAffinity> items) {
        registerDropdown(initializeSmartSearchable(PCLCustomCardAttributesPage.getEligibleAffinities(getColor()), PGR.core.tooltips.affinityGeneral.title), items);
    }

    public void registerBoolean(String title, ActionT1<Boolean> onChange, boolean initial) {
        registerBoolean(title, null, onChange, initial);
    }

    public void registerBoolean(String title, String desc, ActionT1<Boolean> onChange, boolean initial) {
        float predictLength = EUISmartText.getSmartWidth(EUIFontHelper.cardDescriptionFontNormal, title, Settings.WIDTH, 0f);
        registerBoolean(new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f + predictLength, MENU_HEIGHT, MENU_WIDTH, 0))
                        .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.9f)
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
        registerDropdown(initializeSearchable(getAvailableCards(), c -> c.name, StringUtils.capitalize(PGR.core.strings.subjects_card)),
                cards -> {
                    cardIDs.clear();
                    cardIDs.addAll(EUIUtils.mapAsNonnull(cards, t -> t.cardID));
                },
                cardIDs,
                card -> card.cardID
        );
    }

    public <V> void registerCard(List<String> cardIDs, ActionT1<List<AbstractCard>> onChangeImpl) {
        registerDropdown(initializeSearchable(getAvailableCards(), c -> c.name, StringUtils.capitalize(PGR.core.strings.subjects_card)),
                onChangeImpl,
                cardIDs,
                card -> card.cardID
        );
    }

    public void registerColor(List<AbstractCard.CardColor> items) {
        registerDropdown(initializeSearchable(AbstractCard.CardColor.values(), EUIGameUtils::getColorName, EUIRM.strings.ui_colors), items);
    }

    public void registerCost(List<CostFilter> items) {
        registerDropdown(initializeSearchable(CostFilter.values(), c -> c.name, CardLibSortHeader.TEXT[3]), items);
    }

    public void registerDestination(PCLCardSelection item, ActionT1<List<PCLCardSelection>> onChangeImpl) {
        registerDropdown(destinations, onChangeImpl, item);
    }

    public <U, V> EUIDropdown<U> registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, Collection<V> items, FuncT1<V, U> convertFunc) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(items, convertFunc, false);
        }
        return dropdown;
    }

    public <U> EUIDropdown<U> registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, Collection<U> items) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(items, false);
        }
        return dropdown;
    }

    public <U> EUIDropdown<U> registerDropdown(EUIDropdown<U> dropdown, Collection<U> items) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                items.clear();
                items.addAll(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(items, false);
        }
        return dropdown;
    }

    public <U> EUIDropdown<U> registerDropdown(EUIDropdown<U> dropdown, ActionT1<List<U>> onChangeImpl, U item) {
        if (dropdown.size() > 0) {
            activeElements.add(dropdown);
            dropdown.setOnChange(targets -> {
                onChangeImpl.invoke(targets);
                editor.updateRootEffect();
            });
            dropdown.setSelection(item, false);
        }
        return dropdown;
    }

    public <U> EUIDropdown<U> registerDropdown(Collection<U> possibleItems, Collection<U> selectedItems, FuncT1<String, U> textFunc, String title, boolean smartText) {
        return registerDropdown(possibleItems, selectedItems, textFunc, title, smartText, true, true);
    }

    public <U> EUIDropdown<U> registerDropdown(Collection<U> possibleItems, Collection<U> selectedItems, FuncT1<String, U> textFunc, String title, boolean smartText, boolean multiSelect, boolean positionClearAtTop) {
        EUIDropdown<U> dropdown = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , textFunc)
                .setLabelFunctionForOption(textFunc, smartText)
                .setIsMultiSelect(multiSelect)
                .setShouldPositionClearAtTop(positionClearAtTop)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(possibleItems);
        return registerDropdown(dropdown, selectedItems);
    }

    public <U> EUIDropdown<U> registerDropdown(Collection<U> possibleItems, Collection<U> selectedItems, ActionT1<List<U>> onChangeImpl, FuncT1<String, U> textFunc, String title, boolean smartText) {
        return registerDropdown(possibleItems, selectedItems, textFunc, title, smartText, true, true);
    }

    public <U> EUIDropdown<U> registerDropdown(Collection<U> possibleItems, Collection<U> selectedItems, ActionT1<List<U>> onChangeImpl, FuncT1<String, U> textFunc, String title, boolean smartText, boolean multiSelect, boolean positionClearAtTop) {
        EUIDropdown<U> dropdown = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, AUX_OFFSET, 0)
                , textFunc)
                .setLabelFunctionForOption(textFunc, smartText)
                .setIsMultiSelect(multiSelect)
                .setShouldPositionClearAtTop(positionClearAtTop)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(possibleItems);
        return registerDropdown(dropdown, onChangeImpl, selectedItems);
    }

    public void registerIntent(List<PCLIntentType> items) {
        registerDropdown(initializeSearchable(PCLIntentType.sorted(), PCLIntentType::getHeaderString, StringUtils.capitalize(PGR.core.strings.subjects_intent)), items);
    }

    public void registerLoadout(List<String> cardIDs) {
        registerDropdown(initializeSearchable(PCLLoadout.getAll(getColor()), PCLLoadout::getName, PGR.core.strings.sui_seriesUI),
                cards -> {
                    cardIDs.clear();
                    cardIDs.addAll(EUIUtils.mapAsNonnull(cards, t -> t.ID));
                },
                cardIDs,
                card -> card.ID
        );
    }

    public <V> void registerLoadout(List<String> cardIDs, ActionT1<List<PCLLoadout>> onChangeImpl) {
        registerDropdown(initializeSearchable(PCLLoadout.getAll(getColor()), PCLLoadout::getName, PGR.core.strings.sui_seriesUI),
                onChangeImpl,
                cardIDs,
                card -> card.ID
        );
    }

    public void registerOrb(List<PCLOrbHelper> items) {
        registerDropdown(initializeSmartSearchable(PCLOrbHelper.visibleValues(), PGR.core.tooltips.orb.title), items);
    }

    public void registerOrigin(PCLCardSelection item, ActionT1<List<PCLCardSelection>> onChangeImpl) {
        registerDropdown(origins, onChangeImpl, item);
    }

    public void registerPile(List<PCLCardGroupHelper> items) {
        registerDropdown(piles, items);
    }

    public void registerPotion(List<String> potionIDs) {
        registerDropdown(initializeSearchable(getAvailablePotions(), c -> c.name, StringUtils.capitalize(PGR.core.strings.subjects_potion)),
                relics -> {
                    potionIDs.clear();
                    potionIDs.addAll(EUIUtils.mapAsNonnull(relics, t -> t.ID));
                },
                potionIDs,
                potion -> potion.ID
        );
    }

    public <V> void registerPotion(List<String> potionIDs, ActionT1<List<AbstractPotion>> onChangeImpl) {
        registerDropdown(initializeSearchable(getAvailablePotions(), c -> c.name, StringUtils.capitalize(PGR.core.strings.subjects_potion)),
                onChangeImpl,
                potionIDs,
                potion -> potion.ID
        );
    }

    public void registerPotionRarity(List<AbstractPotion.PotionRarity> items) {
        registerDropdown(initializeSearchable(AbstractPotion.PotionRarity.values(), EUIGameUtils::textForPotionRarity, CardLibSortHeader.TEXT[0]), items);
    }

    public void registerPotionSize(List<AbstractPotion.PotionSize> items) {
        registerDropdown(initializeSearchable(AbstractPotion.PotionSize.values(), EUIGameUtils::textForPotionSize, EUIRM.strings.potion_size), items);
    }

    public void registerPower(List<String> powerIDs) {
        registerDropdown(initializeSmartSearchable(getAvailablePowers(), PGR.core.strings.cedit_powers, pd -> pd instanceof PCLDynamicPowerData ? pd.getName() : getSmartSearchableLabel(pd)),
                powers -> {
                    powerIDs.clear();
                    powerIDs.addAll(EUIUtils.mapAsNonnull(powers, t -> t.ID));
                },
                powerIDs,
                power -> power.ID
        );
    }

    public void registerRarity(List<AbstractCard.CardRarity> items) {
        registerDropdown(initializeSearchable(PCLCustomCardPrimaryInfoPage.getEligibleRarities(), EUIGameUtils::textForRarity, CardLibSortHeader.TEXT[0]), items);
    }

    public void registerRelic(List<String> relicIDs) {
        registerDropdown(initializeSearchable(getAvailableRelics(), relic -> relic.name, StringUtils.capitalize(PGR.core.strings.subjects_relic)),
                relics -> {
                    relicIDs.clear();
                    relicIDs.addAll(EUIUtils.mapAsNonnull(relics, t -> t.relicId));
                },
                relicIDs,
                relic -> relic.relicId
        );
    }

    public <V> void registerRelic(List<String> relicIDs, ActionT1<List<AbstractRelic>> onChangeImpl) {
        registerDropdown(initializeSearchable(getAvailableRelics(), relic -> relic.name, StringUtils.capitalize(PGR.core.strings.subjects_relic)),
                onChangeImpl,
                relicIDs,
                relic -> relic.relicId
        );
    }

    public void registerRelicRarity(List<AbstractRelic.RelicTier> items) {
        registerDropdown(initializeSearchable(AbstractRelic.RelicTier.values(), EUIGameUtils::textForRelicTier, CardLibSortHeader.TEXT[0]), items);
    }

    public void registerStance(List<PCLStanceHelper> items) {
        registerDropdown(initializeSmartSearchable(PCLStanceHelper.getAll(getColor()), PGR.core.tooltips.stance.title), items);
    }

    public void registerTag(List<PCLCardTag> items) {
        registerDropdown(initializeSmartSearchable(PCLCardTag.getAll(), PGR.core.strings.cedit_tags), items);
    }

    public void registerType(List<AbstractCard.CardType> items) {
        registerDropdown(initializeSearchable(PCLCustomCardPrimaryInfoPage.getEligibleTypes(getColor()), EUIGameUtils::textForType, CardLibSortHeader.TEXT[1]), items);
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
}
