package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardPurgedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnPurge extends PDelegateCardCond implements OnCardPurgedSubscriber
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnPurge.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnPurge()
    {
        super(DATA);
    }

    public PCond_OnPurge(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public void onPurge(AbstractCard card)
    {
        triggerOnCard(card);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.purge;
    }
}
