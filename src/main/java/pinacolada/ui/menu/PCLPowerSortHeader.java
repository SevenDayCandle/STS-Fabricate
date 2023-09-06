package pinacolada.ui.menu;

import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.GenericSortHeader;
import extendedui.utilities.ItemGroup;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugmentRenderable;
import pinacolada.powers.PCLPowerRenderable;
import pinacolada.resources.PGR;

public class PCLPowerSortHeader extends GenericSortHeader<PCLPowerRenderable> {
    public static PCLPowerSortHeader instance;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton categoryButton;
    protected SortHeaderButton endTurnBehaviorButton;
    protected SortHeaderButton tierButton;

    public PCLPowerSortHeader(ItemGroup<PCLPowerRenderable> group) {
        super(group);
        instance = this;
        float xPosition = START_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.categoryButton = new SortHeaderButton(CardLibSortHeader.TEXT[1], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.endTurnBehaviorButton = new SortHeaderButton(PGR.core.strings.power_turnBehavior, xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.tierButton = new SortHeaderButton(PGR.core.strings.power_priority, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.nameButton, this.categoryButton, this.endTurnBehaviorButton, this.tierButton};
    }

    @Override
    public GenericFilters<PCLPowerRenderable, ?> getFilters() {
        return PGR.powerFilters;
    }

    @Override
    protected float getFirstY() {
        return group.group.get(0).hb.y;
    }

    @Override
    protected void sort(SortHeaderButton button, boolean isAscending) {
        if (button == this.nameButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.power.getName(), b.power.getName())) * (isAscending ? 1 : -1));
        }
        else if (button == this.tierButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (a.power.priority - b.power.priority) * (isAscending ? 1 : -1)));
        }
        else if (button == this.endTurnBehaviorButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (a.power.endTurnBehavior.ordinal() - b.power.endTurnBehavior.ordinal()) * (isAscending ? 1 : -1)));
        }
        else if (button == this.categoryButton) {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (a.power.type.ordinal() - b.power.type.ordinal()) * (isAscending ? 1 : -1)));
        }
        else {
            this.group.sort((a, b) -> (a == null ? -1 : b == null ? 1 : (StringUtils.compare(a.power.ID, b.power.ID) * (isAscending ? 1 : -1))));
        }
    }
}
