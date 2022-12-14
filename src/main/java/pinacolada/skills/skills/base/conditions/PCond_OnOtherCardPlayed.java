package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_OnOtherCardPlayed extends PCond_Delegate
{
    public static final PSkillData DATA = register(PCond_OnOtherCardPlayed.class, PCLEffectType.Delegate, 1, 1)
            .selfTarget();

    public PCond_OnOtherCardPlayed()
    {
        super(DATA);
    }

    public PCond_OnOtherCardPlayed(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean triggerOnOtherCardPlayed(AbstractCard c)
    {
        return triggerOnCard(c);
    }

    @Override
    public String getDelegateSampleText() {return TEXT.subjects.playingXWith("X", TEXT.cardPile.hand);};

    @Override
    public String getDelegateText() {return TEXT.subjects.playingXWith(getFullCardString(getRawString(EFFECT_CHAR)), TEXT.cardPile.hand);};

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.play;
    }
}
