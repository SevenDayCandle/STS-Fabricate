package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ScoutCards;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;


@VisibleSkill
public class PCond_ScoutBranch extends PCond_DoBranch
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_ScoutBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PCond_ScoutBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_ScoutBranch()
    {
        super(DATA);
    }

    public PCond_ScoutBranch(int amount)
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
        return PGR.core.tooltips.scout;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return (s, c, i, o, g) -> new ScoutCards(s, i);
    }
}
