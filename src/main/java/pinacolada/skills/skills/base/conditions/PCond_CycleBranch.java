package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.CycleCards;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PCond_CycleBranch extends PCond_DoBranch
{

    public static final PSkillData<PField_CardCategory> DATA = register(PCond_CycleBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand);

    public PCond_CycleBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_CycleBranch()
    {
        super(DATA);
    }

    public PCond_CycleBranch(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.verbNoun(getActionTitle(), getAmountRawString());
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
}
