package pinacolada.skills.skills.special.conditions;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.interfaces.subscribers.OnAllyDeathSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.base.conditions.PCond_Delegate;

public class PCond_OnAllyDeath extends PCond_Delegate implements OnAllyDeathSubscriber
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnAllyDeath.class, PField_CardCategory.class, 1, 1)
            .pclOnly()
            .selfTarget();

    public PCond_OnAllyDeath()
    {
        super(DATA);
    }

    public PCond_OnAllyDeath(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public void onAllyDeath(PCLCard card, PCLCardAlly ally)
    {
        triggerOnCard(card, ally);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.kill;
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return TEXT.cond_whenObjectIs(fields.getFullCardStringSingular(), getDelegatePastText());
        }
        return TEXT.cond_whenSingle(getDelegatePastText());
    }
}
