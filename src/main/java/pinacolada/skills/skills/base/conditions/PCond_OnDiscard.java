package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PCond_OnDiscard extends PCond_Delegate
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnDiscard.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnDiscard()
    {
        super(DATA);
    }

    public PCond_OnDiscard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public boolean triggerOnDiscard(AbstractCard c)
    {
        return triggerOnCard(c);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.discard;
    }
}
