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

import java.util.List;

public abstract class PCond_Have extends PCond
{
    public PCond_Have(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Have(PSkillData data)
    {
        super(data, PCLCardTarget.None, 1);
    }

    public PCond_Have(PSkillData data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        int count = EUIUtils.count(getCardPile(),
                c -> getFullCardFilter().invoke(c));
        return amount == 0 ? count == 0 : alt ^ count >= amount;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), getFullCardString(getRawString(EFFECT_CHAR))));
    }

    @Override
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (alt ? (input + "-") : (input + "+"));
    }

    abstract public List<AbstractCard> getCardPile();
    abstract public EUITooltip getActionTooltip();

}
