package pinacolada.skills.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.Collections;

public abstract class PDelegateCardCond extends PDelegateCond<PField_CardCategory>
{
    public PDelegateCardCond(PSkillData<PField_CardCategory> data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public PDelegateCardCond(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        fields.setupEditor(editor);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenAObjectIs(TEXT.subjects_x, getDelegateSampleText()) : TEXT.cond_onGeneric(getDelegateSampleText());
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return TEXT.cond_whenAObjectIs(fields.getFullCardStringSingular(), getDelegatePastText());
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
