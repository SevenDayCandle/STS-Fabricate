package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PCond_OnPurge extends PCond_Delegate
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnPurge.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnPurge()
    {
        super(DATA);
    }

    public PCond_OnPurge(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean triggerOnPurge(AbstractCard c)
    {
        return triggerOnCard(c);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.purge;
    }
}
