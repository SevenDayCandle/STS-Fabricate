package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.ScryCards;
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
public class PCond_ScryBranch extends PCond_DoBranch
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_ScryBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PCond_ScryBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_ScryBranch()
    {
        super(DATA);
    }

    public PCond_ScryBranch(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.scry;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return (s, c, i, o, g) -> new ScryCards(s, i);
    }
}
