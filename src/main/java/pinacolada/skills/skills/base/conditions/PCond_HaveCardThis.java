package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PPassiveCond;

import java.util.List;

public abstract class PCond_HaveCardThis extends PPassiveCond<PField_CardGeneric>
{
    public PCond_HaveCardThis(PSkillData<PField_CardGeneric> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PCond_HaveCardThis(PSkillData<PField_CardGeneric> data)
    {
        super(data, PCLCardTarget.None, 1);
    }

    public PCond_HaveCardThis(PSkillData<PField_CardGeneric> data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        int count = sourceCard != null ? EUIUtils.count(getCardPile(),
                c -> c.uuid == sourceCard.uuid) : 0;
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText()
    {
        return fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_thisCard)) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_thisCard));
    }

    @Override
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (fields.not ? (input + "-") : (input + "+"));
    }

    abstract public List<AbstractCard> getCardPile();
    abstract public EUITooltip getActionTooltip();
}
