package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.ReshuffleFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

import java.util.List;


@VisibleSkill
public class PCond_ReshuffleBranch extends PCond_DoBranch
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_ReshuffleBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.ExhaustPile, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.Hand);

    public PCond_ReshuffleBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_ReshuffleBranch()
    {
        super(DATA);
    }

    public PCond_ReshuffleBranch(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    public PCond_ReshuffleBranch(int amount, List<PCLCardGroupHelper> groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups.toArray(new PCLCardGroupHelper[]{}));
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.reshuffle;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return ReshuffleFromPile::new;
    }
}
