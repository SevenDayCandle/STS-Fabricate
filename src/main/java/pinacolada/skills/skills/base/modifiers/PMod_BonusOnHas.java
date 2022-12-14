package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public abstract class PMod_BonusOnHas extends PMod_BonusOn
{
    public PMod_BonusOnHas(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusOnHas(PSkillData data)
    {
        this(data, 0, 1);
    }

    public PMod_BonusOnHas(PSkillData data, int amount, int count)
    {
        super(data, amount, count);
    }

    @Override
    public String getConditionText()
    {
        return TEXT.conditions.ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), getFullCardString(getRawString(EXTRA_CHAR))));
    }

    @Override
    public String getConditionSampleText()
    {
        return TEXT.conditions.ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info)
    {
        int count = EUIUtils.count(getCardPile(),
                c -> getFullCardFilter().invoke(c));
        return extra == 0 ? count == 0 : alt ^ count >= extra;
    }

    abstract public List<AbstractCard> getCardPile();
    abstract public EUITooltip getActionTooltip();
}
