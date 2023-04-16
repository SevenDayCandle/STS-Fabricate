package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardCreatedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnCreate extends PDelegateCardCond implements OnCardCreatedSubscriber
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnCreate.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnCreate()
    {
        super(DATA);
    }

    public PCond_OnCreate(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public void onCardCreated(AbstractCard card, boolean startOfBattle)
    {
        triggerOnCard(card);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.create;
    }
}
