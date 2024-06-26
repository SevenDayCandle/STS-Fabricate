package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIDropdownRow;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTagInfo;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.PCLCustomUpgradableEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCLCustomCardAttributesPage extends PCLCustomGenericPage {
    protected static final float START_X = screenW(0.25f);
    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.06f);
    protected ArrayList<PCLAffinity> availableAffinities;
    protected PCLCustomCardEditScreen screen;
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

    public PCLCustomCardAttributesPage(PCLCustomCardEditScreen screen) {
        this.screen = screen;
        availableAffinities = getEligibleAffinities(screen.currentSlot.slotColor);

        this.header = new EUILabel(FontHelper.topPanelAmountFont,
                new EUIHitbox(screenW(0.5f), PCLCustomEditEntityScreen.START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(FontHelper.cardTitleFont, 0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_attributes);


        tagsDropdown = new EUIDropdown<PCLCardTagInfo>(new EUIHitbox(START_X, screenH(0.8f), MENU_WIDTH * 1.2f, MENU_HEIGHT))
                .setOnChange(this::modifyTags)
                .setLabelFunctionForOption(item -> item.tag.getTooltip().getTitleOrIconForced() + " " + item.tag.getTooltip().title, true)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_tags)
                .setIsMultiSelect(true)
                .setCanAutosize(true, true);
        tagsDropdown.setLabelFunctionForButton((list, __) -> tagsDropdown.makeMultiSelectString(item -> item.tag.getTooltip().getTitleOrIcon()), true)
                .setHeaderRow(new PCLCustomCardTagEditorHeaderRow(tagsDropdown))
                .setRowFunction(PCLCustomCardTagEditorRow::new)
                .setRowWidthFunction((a, b, c) -> a.calculateRowWidth() + MENU_HEIGHT * 6)
                .setItems(EUIUtils.map(PCLCardTag.getAll(), t -> t.make(new Integer[]{}, new Integer[]{})))
                .setTooltip(PGR.core.strings.cedit_tags, EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, PGR.core.strings.cetut_attrTags1, PGR.core.strings.cetut_attrTags2));
        targetDropdown = new EUIDropdown<PCLCardTarget>(new EUIHitbox(tagsDropdown.hb.x + tagsDropdown.hb.width + SPACING_WIDTH / 1.5f, screenH(0.8f), MENU_WIDTH, MENU_HEIGHT)
                , item -> StringUtils.capitalize(item.toString().toLowerCase()))
                .setOnChange(this::modifyTargets)
                .setLabelFunctionForOption(PCLCardTarget::getTitle, false)
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.cedit_cardTarget)
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
                .setHeader(FontHelper.topPanelAmountFont, 0.8f, Settings.GOLD_COLOR, PGR.core.tooltips.timing.title)
                .setCanAutosizeButton(true)
                .setItems(DelayTiming.values())
                .setTooltip(PGR.core.tooltips.timing);

        // Number editors
        float curW = START_X;
        upgradeLabel = new EUILabel(FontHelper.topPanelAmountFont,
                new EUIHitbox(curW, screenH(0.65f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(FontHelper.topPanelAmountFont, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_upgrades)
                .setTooltip(PGR.core.strings.cedit_upgrades, PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        costEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , CardLibSortHeader.TEXT[3], (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setCostsForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(-2, PSkill.DEFAULT_MAX)
                .setTooltip(CardLibSortHeader.TEXT[3], PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        damageEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , StringUtils.capitalize(PGR.core.strings.subjects_damage), (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setDamageForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(StringUtils.capitalize(PGR.core.strings.subjects_damage), PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        blockEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.tooltips.block.title, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setBlockForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.block.title, PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        hitCountEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , EUIUtils.format(PGR.core.strings.cedit_hitCount, StringUtils.capitalize(PGR.core.strings.subjects_damage)), (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setHitCountForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(EUIUtils.format(PGR.core.strings.cedit_hitCount, StringUtils.capitalize(PGR.core.strings.subjects_damage)), PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        rightCountEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , EUIUtils.format(PGR.core.strings.cedit_hitCount, PGR.core.tooltips.block.title), (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setRightCountForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(EUIUtils.format(PGR.core.strings.cedit_hitCount, PGR.core.tooltips.block.title), PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        magicNumberEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.tooltips.counter.title, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setMagicNumberForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(-PSkill.DEFAULT_MAX, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.counter.makeCopy());
        curW += SPACING_WIDTH;
        hpEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.65f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.tooltips.hp.title, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setHpForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.hp.makeCopy());
        magicNumberEditor.tooltip.setChildren(upgradeLabel.tooltip);
        hpEditor.tooltip.setChildren(upgradeLabel.tooltip);

        costEditor.setOnTab(() -> damageEditor.start());
        damageEditor.setOnTab(() -> blockEditor.start());
        blockEditor.setOnTab(() -> hitCountEditor.start());
        hitCountEditor.setOnTab(() -> rightCountEditor.start());
        rightCountEditor.setOnTab(() -> magicNumberEditor.start());
        magicNumberEditor.setOnTab(() -> hpEditor.start());

        // Affinity editors

        curW = START_X;
        upgradeLabel2 = new EUILabel(FontHelper.topPanelAmountFont,
                new EUIHitbox(curW, screenH(0.52f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(FontHelper.topPanelAmountFont, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_upgrades)
                .setTooltip(upgradeLabel.tooltip);
        boolean canShowLabels = !availableAffinities.isEmpty();
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
        ArrayList<PCLAffinity> availableAffinities = new ArrayList<>(PCLAffinity.basic());
        availableAffinities.add(PCLAffinity.Star);
        return availableAffinities;
    }

    // Colorless/Curse should not be able to see Summon in the card editor
    public static List<PCLCardTarget> getEligibleTargets(AbstractCard.CardColor color) {
        return PCLCardTarget.getAll();
    }

    @Override
    public TextureCache getTextureCache() {
        return EUIRM.images.tag;
    }

    @Override
    public String getTitle() {
        return header.text;
    }

    @Override
    public EUITourTooltip[] getTour() {
        return EUIUtils.array(
                tagsDropdown.makeTour(true),
                targetDropdown.makeTour(true),
                timingDropdown.makeTour(true),
                upgradeLabel.makeTour(true)
        );
    }

    // Lists start off as empty before they are selected. When they are first selected, set the value for the current form to 1
    protected void modifyTags(List<PCLCardTagInfo> tags) {
        int form = screen.currentBuilder;
        int maxForm = screen.getTempBuilders().size();
        for (PCLCardTagInfo info : tags) {
            if (info.value.length == 0) {
                info.value = new Integer[maxForm];
                Arrays.fill(info.value, 0);
                info.set(form, 1);
            }
            if (info.upgrades.length == 0) {
                info.upgrades = new Integer[maxForm];
                Arrays.fill(info.upgrades, 0);
                info.setUpgrade(form, 1);
            }
        }
        for (EUIDropdownRow<?> row : tagsDropdown.rows) {
            if (row instanceof PCLCustomCardTagEditorRow) {
                ((PCLCustomCardTagEditorRow) row).setForm(form).forceRefresh();
            }
        }
        screen.modifyAllBuilders((e, i) -> e.setTags(tags));
    }

    protected void modifyTargets(List<PCLCardTarget> targets) {
        if (!targets.isEmpty()) {
            screen.modifyBuilder(e -> e.setTarget(targets.get(0)));
            for (PCLCustomGenericPage page : screen.primaryPages) {
                if (page != this) {
                    page.refresh();
                }
            }
        }
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourCardAttribute, getTour());
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
                info.value = other.value.clone();
                info.upgrades = other.upgrades != null ? other.upgrades.clone() : info.value.clone();
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
