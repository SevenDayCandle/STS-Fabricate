package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PCond_OnDraw extends PCond_Delegate
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnDraw.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnDraw()
    {
        super(DATA);
    }

    public PCond_OnDraw(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public boolean triggerOnDraw(AbstractCard c)
    {
        return triggerOnCard(c);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.draw;
    }
}
