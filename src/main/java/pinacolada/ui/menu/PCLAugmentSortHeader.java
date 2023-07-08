package pinacolada.ui.menu;

import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.GenericSortHeader;
import extendedui.utilities.ItemGroup;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugmentRenderable;
import pinacolada.resources.PGR;

public class PCLAugmentSortHeader extends GenericSortHeader<PCLAugmentRenderable> {
    public static PCLAugmentSortHeader instance;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton categoryButton;
    protected SortHeaderButton tierButton;

    public PCLAugmentSortHeader(ItemGroup<PCLAugmentRenderable> group) {
        super(group);
        instance = this;
        float xPosition = START_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.categoryButton = new SortHeaderButton(PGR.core.strings.misc_category, xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.tierButton = new SortHeaderButton(PGR.core.strings.misc_tier, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.nameButton, this.categoryButton, this.tierButton};
    }

    @Override
    protected float getFirstY() {
        return group.group.get(0).hb.y;
    }

    @Override
    protected void sort(SortHeaderButton button, boolean isAscending) {
        if (button == this.nameButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.augment.getName(), b.augment.getName())) * (isAscending ? 1 : -1));
        }
        else if (button == this.tierButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (a.augment.data.tier - b.augment.data.tier) * (isAscending ? 1 : -1)));
        }
        else if (button == this.categoryButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (StringUtils.compare(a.augment.data.categorySub.suffix, b.augment.data.categorySub.suffix) * (isAscending ? 1 : -1))));
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (a.augment.data.category.ordinal() - b.augment.data.category.ordinal()) * (isAscending ? 1 : -1)));
        }
        else {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (StringUtils.compare(a.augment.data.ID, b.augment.data.ID) * (isAscending ? 1 : -1))));
        }
    }

    @Override
    public GenericFilters<PCLAugmentRenderable, ?> getFilters() {
        return PGR.augmentFilters;
    }
}
