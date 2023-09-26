package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.ui.cardFilter.*;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.powers.EUIExporterPCLPowerRow;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PCLPowerRenderable;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCLPowerKeywordFilters extends GenericFilters<PCLPowerRenderable, PCLPowerKeywordFilters.PowerFilters, CustomFilterModule<PCLPowerRenderable>> {
    public static final ArrayList<CustomFilterModule<PCLPowerRenderable>> globalFilters = new ArrayList<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<PCLPowerData.Behavior> endTurnBehaviorDropdown;
    public final EUIDropdown<AbstractPower.PowerType> typeDropdown;
    public final EUIDropdown<Integer> priorityDropdown;

    public PCLPowerKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        endTurnBehaviorDropdown = new EUIDropdown<PCLPowerData.Behavior>(new EUIHitbox(0, 0, scale(240), scale(48))
                , PCLPowerData.Behavior::getText)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentEndTurnBehaviors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.power_turnBehavior)
                .setItems(PCLPowerData.Behavior.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);

        typeDropdown = new EUIDropdown<AbstractPower.PowerType>(new EUIHitbox(0, 0, scale(240), scale(48))
                , GameUtilities::textForPowerType)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentTypes, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[1])
                .setItems(AbstractPower.PowerType.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);

        priorityDropdown = new EUIDropdown<Integer>(new EUIHitbox(0, 0, scale(240), scale(48))
                , String::valueOf)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentPriorities, costs))
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

    public static int rankByEndTurnBehavior(PCLPowerRenderable a, PCLPowerRenderable b) {
        return (a == null ? -1 : b == null ? 1 :(a.power.endTurnBehavior.ordinal() - b.power.endTurnBehavior.ordinal()));
    }
    public static int rankByName(PCLPowerRenderable a, PCLPowerRenderable b) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.power.getName(), b.power.getName()));
    }

    public static int rankByPriority(PCLPowerRenderable a, PCLPowerRenderable b) {
        return (a == null ? -1 : b == null ? 1 : (a.power.priority - b.power.priority));
    }

    public static int rankByType(PCLPowerRenderable a, PCLPowerRenderable b) {
        return (a == null ? -1 : b == null ? 1 : (a.power.type.ordinal() - b.power.type.ordinal()));
    }

    @Override
    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        super.clear(shouldInvoke, shouldClearColors);
        originsDropdown.setSelectionIndices((int[]) null, false);
        endTurnBehaviorDropdown.setSelectionIndices((int[]) null, false);
        typeDropdown.setSelectionIndices((int[]) null, false);
        priorityDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
    }

    @Override
    public void cloneFrom(PowerFilters filters) {
        originsDropdown.setSelection(filters.currentOrigins, true);
        endTurnBehaviorDropdown.setSelection(filters.currentEndTurnBehaviors, true);
        typeDropdown.setSelection(filters.currentTypes, true);
        priorityDropdown.setSelection(filters.currentPriorities, true);
    }

    public boolean evaluate(PCLPowerRenderable c) {
        //Name check
        if (filters.currentName != null && !filters.currentName.isEmpty()) {
            String name = getNameForSort(c);
            if (name == null || !name.toLowerCase().contains(filters.currentName.toLowerCase())) {
                return false;
            }
        }

        //Description check
        if (filters.currentDescription != null && !filters.currentDescription.isEmpty()) {
            String desc = getDescriptionForSort(c);
            if (desc == null || !desc.toLowerCase().contains(filters.currentDescription.toLowerCase())) {
                return false;
            }
        }

        //Origin check
        if (!evaluateItem(filters.currentOrigins, EUIGameUtils.getModInfo(c))) {
            return false;
        }

        //Category check
        if (!evaluateItem(filters.currentPriorities, c.power.priority)) {
            return false;
        }

        //Category check
        if (!evaluateItem(filters.currentEndTurnBehaviors, c.power.endTurnBehavior)) {
            return false;
        }

        //Category check
        if (!evaluateItem(filters.currentTypes, c.power.type)) {
            return false;
        }

        //Tooltips check
        if (!filters.currentFilters.isEmpty() && (!getAllTooltips(c).containsAll(filters.currentFilters))) {
            return false;
        }

        //Negate Tooltips check
        if (!filters.currentNegateFilters.isEmpty() && (EUIUtils.any(getAllTooltips(c), filters.currentNegateFilters::contains))) {
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

    @Override
    public float getFirstY() {
        return group.group.get(0).currentY;
    }

    @Override
    public void defaultSort() {
        this.group.sort(PCLPowerKeywordFilters::rankByName);
        this.group.sort(PCLPowerKeywordFilters::rankByType);
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<PCLPowerRenderable> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<Integer> availablePriorities = new HashSet<>();
        if (originalGroup != null) {
            currentTotal = originalGroup.size();
            for (PCLPowerRenderable augment : originalGroup) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(augment)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(augment));
                availablePriorities.add(augment.power.priority);
            }
            doForFilters(m -> m.initializeSelection(originalGroup));
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
    protected void setupSortHeader(FilterSortHeader header, float startX) {
        
        startX = makeToggle(header, PCLPowerKeywordFilters::rankByType, CardLibSortHeader.TEXT[1], startX);
        startX = makeToggle(header, PCLPowerKeywordFilters::rankByName, CardLibSortHeader.TEXT[2], startX);
        startX = makeToggle(header, PCLPowerKeywordFilters::rankByEndTurnBehavior, PGR.core.strings.power_turnBehavior, startX);
        startX = makeToggle(header, PCLPowerKeywordFilters::rankByPriority, PGR.core.strings.power_priority, startX);
    }

    @Override
    public EUIExporter.Exportable<PCLPowerRenderable> getExportable() {
        return EUIExporterPCLPowerRow.powerExportable;
    }

    @Override
    protected PowerFilters getFilterObject() {
        return new PowerFilters();
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

    public static class PowerFilters extends GenericFiltersObject {
        public final HashSet<PCLPowerData.Behavior> currentEndTurnBehaviors = new HashSet<>();
        public final HashSet<Integer> currentPriorities = new HashSet<>();
        public final HashSet<AbstractPower.PowerType> currentTypes = new HashSet<>();

        public void clear(boolean shouldClearColors) {
            super.clear(shouldClearColors);
            currentEndTurnBehaviors.clear();
            currentPriorities.clear();
            currentTypes.clear();
        }

        public void cloneFrom(PowerFilters other) {
            super.cloneFrom(other);
            EUIUtils.replaceContents(currentEndTurnBehaviors, other.currentEndTurnBehaviors);
            EUIUtils.replaceContents(currentPriorities, other.currentPriorities);
            EUIUtils.replaceContents(currentTypes, other.currentTypes);
        }

        public boolean isEmpty() {
            return super.isEmpty() && currentEndTurnBehaviors.isEmpty()
                    && currentPriorities.isEmpty() && currentTypes.isEmpty();
        }
    }
}
