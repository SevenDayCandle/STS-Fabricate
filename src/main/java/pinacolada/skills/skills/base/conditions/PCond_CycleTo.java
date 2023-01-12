package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.CycleCards;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PCond_CycleTo extends PCond_DoTo
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_CycleTo.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand);

    public PCond_CycleTo()
    {
        this(1, (PCLCardGroupHelper) null);
    }

    public PCond_CycleTo(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_CycleTo(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, PCLCardTarget.None, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.cycle;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return (s, c, i, g) -> new CycleCards(s, i, fields.random);
    }
}
