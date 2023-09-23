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
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentCategorySub;
import pinacolada.augments.PCLAugmentRenderable;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PCLAugmentKeywordFilters extends GenericFilters<PCLAugmentRenderable, PCLAugmentKeywordFilters.AugmentFilters, CustomFilterModule<PCLAugmentRenderable>> {
    public static final ArrayList<CustomFilterModule<PCLAugmentRenderable>> globalFilters = new ArrayList<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<PCLAugmentCategory> categoryDropdown;
    public final EUIDropdown<PCLAugmentCategorySub> subCategoryDropdown;
    public final EUIDropdown<Integer> tierDropdown;

    public PCLAugmentKeywordFilters() {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.ui_basegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_origins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        categoryDropdown = new EUIDropdown<PCLAugmentCategory>(new EUIHitbox(0, 0, scale(240), scale(48))
                , PCLAugmentCategory::getName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentCategories, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.misc_category)
                .setItems(PCLAugmentCategory.values())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);

        subCategoryDropdown = new EUIDropdown<PCLAugmentCategorySub>(new EUIHitbox(0, 0, scale(240), scale(48))
                , PCLAugmentCategorySub::getName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentSubCategories, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.misc_subCategory)
                .setItems(PCLAugmentCategorySub.sortedValues())
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);

        tierDropdown = new EUIDropdown<Integer>(new EUIHitbox(0, 0, scale(240), scale(48))
                , String::valueOf)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(filters.currentTiers, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, PGR.core.strings.misc_tier)
                .setItems(EUIUtils.range(1, 3))
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    public static String getDescriptionForSort(PCLAugmentRenderable c) {
        return c.augment.getText();
    }

    public static String getNameForSort(PCLAugmentRenderable c) {
        return c.augment.getName();
    }

    public static int rankByCategory(PCLAugmentRenderable a, PCLAugmentRenderable b) {
        return (a == null ? -1 : b == null ? 1 :
                (
                        a.augment.data.category == b.augment.data.category ? StringUtils.compare(a.augment.data.categorySub.suffix, b.augment.data.categorySub.suffix) :
                        a.augment.data.category.ordinal() - b.augment.data.category.ordinal()
                ));
    }
    public static int rankByName(PCLAugmentRenderable a, PCLAugmentRenderable b) {
        return (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.augment.getName(), b.augment.getName()));
    }

    public static int rankByTier(PCLAugmentRenderable a, PCLAugmentRenderable b) {
        return (a == null ? -1 : b == null ? 1 : (a.augment.data.tier - b.augment.data.tier));
    }

    @Override
    public void clear(boolean shouldInvoke, boolean shouldClearColors) {
        super.clear(shouldInvoke, shouldClearColors);
        originsDropdown.setSelectionIndices((int[]) null, false);
        categoryDropdown.setSelectionIndices((int[]) null, false);
        subCategoryDropdown.setSelectionIndices((int[]) null, false);
        tierDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        descriptionInput.setLabel("");
    }

    @Override
    public void defaultSort() {
        this.group.sort(PCLAugmentKeywordFilters::rankByName);
        this.group.sort(PCLAugmentKeywordFilters::rankByTier);
        this.group.sort(PCLAugmentKeywordFilters::rankByCategory);
    }

    public boolean evaluate(PCLAugmentRenderable c) {
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
        if (!evaluateItem(filters.currentCategories, c.augment.data.category)) {
            return false;
        }

        //Category check
        if (!evaluateItem(filters.currentSubCategories, c.augment.data.categorySub)) {
            return false;
        }

        //Category check
        if (!evaluateItem(filters.currentTiers, c.augment.data.tier)) {
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

    public List<EUIKeywordTooltip> getAllTooltips(PCLAugmentRenderable c) {
        return c.getTipsForFilters();
    }

    @Override
    public ArrayList<CustomFilterModule<PCLAugmentRenderable>> getGlobalFilters() {
        return globalFilters;
    }

    @Override
    public float getFirstY() {
        return group.group.get(0).currentY;
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<PCLAugmentRenderable> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool) {
        HashSet<ModInfo> availableMods = new HashSet<>();
        int maxTier = 1;
        if (originalGroup != null) {
            currentTotal = originalGroup.size();
            for (PCLAugmentRenderable augment : originalGroup) {
                for (EUIKeywordTooltip tooltip : getAllTooltips(augment)) {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(augment));
                maxTier = Math.max(augment.augment.data.tier, maxTier);
            }
            doForFilters(m -> m.initializeSelection(originalGroup));
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);
        tierDropdown.setItems(EUIUtils.range(1, maxTier));
    }

    @Override
    public boolean isHoveredImpl() {
        return originsDropdown.areAnyItemsHovered()
                || categoryDropdown.areAnyItemsHovered()
                || subCategoryDropdown.areAnyItemsHovered()
                || tierDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || descriptionInput.hb.hovered
                || EUIUtils.any(getGlobalFilters(), CustomFilterModule::isHovered)
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void renderFilters(SpriteBatch sb) {
        originsDropdown.tryRender(sb);
        categoryDropdown.tryRender(sb);
        subCategoryDropdown.tryRender(sb);
        tierDropdown.tryRender(sb);
        nameInput.tryRender(sb);
        descriptionInput.tryRender(sb);
        doForFilters(m -> m.render(sb));
    }

    @Override
    protected void setupSortHeader(FilterSortHeader header, float startX) {
        
        startX = makeToggle(header, PCLAugmentKeywordFilters::rankByCategory, PGR.core.strings.misc_category, startX);
        startX = makeToggle(header, PCLAugmentKeywordFilters::rankByName, CardLibSortHeader.TEXT[2], startX);
        startX = makeToggle(header, PCLAugmentKeywordFilters::rankByTier, PGR.core.strings.misc_tier, startX);
    }

    @Override
    public EUIExporter.Exportable<PCLAugmentRenderable> getExportable() {
        return null;
    }

    @Override
    protected AugmentFilters getFilterObject() {
        return new AugmentFilters();
    }

    @Override
    public void updateFilters() {
        float xPos = updateDropdown(originsDropdown, hb.x - SPACING * 3.65f);
        xPos = updateDropdown(categoryDropdown, xPos);
        xPos = updateDropdown(subCategoryDropdown, xPos);
        xPos = updateDropdown(tierDropdown, xPos);
        nameInput.setPosition(hb.x + SPACING * 5.15f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        descriptionInput.setPosition(nameInput.hb.cX + nameInput.hb.width + SPACING * 2.95f, DRAW_START_Y + scrollDelta - SPACING * 3.8f).tryUpdate();
        doForFilters(CustomFilterModule<PCLAugmentRenderable>::update);
    }


    public static class AugmentFilters extends GenericFiltersObject {
        public final HashSet<PCLAugmentCategory> currentCategories = new HashSet<>();
        public final HashSet<PCLAugmentCategorySub> currentSubCategories = new HashSet<>();
        public final HashSet<Integer> currentTiers = new HashSet<>();

        public void clear(boolean shouldClearColors) {
            super.clear(shouldClearColors);
            currentCategories.clear();
            currentSubCategories.clear();
            currentTiers.clear();
        }

        public void cloneFrom(AugmentFilters other) {
            super.cloneFrom(other);
            EUIUtils.replaceContents(currentCategories, other.currentCategories);
            EUIUtils.replaceContents(currentSubCategories, other.currentSubCategories);
            EUIUtils.replaceContents(currentTiers, other.currentTiers);
        }

        public boolean isEmpty() {
            return super.isEmpty() && currentCategories.isEmpty()
                    && currentSubCategories.isEmpty() && currentTiers.isEmpty();
        }
    }
}
