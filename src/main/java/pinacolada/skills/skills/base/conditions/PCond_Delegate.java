package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.PTrigger;

public abstract class PCond_Delegate extends PPassiveCond<PField_CardCategory>
{
    public PCond_Delegate(PSkillData<PField_CardCategory> data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public PCond_Delegate(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_onGeneric(getDelegateSampleText());
    }

    @Override
    public String getSubText()
    {
        if (hasParentType(PTrigger.class))
        {
            return TEXT.cond_whenObjectIs(fields.getFullCardString(1), getDelegatePastText());
        }
        return TEXT.cond_onGeneric(getDelegateText());
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

    public void triggerOnCard(AbstractCard c)
    {
        if (fields.getFullCardFilter().invoke(c))
        {
            useFromTrigger(makeInfo(null).setData(EUIUtils.list(c)));
        }
    }

    public void triggerOnCard(AbstractCard c, AbstractCreature target)
    {
        if (fields.getFullCardFilter().invoke(c))
        {
            useFromTrigger(makeInfo(target).setData(EUIUtils.list(c)));
        }
    }

    public String getDelegatePastText() {return getDelegateTooltip().past();}

    public String getDelegateSampleText() {return getDelegateText();}

    public String getDelegateText() {return getDelegateTooltip().title;}

    public abstract EUITooltip getDelegateTooltip();
}
