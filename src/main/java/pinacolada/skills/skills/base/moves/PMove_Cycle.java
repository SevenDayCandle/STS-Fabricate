package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.CycleCards;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Cycle extends PMove_Select
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Cycle.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMove_Cycle()
    {
        this(1);
    }

    public PMove_Cycle(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Cycle(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.cycle;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return (s, c, i, o, g) -> new CycleCards(s, i, o);
    }

    @Override
    public String getSubText()
    {
        return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedString())
                        : EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawString(), fields.getFullCardString());
    }
}
