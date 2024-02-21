package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.*;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.EUIHoverable;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.BlightTier;
import extendedui.utilities.CostFilter;
import extendedui.utilities.EUITextHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.CardFlag;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.monsters.PCLIntentType;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.editor.card.PCLCustomCardAttributesPage;
import pinacolada.ui.editor.card.PCLCustomCardEditScreen;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;
import pinacolada.ui.editor.power.PCLCustomPowerEditScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static pinacolada.ui.editor.PCLCustomEffectPage.OFFSET_AMOUNT;

public class PCLCustomEffectEditingPane extends PCLCustomGenericPage {
    public static final float CUTOFF = Settings.WIDTH * 0.4f;
    public static final float MAIN_OFFSET = MENU_WIDTH * 3.16f;
    public static final float AUX_OFFSET = MENU_WIDTH * 2.43f;
    private final ArrayList<EUIHoverable> activeElements = new ArrayList<>();
    private PSkill<?> lastEffect;
    private float additionalHeight;
    protected EUISearchableDropdown<PSkill<?>> effects;
    protected EUIDropdown<PCLCardGroupHelper> piles;
    protected EUIDropdown<PCLCardSelection> destinations;
    protected EUIDropdown<PCLCardSelection> origins;
    protected EUIDropdown<PCLCardTarget> targets;
    protected PCLCustomUpgradableEditor valueEditor;
    protected PCLCustomUpgradableEditor extraEditor;
    protected PCLCustomUpgradableEditor scopeEditor;
    protected PCLCustomUpgradableEditor upgradeEditor;
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

    public void changeAmountForSkill(PSkill<?> skill, int val, int upVal) {
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getAmountSource()) {
                case Damage:
                    sc.modifyAllBuilders((e, i) -> e.setDamageForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case Block:
                    sc.modifyAllBuilders((e, i) -> e.setBlockForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case MagicNumber:
                    sc.modifyAllBuilders((e, i) -> e.setMagicNumberForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case SecondaryNumber:
                    sc.modifyAllBuilders((e, i) -> e.setHpForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case HitCount:
                    sc.modifyAllBuilders((e, i) -> e.setHitCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case RightCount:
                    sc.modifyAllBuilders((e, i) -> e.setRightCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
            }
        }
        skill.setAmount(val, upVal);
    }

    public void changeExtra2ForSkill(PSkill<?> skill, int val, int upVal) {
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getExtra2Source()) {
                case Damage:
                    sc.modifyAllBuilders((e, i) -> e.setDamageForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case Block:
                    sc.modifyAllBuilders((e, i) -> e.setBlockForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case MagicNumber:
                    sc.modifyAllBuilders((e, i) -> e.setMagicNumberForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case SecondaryNumber:
                    sc.modifyAllBuilders((e, i) -> e.setHpForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case HitCount:
                    sc.modifyAllBuilders((e, i) -> e.setHitCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case RightCount:
                    sc.modifyAllBuilders((e, i) -> e.setRightCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
            }
        }
        skill.setExtra2(val, upVal);
    }

    public void changeExtraForSkill(PSkill<?> skill, int val, int upVal) {
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getExtraSource()) {
                case Damage:
                    sc.modifyAllBuilders((e, i) -> e.setDamageForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case Block:
                    sc.modifyAllBuilders((e, i) -> e.setBlockForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case MagicNumber:
                    sc.modifyAllBuilders((e, i) -> e.setMagicNumberForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case SecondaryNumber:
                    sc.modifyAllBuilders((e, i) -> e.setHpForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case HitCount:
                    sc.modifyAllBuilders((e, i) -> e.setHitCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case RightCount:
                    sc.modifyAllBuilders((e, i) -> e.setRightCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
            }
        }
        skill.setExtra(val, upVal);
    }

    public void changeScopeForSkill(PSkill<?> skill, int val, int upVal) {
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getScopeSource()) {
                case Damage:
                    sc.modifyAllBuilders((e, i) -> e.setDamageForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case Block:
                    sc.modifyAllBuilders((e, i) -> e.setBlockForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case MagicNumber:
                    sc.modifyAllBuilders((e, i) -> e.setMagicNumberForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case SecondaryNumber:
                    sc.modifyAllBuilders((e, i) -> e.setHpForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case HitCount:
                    sc.modifyAllBuilders((e, i) -> e.setHitCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
                case RightCount:
                    sc.modifyAllBuilders((e, i) -> e.setRightCountForForm(sc.currentBuilder, sc.currentBuilder + 1, val, upVal));
                    return;
            }
        }
        skill.setScope(val, upVal);
    }

    public void close() {
        editor.currentEditingSkill = null;
    }

    public int getAmountForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getAmountSource()) {
                case Damage:
                    return sc.getBuilder().getDamage(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlock(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumber(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHp(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCount(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCount(sc.currentBuilder);
            }
        }
        return skill.amount;
    }

    public int getAmountUpgradeForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getAmountSource()) {
                case Damage:
                    return sc.getBuilder().getDamageUpgrade(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlockUpgrade(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumberUpgrade(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHpUpgrade(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCountUpgrade(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCountUpgrade(sc.currentBuilder);
            }
        }
        return skill.getUpgrade();
    }

    protected ArrayList<AbstractCard> getAvailableCards() {
        return PCLCustomEditEntityScreen.getAvailableCards(getColor());
    }

    protected ArrayList<AbstractPotion> getAvailablePotions() {
        return PCLCustomEditEntityScreen.getAvailablePotions(getColor());
    }

    protected ArrayList<AbstractRelic> getAvailableRelics() {
        return PCLCustomEditEntityScreen.getAvailableRelics(getColor());
    }

    public EditorMaker getBuilder() {
        return editor.screen.getBuilder();
    }

    public AbstractCard.CardColor getColor() {
        return getBuilder().getCardColor();
    }

    public Color getColorForEffect(PSkill<?> effect) {
        return editor.rootEffect == null || effect instanceof PPrimary || editor.rootEffect.isSkillAllowed(effect, editor) ? Color.WHITE : Color.GRAY;
    }

    public int getExtra2ForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getExtra2Source()) {
                case Damage:
                    return sc.getBuilder().getDamage(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlock(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumber(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHp(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCount(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCount(sc.currentBuilder);
            }
        }
        return skill.extra2;
    }

    public int getExtra2UpgradeForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getExtra2Source()) {
                case Damage:
                    return sc.getBuilder().getDamageUpgrade(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlockUpgrade(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumberUpgrade(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHpUpgrade(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCountUpgrade(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCountUpgrade(sc.currentBuilder);
            }
        }
        return skill.getUpgradeExtra2();
    }

    public int getExtraForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getExtraSource()) {
                case Damage:
                    return sc.getBuilder().getDamage(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlock(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumber(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHp(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCount(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCount(sc.currentBuilder);
            }
        }
        return skill.extra;
    }

    public int getExtraUpgradeForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getExtraSource()) {
                case Damage:
                    return sc.getBuilder().getDamageUpgrade(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlockUpgrade(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumberUpgrade(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHpUpgrade(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCountUpgrade(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCountUpgrade(sc.currentBuilder);
            }
        }
        return skill.getUpgradeExtra();
    }

    public int getScopeForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getScopeSource()) {
                case Damage:
                    return sc.getBuilder().getDamage(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlock(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumber(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHp(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCount(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCount(sc.currentBuilder);
            }
        }
        return skill.scope;
    }

    public int getScopeUpgradeForSkill(PSkill<?> skill) {
        if (skill == null) {
            return 0;
        }
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            switch (skill.getScopeSource()) {
                case Damage:
                    return sc.getBuilder().getDamageUpgrade(sc.currentBuilder);
                case Block:
                    return sc.getBuilder().getBlockUpgrade(sc.currentBuilder);
                case MagicNumber:
                    return sc.getBuilder().getMagicNumberUpgrade(sc.currentBuilder);
                case SecondaryNumber:
                    return sc.getBuilder().getHpUpgrade(sc.currentBuilder);
                case HitCount:
                    return sc.getBuilder().getHitCountUpgrade(sc.currentBuilder);
                case RightCount:
                    return sc.getBuilder().getRightCountUpgrade(sc.currentBuilder);
            }
        }
        return skill.getUpgradeScope();
    }

    public String getSmartSearchableLabel(TooltipProvider item) {
        EUITooltip tip = item.getTooltip();
        return tip instanceof EUIKeywordTooltip && ((EUIKeywordTooltip) tip).icon != null ? tip.getTitleOrIconForced() + " " + tip.title : tip.title;
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
    public EUITourTooltip[] getTour() {
        return new EUITourTooltip[0];
    }

    public <T> EUIDropdown<T> initializeRegular(T[] items, FuncT1<String, T> labelFunc, String title, boolean multiselect) {
        return initializeRegular(Arrays.asList(items), labelFunc, title, multiselect);
    }

    public <T> EUIDropdown<T> initializeRegular(Collection<T> items, FuncT1<String, T> labelFunc, String title, boolean multiselect) {
        return new EUIDropdown<T>(new OriginRelativeHitbox(hb, MENU_WIDTH * 1.35f, MENU_HEIGHT, 0, 0))
                .setLabelFunctionForOption(labelFunc, false)
                .setIsMultiSelect(multiselect)
                .setShouldPositionClearAtTop(true)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, title)
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
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(items);
    }

    protected void initializeSelectors() {
        final AbstractCard.CardColor cardColor = getColor();
        String cetutString = editor.screen instanceof PCLCustomPowerEditScreen ? PGR.core.strings.cetut_amountPower : PGR.core.strings.cetut_amount;
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
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, node.type.getTitle())
                .setItems(node.getEffects());
        effects.sortByLabel();
        effects.setActive(effects.size() > 1);

        float startX = effects.isActive ? effects.hb.width + MENU_WIDTH * 0.2f : 0;
        valueEditor = new PCLCustomUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f, MENU_HEIGHT, startX, OFFSET_AMOUNT)
                , EUIRM.strings.ui_amount, (val, upVal) -> {
            if (node.skill != null) {
                changeAmountForSkill(node.skill, val, upVal);
                editor.updateRootEffect();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX)
                .setTooltip(EUIRM.strings.ui_amount, cetutString);
        startX += MENU_WIDTH * 0.55f;
        extraEditor = new PCLCustomUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f, MENU_HEIGHT, startX, OFFSET_AMOUNT)
                , PGR.core.strings.cedit_extraValue, (val, upVal) -> {
            if (node.skill != null) {
                changeExtraForSkill(node.skill, val, upVal);
                editor.updateRootEffect();
            }
        })
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_extraValue, cetutString);
        startX += MENU_WIDTH * 0.55f;
        scopeEditor = new PCLCustomUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f, MENU_HEIGHT, startX, OFFSET_AMOUNT)
                , PGR.core.strings.cedit_scope, (val, upVal) -> {
            if (node.skill != null) {
                changeScopeForSkill(node.skill, val, upVal);
                editor.updateRootEffect();
            }
        })
                .setLimits(1, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_scope, PGR.core.strings.cetut_scope);
        startX += MENU_WIDTH * 0.55f;
        upgradeEditor = new PCLCustomUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f, MENU_HEIGHT, startX, OFFSET_AMOUNT)
                , PGR.core.strings.cedit_extraValue, (val, upVal) -> {
                    if (node.skill != null) {
                        changeExtra2ForSkill(node.skill, val, upVal);
                        editor.updateRootEffect();
                    }
                })
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.cedit_upgrades, cetutString);

        targets = new EUIDropdown<>(new OriginRelativeHitbox(hb, MENU_WIDTH, MENU_HEIGHT, AUX_OFFSET, 0)
                , PCLCardTarget::getTitle)
                .setOnChange(this::modifyTargets)
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_cardTarget)
                .setCanAutosize(true, true)
                .setItems(PCLCustomCardAttributesPage.getEligibleTargets(cardColor))
                .setTooltip(PGR.core.strings.cedit_cardTarget, PGR.core.strings.cetut_effectTarget);

        destinations = initializeRegular(PCLCardSelection.values(), PCLCardSelection::getTitle, PGR.core.strings.cedit_destinations, false)
                .setTooltip(PGR.core.strings.cedit_destinations, PGR.core.strings.cetut_destination);
        origins = initializeRegular(PCLCardSelection.values(), PCLCardSelection::getTitle, PGR.core.strings.cedit_origins, false)
                .setTooltip(PGR.core.strings.cedit_origins, PGR.core.strings.cetut_origin);
        piles = initializeRegular(PCLCardGroupHelper.getStandard(), PCLCardGroupHelper::getCapitalTitle, PGR.core.strings.cedit_pile, true)
                .setLabelFunctionForButton((list, __) -> list.isEmpty() ? StringUtils.capitalize(PGR.core.strings.subjects_thisCard()) : piles.makeMultiSelectString(), false)
                .setTooltip(PGR.core.strings.cedit_pile, PGR.core.strings.cetut_pile);
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
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(items);
        dropdown.setLabelFunctionForButton((list, __) -> dropdown.makeMultiSelectString(item -> item.getTooltip().getTitleOrIcon()), true);
        return dropdown;
    }

    protected void modifyTargets(List<PCLCardTarget> targets) {
        if (node.skill != null && !targets.isEmpty()) {
            node.skill.setTarget(targets.get(0));
            if (shouldOverrideTarget) {
                if (editor.screen instanceof PCLCustomCardEditScreen) {
                    ((PCLCustomCardEditScreen)editor.screen).modifyBuilder(e -> e.setTarget(targets.get(0)));
                    for (PCLCustomGenericPage page : editor.screen.primaryPages) {
                        if (page != editor) {
                            page.refresh();
                        }
                    }
                }
            }
            editor.updateRootEffect();
        }
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
        int e2Min = data != null ? data.minExtra2 : 0;
        int e2Max = data != null ? data.maxExtra2 : 0;

        effects.setSelection(node.skill, false);
        valueEditor
                .setLimits(min, max)
                .setValue(getAmountForSkill(node.skill), getAmountUpgradeForSkill(node.skill), false)
                .setActive(min != max);
        extraEditor
                .setLimits(eMin, eMax)
                .setValue(getExtraForSkill(node.skill), getExtraUpgradeForSkill(node.skill), false)
                .setActive(eMin != eMax);
        scopeEditor
                .setLimits(1, PSkill.DEFAULT_MAX)
                .setValue(getScopeForSkill(node.skill), getScopeUpgradeForSkill(node.skill), false)
                .setActive(node.skill != null && node.skill.target.targetsRandomOrAny());
        upgradeEditor
                .setLimits(e2Min, e2Max)
                .setValue(getExtra2ForSkill(node.skill), getExtra2UpgradeForSkill(node.skill), false)
                .setActive(e2Min != e2Max);
        if (node.skill != null && lastEffect != node.skill) {
            lastEffect = node.skill;
            shouldOverrideTarget = node.skill.shouldOverrideTarget();
            activeElements.clear();
            valueEditor.setHeaderText(node.skill.getHeaderTextForAmount()).setOnTab(() -> {
                if (extraEditor.isActive) {
                    extraEditor.start();
                }
                else {
                    scopeEditor.start();
                }
            });
            extraEditor.setHeaderText(node.skill.getHeaderTextForExtra()).setOnTab(() -> scopeEditor.start());
            scopeEditor.setHeaderText(node.skill.getHeaderTextForScope());
            upgradeEditor.setHeaderText(node.skill.getHeaderTextForExtra2());
            targets
                    .setItems(PSkill.getEligibleTargets(node.skill))
                    .setActive(targets.getAllItems().size() > 1);
            piles.setItems(PSkill.getEligiblePiles(node.skill))
                    .setActive(piles.getAllItems().size() > 1);
            destinations.setItems(PSkill.getEligibleDestinations(node.skill))
                    .setActive(destinations.getAllItems().size() > 1);
            origins.setItems(PSkill.getEligibleOrigins(node.skill))
                    .setActive(origins.getAllItems().size() > 1);
            piles.setItems(PSkill.getEligiblePiles(node.skill))
                    .setActive(!piles.getAllItems().isEmpty());
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
            scopeEditor.setHeaderText(PGR.core.strings.cedit_scope);
            upgradeEditor.setHeaderText(PGR.core.strings.cedit_upgrades);
        }
        else {
            shouldOverrideTarget = node.skill.shouldOverrideTarget();
        }
    }

    public void registerAffinity(List<PCLAffinity> items) {
        registerAffinity(items, PGR.core.tooltips.affinityGeneral.title);
    }

    public void registerAffinity(List<PCLAffinity> items, String title) {
        registerDropdown(initializeSmartSearchable(PCLCustomCardAttributesPage.getEligibleAffinities(getColor()), title), items);
    }

    public void registerAugment(List<String> augmentIDs) {
        registerDropdown(initializeSearchable(PCLCustomEditEntityScreen.getAvailableAugments(), PCLAugmentData::getName, PGR.core.tooltips.augment.title),
                augments -> {
                    augmentIDs.clear();
                    augmentIDs.addAll(EUIUtils.mapAsNonnull(augments, t -> t.ID));
                },
                augmentIDs,
                augment -> augment.ID
        );
    }

    public void registerAugmentCategory(List<PCLAugmentCategory> categories) {
        registerDropdown(initializeSearchable(PCLAugmentCategory.values(), PCLAugmentCategory::getName, PGR.core.strings.augment_category), categories)
                .setTooltip(PGR.core.strings.augment_category, PGR.core.strings.cetut_augmentCategory);
    }

    public void registerBlight(List<String> blightIDs) {
        registerDropdown(initializeSearchable(PCLCustomEditEntityScreen.getAvailableBlights(), blight -> blight.name, StringUtils.capitalize(PGR.core.strings.subjects_blight)),
                blights -> {
                    blightIDs.clear();
                    blightIDs.addAll(EUIUtils.mapAsNonnull(blights, t -> t.blightID));
                },
                blightIDs,
                blight -> blight.blightID
        );
    }

    public <V> void registerBlight(List<String> blightIDs, ActionT1<List<AbstractBlight>> onChangeImpl) {
        registerDropdown(initializeSearchable(PCLCustomEditEntityScreen.getAvailableBlights(), blight -> blight.name, StringUtils.capitalize(PGR.core.strings.subjects_blight)),
                onChangeImpl,
                blightIDs,
                blight -> blight.blightID
        );
    }

    public void registerBlightRarity(List<BlightTier> items) {
        registerDropdown(initializeSearchable(BlightTier.values(), BlightTier::getName, CardLibSortHeader.TEXT[0]), items)
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_blightRarity);
    }

    public void registerBoolean(String title, ActionT1<Boolean> onChange, boolean initial) {
        registerBoolean(title, null, onChange, initial);
    }

    public void registerBoolean(String title, String desc, ActionT1<Boolean> onChange, boolean initial) {
        float predictLength = EUITextHelper.getSmartWidth(FontHelper.cardDescFont_N, title, Settings.WIDTH, 0f);
        registerBoolean(new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH * 0.2f + predictLength, MENU_HEIGHT, MENU_WIDTH, 0))
                        .setFont(FontHelper.cardDescFont_N, 0.9f)
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
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, title)
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
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, title)
                .setCanAutosize(true, true)
                .setItems(possibleItems);
        return registerDropdown(dropdown, onChangeImpl, selectedItems);
    }

    public void registerFlag(List<String> cardIDs) {
        registerDropdown(initializeSearchable(CardFlag.getAll(), CardFlag::getName, PGR.core.strings.cedit_flags),
                cards -> {
                    cardIDs.clear();
                    cardIDs.addAll(EUIUtils.mapAsNonnull(cards, t -> t.ID));
                },
                cardIDs,
                card -> card.ID
        )
                .setTooltip(PGR.core.strings.cedit_flags, "");
    }

    public <V> void registerFlag(List<String> cardIDs, ActionT1<List<CardFlag>> onChangeImpl) {
        registerDropdown(initializeSearchable(CardFlag.getAll(), CardFlag::getName, PGR.core.strings.cedit_flags),
                onChangeImpl,
                cardIDs,
                card -> card.ID
        )
                .setTooltip(PGR.core.strings.cedit_flags, "");
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
        )
                .setTooltip(PGR.core.strings.sui_seriesUI, "");
    }

    public <V> void registerLoadout(List<String> cardIDs, ActionT1<List<PCLLoadout>> onChangeImpl) {
        registerDropdown(initializeSearchable(PCLLoadout.getAll(getColor()), PCLLoadout::getName, PGR.core.strings.sui_seriesUI),
                onChangeImpl,
                cardIDs,
                card -> card.ID
        )
                .setTooltip(PGR.core.strings.sui_seriesUI, "");
    }

    public void registerOrb(List<String> orbIDs) {
        registerDropdown(initializeSmartSearchable(PCLCustomEditEntityScreen.getAvailableOrbs(), PGR.core.tooltips.orb.title, this::getSmartSearchableLabel),
                powers -> {
                    orbIDs.clear();
                    orbIDs.addAll(EUIUtils.mapAsNonnull(powers, t -> t.ID));
                },
                orbIDs,
                power -> power.ID
        ).setTooltip(PGR.core.tooltips.orb);
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
        registerDropdown(initializeSearchable(AbstractPotion.PotionRarity.values(), EUIGameUtils::textForPotionRarity, CardLibSortHeader.TEXT[0]), items)
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_potionRarity);
    }

    public void registerPotionSize(List<AbstractPotion.PotionSize> items) {
        registerDropdown(initializeSearchable(AbstractPotion.PotionSize.values(), EUIGameUtils::textForPotionSize, EUIRM.strings.potion_size), items)
                .setTooltip(EUIRM.strings.potion_size, PGR.core.strings.cetut_potionSize);
    }

    public void registerPower(List<String> powerIDs) {
        registerDropdown(initializeSmartSearchable(PCLCustomEditEntityScreen.getAvailablePowers(), PGR.core.strings.cedit_powers, this::getSmartSearchableLabel),
                powers -> {
                    powerIDs.clear();
                    powerIDs.addAll(EUIUtils.mapAsNonnull(powers, t -> t.ID));
                },
                powerIDs,
                power -> power.ID
        );
    }

    public void registerRarity(List<AbstractCard.CardRarity> items) {
        registerDropdown(initializeSearchable(AbstractCard.CardRarity.values(), EUIGameUtils::textForRarity, CardLibSortHeader.TEXT[0]), items)
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_rarity);
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
        registerDropdown(initializeSearchable(AbstractRelic.RelicTier.values(), EUIGameUtils::textForRelicTier, CardLibSortHeader.TEXT[0]), items)
                .setTooltip(CardLibSortHeader.TEXT[0], PGR.core.strings.cetut_relicRarity);
    }

    public void registerStance(List<PCLStanceHelper> items) {
        registerDropdown(initializeSmartSearchable(PCLStanceHelper.getAll(getColor()), PGR.core.tooltips.stance.title), items)
                .setTooltip(PGR.core.tooltips.stance);
    }

    public void registerTag(List<PCLCardTag> items) {
        registerTag(items, PGR.core.strings.cedit_tags);
    }

    public void registerTag(List<PCLCardTag> items, String title) {
        registerDropdown(initializeSmartSearchable(PCLCardTag.getAll(), title), items)
                .setTooltip(title, PGR.core.strings.cetut_attrTags1);
    }

    public void registerType(List<AbstractCard.CardType> items) {
        registerDropdown(initializeSearchable(AbstractCard.CardType.values(), EUIGameUtils::textForType, CardLibSortHeader.TEXT[1]), items)
                .setTooltip(CardLibSortHeader.TEXT[1], PGR.core.strings.cetut_type);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.backdrop.tryRender(sb);
        this.effects.tryRender(sb);
        this.targets.tryRender(sb);
        this.valueEditor.tryRender(sb);
        this.extraEditor.tryRender(sb);
        this.scopeEditor.tryRender(sb);
        this.upgradeEditor.tryRender(sb);
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
        this.scopeEditor.tryUpdate();
        this.upgradeEditor.tryUpdate();
        for (EUIHoverable element : activeElements) {
            element.tryUpdate();
        }
        if (EUIInputManager.leftClick.isJustPressed() && !wasBusy &&
                !backdrop.hb.hovered && !effects.areAnyItemsHovered()
                && !targets.areAnyItemsHovered() && !valueEditor.hb.hovered
                && !extraEditor.hb.hovered && !scopeEditor.hb.hovered && !upgradeEditor.hb.hovered
                && !EUIUtils.any(activeElements, e -> e.hb.hovered)) {
            close();
        }
    }
}
