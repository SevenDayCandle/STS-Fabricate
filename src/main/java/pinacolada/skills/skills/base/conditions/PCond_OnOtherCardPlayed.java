package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.OnCardPlayedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PCond_OnOtherCardPlayed extends PCond_Delegate implements OnCardPlayedSubscriber
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnOtherCardPlayed.class, PField_CardCategory.class, 1, 1)
            .selfTarget();

    public PCond_OnOtherCardPlayed()
    {
        super(DATA);
    }

    public PCond_OnOtherCardPlayed(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public void onCardPlayed(AbstractCard card)
    {
        triggerOnCard(card);
    }

    @Override
    public String getDelegateSampleText() {return TEXT.subjects_playingXWith(TEXT.subjects_x, TEXT.cpile_hand);}

    @Override
    public String getDelegateText() {return TEXT.subjects_playingXWith(fields.getFullCardString(), TEXT.cpile_hand);}

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.play;
    }
}
