package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.ui.cardFilter.FilterKeywordButton;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.ItemGroup;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.EUIExporterPCLAugmentRow;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PCLPowerRenderable;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCLPowerKeywordFilters extends GenericFilters<PCLPowerRenderable, CustomFilterModule<PCLPowerRenderable>> {
    public static final ArrayList<CustomFilterModule<PCLPowerRenderable>> globalFilters = new ArrayList<>();
    public final HashSet<ModInfo> currentOrigins = new HashSet<>();
    public final HashSet<PCLPowerData.Behavior> currentEndTurnBehaviors = new HashSet<>();
    public final HashSet<Integer> currentPriorities = new HashSet<>();
    public final HashSet<AbstractPower.PowerType> currentTypes = new HashSet<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<PCLPowerData.Behavior> endTurnBehaviorDropdown;
    public final EUIDropdown<AbstractPower.PowerType> typeDropdown;
    public final EUIDropdown<Integer> priorityDropdown;

    public PCLPowerKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        endTurnBehaviorDropdown = new EUIDropdown<PCLPowerData.Behavior>(new EUIHitbox(0, 0, scale(240), scale(48))
                , PCLPowerData.Behavior::getText)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentEndTurnBehaviors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.power_turnBehavior)
                .setItems(PCLPowerData.Behavior.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);

        typeDropdown = new EUIDropdown<AbstractPower.PowerType>(new EUIHitbox(0, 0, scale(240), scale(48))
                , GameUtilities::textForPowerType)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentTypes, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setItems(AbstractPower.PowerType.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);

        priorityDropdown = new EUIDropdown<Integer>(new EUIHitbox(0, 0, scale(240), scale(48))
                , String::valueOf)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentPriorities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.power_priority)
                .setItems(EUIUtils.range(1, 3))
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    public static String getDescriptionForSort(PCLPowerRenderable c) {
        return c.power.getText();
    }

    public static String getNameForSort(PCLPowerRenderable c) {
        return c.power.getName();
    }

    @Override
    public boolean areFiltersEmpty() {
        return (currentName == null || currentName.isEmpty())
                && (currentDescription == null || currentDescription.isEmpty())
                && currentOrigins.isEmpty() && currentPriorities.isEmpty()
                && currentEndTurnBehaviors.isEmpty() && currentTypes.isEmpty()
                && currentFilters.isEmpty() && currentNegateFilters.isEmpty()
                && EUIUtils.all(getGlobalFilters(), CustomFilterModule::isEmpty);
    }

    @Override
    public void clearFilters(boolean shouldInvoke, boolean shouldClearColors) {
        currentOrigins.clear();
        currentFilters.clear();
        currentNegateFilters.clear();
        currentName = null;
        currentDescription = null;
        originsDropdown.setSelectionIndices((int[]) null, false);
        endTurnBehaviorDropdown.setSelectionIndices((int[]) null, false);
        typeDropdown.setSelectionIndices((int[]) null, false);
        priorityDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
        doForFilters(CustomFilterModule::reset);
    }

    public boolean evaluate(PCLPowerRenderable c) {
        //Name check
        if (currentName != null && !currentName.isEmpty()) {
            String name = getNameForSort(c);
            if (name == null || !name.toLowerCase().contains(currentName.toLowerCase())) {
                return false;
            }
        }

        //Description check
        if (currentDescription != null && !currentDescription.isEmpty()) {
            String desc = getDescriptionForSort(c);
            if (desc == null || !desc.toLowerCase().contains(currentDescription.toLowerCase())) {
                return false;
            }
        }

        //Origin check
        if (!evaluateItem(currentOrigins, EUIGameUtils.getModInfo(c))) {
            return false;
        }

        //Category check
        if (!evaluateItem(currentPriorities, c.power.priority)) {
            return false;
        }

        //Category check
        if (!evaluateItem(currentEndTurnBehaviors, c.power.endTurnBehavior)) {
            return false;
        }

        //Category check
        if (!evaluateItem(currentTypes, c.power.type)) {
            return false;
        }

        //Tooltips check
        if (!currentFilters.isEmpty() && (!getAllTooltips(c).containsAll(currentFilters))) {
            return false;
        }

        //Negate Tooltips check
        if (!currentNegateFilters.isEmpty() && (EUIUtils.any(getAllTooltips(c), currentNegateFilters::contains))) {
            return false;
        }

        //Module check
        return customModule == null || customModule.isItemValid(c);
    }

    public List<EUIKeywordTooltip> getAllTooltips(PCLPowerRenderable c) {
        return c.getTipsForFilters();
    }

    @Override
    public ArrayList<CustomFilterModule<PCLPowerRenderable>> getGlobalFilters() {
        return globalFilters;
    }

    public PCLPowerKeywordFilters initializeForCustomHeader(ItemGroup<PCLPowerRenderable> group, ActionT1<FilterKeywordButton> onClick, AbstractCard.CardColor color, boolean isAccessedFromCardPool, boolean snapToGroup) {
        PGR.powerHeader.setGroup(group).snapToGroup(snapToGroup);
        initialize(button -> {
            PGR.augmentHeader.updateForFilters();
            onClick.invoke(button);
        }, PGR.powerHeader.originalGroup, color, isAccessedFromCardPool);
        PGR.augmentHeader.updateForFilters();
        EUIExporter.exportButton.setOnClick(() -> EUIExporterPCLAugmentRow.augmentExportable.openAndPosition(PGR.augmentHeader.group.group));
        EUI.openFiltersButton.setOnClick(() -> PGR.augmentFilters.toggleFilters());
        return this;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<PCLPowerRenderable> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<Integer> availablePriorities = new HashSet<>();
        if (referenceItems != null) {
            currentTotal = getReferenceCount();
            for (PCLPowerRenderable augment : referenceItems) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(augment)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(augment));
                availablePriorities.add(augment.power.priority);
            }
            doForFilters(m -> m.initializeSelection(referenceItems));
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        ArrayList<Integer> priorities = new ArrayList<>(availablePriorities);
        priorities.sort(Integer::compare);
        originsDropdown.setItems(modInfos);
        priorityDropdown.setItems(priorities);
    }

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || endTurnBehaviorDropdown.areAnyItemsHovered()
                || typeDropdown.areAnyItemsHovered()
                || priorityDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomFilterModule::isHovered)
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void renderFilters(SpriteBatch sb) {
        originsDropdown.tryRender(sb);
        endTurnBehaviorDropdown.tryRender(sb);
        typeDropdown.tryRender(sb);
        priorityDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);
        doForFilters(m -> m.render(sb));
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        xPos = updateDropdown(typeDropdown, xPos);
        xPos = updateDropdown(endTurnBehaviorDropdown, xPos);
        xPos = updateDropdown(priorityDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomFilterModule<PCLPowerRenderable>::update);
    }
}
