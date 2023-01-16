package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

public abstract class PCond_Have extends PCond<PField_CardCategory>
{
    public PCond_Have(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PCond_Have(PSkillData<PField_CardCategory> data)
    {
        super(data, PCLCardTarget.None, 1);
    }

    public PCond_Have(PSkillData<PField_CardCategory> data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        int count = EUIUtils.count(getCardPile(),
                c -> fields.getFullCardFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.forced ^ count >= amount;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardString()));
    }

    @Override
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (fields.forced ? (input + "-") : (input + "+"));
    }

    abstract public List<AbstractCard> getCardPile();
    abstract public EUITooltip getActionTooltip();

}
