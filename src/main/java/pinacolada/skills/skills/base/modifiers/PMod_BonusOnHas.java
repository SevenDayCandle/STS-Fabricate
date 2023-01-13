package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

public abstract class PMod_BonusOnHas extends PMod_BonusOn<PField_CardCategory>
{
    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data)
    {
        this(data, 0, 1);
    }

    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data, int amount, int count)
    {
        super(data, amount, count);
    }

    @Override
    public String getConditionText()
    {
        return TEXT.conditions.ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), fields.getFullCardString(getRawString(EXTRA_CHAR))));
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
                c -> fields.getFullCardFilter().invoke(c));
        return extra == 0 ? count == 0 : fields.random ^ count >= extra;
    }

    abstract public List<AbstractCard> getCardPile();
    abstract public EUITooltip getActionTooltip();
}
