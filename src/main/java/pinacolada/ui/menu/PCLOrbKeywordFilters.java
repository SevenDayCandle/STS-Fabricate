package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.ui.cardFilter.FilterKeywordButton;
import extendedui.ui.cardFilter.FilterSortHeader;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.GenericFiltersObject;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.orbs.EUIExporterPCLOrbRow;
import pinacolada.resources.PGR;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.ui.PCLOrbRenderable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCLOrbKeywordFilters extends GenericFilters<PCLOrbRenderable, PCLOrbKeywordFilters.OrbFilters, CustomFilterModule<PCLOrbRenderable>> {
    public static final ArrayList<CustomFilterModule<PCLOrbRenderable>> globalFilters = new ArrayList<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<DelayTiming> timingDropdown;

    public PCLOrbKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        timingDropdown = new EUIDropdown<DelayTiming>(new EUIHitbox(0, 0, scale(240), scale(48))
                , DelayTiming::getTitle)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentTimings, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.power_turnBehavior)
                .setItems(DelayTiming.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    public static String getDescriptionForSort(PCLOrbRenderable c) {
        return c.item.getText();
    }

    public static String getNameForSort(PCLOrbRenderable c) {
        return c.item.getName();
    }

    public static int rankByName(PCLOrbRenderable a, PCLOrbRenderable b) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.item.getName(), b.item.getName()));
    }

    public static int rankByTiming(PCLOrbRenderable a, PCLOrbRenderable b) {
        return (a == null ? -1 : b == null ? 1 : (a.item.timing.ordinal() - b.item.timing.ordinal()));
    }

    @Override
    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        super.clear(shouldInvoke, shouldClearColors);
        originsDropdown.setSelectionIndices((int[]) null, false);
        timingDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
    }

    @Override
    public void cloneFrom(OrbFilters filters) {
        originsDropdown.setSelection(filters.currentOrigins, true);
        timingDropdown.setSelection(filters.currentTimings, true);
    }

    @Override
    public void defaultSort() {
        this.group.sort(PCLOrbKeywordFilters::rankByName, PCLOrbKeywordFilters::rankByTiming);
    }

    public boolean evaluate(PCLOrbRenderable c) {
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
        if (!evaluateItem(filters.currentTimings, c.item.timing)) {
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

    public List<EUIKeywordTooltip> getAllTooltips(PCLOrbRenderable c) {
        return c.getTipsForFilters();
    }

    @Override
    public EUIExporter.Exportable<PCLOrbRenderable> getExportable() {
        return EUIExporterPCLOrbRow.orbExportable;
    }

    @Override
    protected OrbFilters getFilterObject() {
        return new OrbFilters();
    }

    @Override
    public float getFirstY() {
        return group.group.get(0).currentY;
    }

    @Override
    public ArrayList<CustomFilterModule<PCLOrbRenderable>> getGlobalFilters() {
        return globalFilters;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<PCLOrbRenderable> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        HashSet<ModInfo> availableMods = new HashSet<>();
        if (originalGroup != null) {
            currentTotal = originalGroup.size();
            for (PCLOrbRenderable augment : originalGroup) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(augment)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(augment));
            }
            doForFilters(m -> m.initializeSelection(originalGroup));
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);
    }

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || timingDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomFilterModule::isHovered)
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void renderFilters(SpriteBatch sb) {
        originsDropdown.tryRender(sb);
        timingDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);
        doForFilters(m -> m.render(sb));
    }

    @Override
    protected void setupSortHeader(FilterSortHeader header, float startX) {

        startX = makeToggle(header, PCLOrbKeywordFilters::rankByTiming, PGR.core.tooltips.timing.title, startX);
        startX = makeToggle(header, PCLOrbKeywordFilters::rankByName, CardLibSortHeader.TEXT[2], startX);
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        xPos = updateDropdown(timingDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomFilterModule<PCLOrbRenderable>::update);
    }

    public static class OrbFilters extends GenericFiltersObject {
        public final HashSet<DelayTiming> currentTimings = new HashSet<>();

        public void clear(boolean shouldClearColors) {
            super.clear(shouldClearColors);
            currentTimings.clear();
        }

        public void cloneFrom(OrbFilters other) {
            super.cloneFrom(other);
            EUIUtils.replaceContents(currentTimings, other.currentTimings);
        }

        public boolean isEmpty() {
            return super.isEmpty() && currentTimings.isEmpty();
        }
    }
}
