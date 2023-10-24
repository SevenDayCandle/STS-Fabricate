package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.CycleCards;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_Cycle extends PMove_DoCard<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Cycle.class, PField_CardCategory.class)
            .noTarget()
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.Hand);

    public PMove_Cycle() {
        this(1, PCLCardGroupHelper.Hand);
    }

    public PMove_Cycle(int amount) {
        this(amount, PCLCardGroupHelper.Hand);
    }

    public PMove_Cycle(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_Cycle(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return (s, c, i, o, g) -> new CycleCards(s, i, o);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.cycle;
    }
}
