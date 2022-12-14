package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;

public abstract class PCond_Delegate extends PCond
{

    public PCond_Delegate(PSkillData data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public PCond_Delegate(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.onGeneric(getDelegateSampleText());
    }

    @Override
    public String getSubText()
    {
        if (hasParentType(PTrigger.class))
        {
            return TEXT.conditions.whenObjectIs(getFullCardString(getRawString(EFFECT_CHAR)), getDelegatePastText());
        }
        return TEXT.conditions.onGeneric(getDelegateText());
    }

    // This should not activate the child effect when played normally

    @Override
    public void use(PCLUseInfo info)
    {
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
    }

    @Override
    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fromTrigger;
    }

    public boolean triggerOnCard(AbstractCard c)
    {
        if (getFullCardFilter().invoke(c))
        {
            if (this.childEffect != null)
            {
                this.childEffect.setCards(c);
                this.childEffect.use(makeInfo(null));
            }
            return true;
        }
        return false;
    }

    public String getDelegatePastText() {return getDelegateTooltip().past();};

    public String getDelegateSampleText() {return getDelegateText();};

    public String getDelegateText() {return getDelegateTooltip().title;};

    public abstract EUITooltip getDelegateTooltip();
}
