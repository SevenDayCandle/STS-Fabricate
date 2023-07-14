package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIDropdownRow;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTagInfo;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.PCLCustomUpgradableEditor;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCLCustomCardAttributesPage extends PCLCustomGenericPage {
    protected static final float START_X = screenW(0.25f);
    protected static final float PAD_X = AbstractCard.IMG_WIDTH * 0.75f + Settings.CARD_VIEW_PAD_X;
    protected static final float PAD_Y = scale(10);
    public static final int EFFECT_COUNT = 2;
    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.06f);
    protected ArrayList<PCLAffinity> availableAffinities;
    protected PCLCustomCardEditCardScreen screen;
    protected EUILabel header;
    protected EUIDropdown<PCLCardTagInfo> tagsDropdown;
    protected EUIDropdown<PCLCardTarget> targetDropdown;
    protected EUIDropdown<DelayTiming> timingDropdown;
    protected PCLCustomUpgradableEditor costEditor;
    protected PCLCustomUpgradableEditor damageEditor;
    protected PCLCustomUpgradableEditor blockEditor;
    protected PCLCustomUpgradableEditor magicNumberEditor;
    protected PCLCustomUpgradableEditor hpEditor;
    protected PCLCustomUpgradableEditor hitCountEditor;
    protected PCLCustomUpgradableEditor rightCountEditor;
    protected ArrayList<PCLCustomCardAffinityValueEditor> affinityEditors = new ArrayList<>();
    protected EUILabel upgradeLabel;
    protected EUILabel upgradeLabel2;

    public PCLCustomCardAttributesPage(PCLCustomCardEditCardScreen screen) {
        this.screen = screen;
        availableAffinities = getEligibleAffinities(screen.currentSlot.slotColor);

        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(screenW(0.5f), PCLCustomEditEntityScreen.START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontLarge, 0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_attributes);


        tagsDropdown = new EUIDropdown<PCLCardTagInfo>(new EUIHitbox(START_X, screenH(0.8f), MENU_WIDTH * 1.2f, MENU_HEIGHT))
                .setOnChange(tags -> screen.modifyAllBuilders((e, i) -> e.setTags(tags)))
                .setLabelFunctionForOption(item -> item.tag.getTooltip().getTitleOrIcon() + " " + item.tag.getTooltip().title, true)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_tags)
                .setIsMultiSelect(true)
                .setCanAutosize(true, true);
        tagsDropdown.setLabelFunctionForButton((list, __) -> tagsDropdown.makeMultiSelectString(item -> item.tag.getTooltip().getTitleOrIcon()), true)
                .setHeaderRow(new PCLCustomCardTagEditorHeaderRow(tagsDropdown))
                .setRowFunction(PCLCustomCardTagEditorRow::new)
                .setRowWidthFunction((a, b, c) -> a.calculateRowWidth() + MENU_HEIGHT * 6)
                .setItems(EUIUtils.map(PCLCardTag.getAll(), t -> t.make(1, 1)))
                .setTooltip(PGR.core.strings.cedit_tags, EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, PGR.core.strings.cetut_attrTags1, PGR.core.strings.cetut_attrTags2));
        targetDropdown = new EUIDropdown<PCLCardTarget>(new EUIHitbox(tagsDropdown.hb.x + tagsDropdown.hb.width + SPACING_WIDTH / 1.5f, screenH(0.8f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty()) {
                        screen.modifyBuilder(e -> e.setTarget(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_cardTarget)
                .setCanAutosizeButton(true)
                .setItems(getEligibleTargets(screen.getBuilder().cardColor))
                .setTooltip(PGR.core.strings.cedit_cardTarget, PGR.core.strings.cetut_cardTarget);
        timingDropdown = new EUIDropdown<DelayTiming>(new EUIHitbox(targetDropdown.hb.x + targetDropdown.hb.width + SPACING_WIDTH / 1.5f, screenH(0.8f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(targets -> {
                    if (!targets.isEmpty()) {
                        screen.modifyBuilder(e -> e.setTiming(targets.get(0)));
                    }
                })
                .setLabelFunctionForOption(DelayTiming::getTitle, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.tooltips.timing.title)
                .setCanAutosizeButton(true)
                .setItems(DelayTiming.values())
                .setTooltip(PGR.core.tooltips.timing);

        // Number editors
        float curW = START_X;
        upgradeLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(curW, screenH(0.65f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_upgrades)
                .setTooltip(PGR.core.strings.cedit_upgrades, PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        costEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , CardLibSortHeader.TEXT[3], (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setCostsForForm(screen.currentBuilder, screen.tempBuilders.size(), val, upVal)))
                .setLimits(-2, PSkill.DEFAULT_MAX)
                .setTooltip(upgradeLabel.tooltip);
        curW += SPACING_WIDTH;
        damageEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_damage, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setDamageForForm(screen.currentBuilder, screen.tempBuilders.size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(upgradeLabel.tooltip);
        curW += SPACING_WIDTH;
        blockEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.cedit_block, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setBlockForForm(screen.currentBuilder, screen.tempBuilders.size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(upgradeLabel.tooltip);
        curW += SPACING_WIDTH;
        hitCountEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , EUIUtils.format(PGR.core.strings.cedit_hitCount, PGR.core.strings.cedit_damage), (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setHitCountForForm(screen.currentBuilder, screen.tempBuilders.size(), val, upVal)))
                .setLimits(1, PSkill.DEFAULT_MAX)
                .setTooltip(upgradeLabel.tooltip);
        curW += SPACING_WIDTH;
        rightCountEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , EUIUtils.format(PGR.core.strings.cedit_hitCount, PGR.core.strings.cedit_block), (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setRightCountForForm(screen.currentBuilder, screen.tempBuilders.size(), val, upVal)))
                .setLimits(1, PSkill.DEFAULT_MAX)
                .setTooltip(upgradeLabel.tooltip);
        curW += SPACING_WIDTH;
        magicNumberEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.tooltips.counter.title, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setMagicNumberForForm(screen.currentBuilder, screen.tempBuilders.size(), val, upVal)))
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.counter.makeCopy());
        curW += SPACING_WIDTH;
        hpEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.tooltips.hp.title, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setHpForForm(screen.currentBuilder, screen.tempBuilders.size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.hp.makeCopy());
        magicNumberEditor.tooltip.setChildren(upgradeLabel.tooltip);
        hpEditor.tooltip.setChildren(upgradeLabel.tooltip);

        // Affinity editors

        curW = START_X;
        upgradeLabel2 = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(curW, screenH(0.52f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_upgrades)
                .setTooltip(upgradeLabel.tooltip);
        boolean canShowLabels = availableAffinities.size() > 0;
        upgradeLabel2.setActive(canShowLabels);

        curW += SPACING_WIDTH;
        for (PCLAffinity affinity : availableAffinities) {
            affinityEditors.add(new PCLCustomCardAffinityValueEditor(new EUIHitbox(curW, screenH(0.52f), MENU_WIDTH / 4, MENU_HEIGHT)
                    , affinity, (af, val, upVal) -> screen.modifyBuilder(e -> e.setAffinities(af, val, upVal))));
            curW += SPACING_WIDTH;
        }

        refresh();
    }

    public static ArrayList<PCLAffinity> getEligibleAffinities(AbstractCard.CardColor color) {
        ArrayList<PCLAffinity> availableAffinities = new ArrayList<>(PGR.config.showIrrelevantProperties.get() ? Arrays.asList(PCLAffinity.basic()) : PCLAffinity.getAvailableAffinitiesAsList(color, false));
        if (availableAffinities.size() > 0) {
            availableAffinities.add(PCLAffinity.Star);
        }
        return availableAffinities;
    }

    // Colorless/Curse should not be able to see Summon in the card editor
    public static List<PCLCardTarget> getEligibleTargets(AbstractCard.CardColor color) {
        if (GameUtilities.isPCLOnlyCardColor(color) || PGR.config.showIrrelevantProperties.get()) {
            return PCLCardTarget.getAll();
        }
        return EUIUtils.filterInPlace(PCLCardTarget.getAll(), PCLCardTarget::vanillaCompatible);
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourCardAttribute,
                targetDropdown.makeTour(true),
                tagsDropdown.makeTour(true),
                upgradeLabel.makeTour(true));
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorAttribute;
    }

    @Override
    public String getTitle() {
        return header.text;
    }

    @Override
    public void refresh() {
        PCLDynamicCardData builder = screen.getBuilder();
        int form = screen.currentBuilder;
        boolean isSummon = builder.cardType == PCLEnum.CardType.SUMMON;

        costEditor.setValue(builder.getCost(form), builder.getCostUpgrade(form));
        damageEditor.setValue(builder.getDamage(form), builder.getDamageUpgrade(form));
        blockEditor.setValue(builder.getBlock(form), builder.getBlockUpgrade(form));
        hitCountEditor.setValue(builder.getHitCount(form), builder.getHitCountUpgrade(form));
        rightCountEditor.setValue(builder.getRightCount(form), builder.getRightCountUpgrade(form));
        magicNumberEditor.setValue(builder.getMagicNumber(form), builder.getMagicNumberUpgrade(form));
        hpEditor.setValue(builder.getHp(form), builder.getHpUpgrade(form)).setActive(isSummon);

        targetDropdown.setSelection(builder.cardTarget, false);
        timingDropdown.setSelection(builder.timing, false).setActive(isSummon);

        List<PCLCardTagInfo> infos = tagsDropdown.getAllItems();
        ArrayList<Integer> selection = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++) {
            PCLCardTagInfo info = infos.get(i);
            if (builder.tags.containsKey(info.tag)) {
                PCLCardTagInfo other = builder.tags.get(info.tag);
                Integer start = other.get(form);
                Integer upgrade = other.getUpgrade(form);
                if (upgrade == null) {
                    upgrade = start;
                }
                info.set(form, start);
                info.setUpgrade(form, upgrade);
                selection.add(i);
            }
        }
        tagsDropdown.setSelectionIndices(selection, false);
        for (EUIDropdownRow<?> row : tagsDropdown.rows) {
            if (row instanceof PCLCustomCardTagEditorRow) {
                ((PCLCustomCardTagEditorRow) row).setForm(form).forceRefresh();
            }
        }

        for (int i = 0; i < availableAffinities.size(); i++) {
            PCLAffinity a = availableAffinities.get(i);
            affinityEditors.get(i).setValue(builder.affinities.getLevel(a), builder.affinities.getUpgrade(a), false);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        upgradeLabel.tryRender(sb);
        upgradeLabel2.tryRender(sb);
        for (PCLCustomCardAffinityValueEditor aEditor : affinityEditors) {
            aEditor.tryRender(sb);
        }
        costEditor.tryRender(sb);
        damageEditor.tryRender(sb);
        blockEditor.tryRender(sb);
        magicNumberEditor.tryRender(sb);
        hpEditor.tryRender(sb);
        hitCountEditor.tryRender(sb);
        rightCountEditor.tryRender(sb);
        targetDropdown.tryRender(sb);
        timingDropdown.tryRender(sb);
        tagsDropdown.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        costEditor.tryUpdate();
        damageEditor.tryUpdate();
        blockEditor.tryUpdate();
        magicNumberEditor.tryUpdate();
        hpEditor.tryUpdate();
        hitCountEditor.tryUpdate();
        rightCountEditor.tryUpdate();
        targetDropdown.tryUpdate();
        timingDropdown.tryUpdate();
        tagsDropdown.tryUpdate();
        for (PCLCustomCardAffinityValueEditor aEditor : affinityEditors) {
            aEditor.tryUpdate();
        }
        upgradeLabel.tryUpdate();
        upgradeLabel2.tryUpdate();
    }
}
