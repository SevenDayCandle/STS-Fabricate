package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ExhaustFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PCond_ExhaustTo extends PCond_DoTo
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_ExhaustTo.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.Hand);

    public PCond_ExhaustTo()
    {
        this(1, PCLCardGroupHelper.Hand);
    }

    public PCond_ExhaustTo(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_ExhaustTo(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, PCLCardTarget.None, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.exhaust;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return ExhaustFromPile::new;
    }
}
