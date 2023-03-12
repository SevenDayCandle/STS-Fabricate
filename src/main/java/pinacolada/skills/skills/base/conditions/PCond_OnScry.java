package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardScrySubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PCond_OnScry extends PCond_Delegate implements OnCardScrySubscriber
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnScry.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnScry()
    {
        super(DATA);
    }

    public PCond_OnScry(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public void onScry(AbstractCard card)
    {
        triggerOnCard(card);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.scry;
    }
}
