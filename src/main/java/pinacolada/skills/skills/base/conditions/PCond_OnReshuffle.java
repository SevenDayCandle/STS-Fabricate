package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardReshuffledSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCardCond;

@VisibleSkill
public class PCond_OnReshuffle extends PDelegateCardCond implements OnCardReshuffledSubscriber
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnReshuffle.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnReshuffle()
    {
        super(DATA);
    }

    public PCond_OnReshuffle(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public void onCardReshuffled(AbstractCard card, CardGroup sourcePile)
    {
        triggerOnCard(card);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.reshuffle;
    }
}
