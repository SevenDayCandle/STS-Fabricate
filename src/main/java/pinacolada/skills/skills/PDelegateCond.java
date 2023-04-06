package pinacolada.skills.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.Collections;

public abstract class PDelegateCond extends PPassiveNonCheckCond<PField_CardCategory>
{
    public PDelegateCond(PSkillData<PField_CardCategory> data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public PDelegateCond(PSkillData<PField_CardCategory> data, PSkillSaveData content)
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
        if (isWhenClause())
        {
            return TEXT.cond_whenObjectIs(fields.getFullCardStringSingular(), getDelegatePastText());
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
    public boolean canPlay(PCLUseInfo info)
    {
        return true;
    }

    public void triggerOnCard(AbstractCard c)
    {
        if (fields.getFullCardFilter().invoke(c))
        {
            useFromTrigger(makeInfo(null).setData(Collections.singletonList(c)));
        }
    }

    public void triggerOnCard(AbstractCard c, AbstractCreature target)
    {
        if (fields.getFullCardFilter().invoke(c))
        {
            useFromTrigger(makeInfo(target).setData(Collections.singletonList(c)));
        }
    }

    public String getDelegatePastText() {return getDelegateTooltip().past();}

    public String getDelegateSampleText() {return getDelegateText();}

    public String getDelegateText() {return getDelegateTooltip().title;}

    public abstract EUITooltip getDelegateTooltip();
}