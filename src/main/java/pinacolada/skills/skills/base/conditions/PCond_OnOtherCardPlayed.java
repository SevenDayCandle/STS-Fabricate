package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PCond_OnOtherCardPlayed extends PCond_Delegate
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
    public boolean triggerOnOtherCardPlayed(AbstractCard c)
    {
        return triggerOnCard(c);
    }

    @Override
    public String getDelegateSampleText() {return TEXT.subjects.playingXWith(TEXT.subjects.x, TEXT.cardPile.hand);}

    @Override
    public String getDelegateText() {return TEXT.subjects.playingXWith(fields.getFullCardString(), TEXT.cardPile.hand);}

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.play;
    }
}
