package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLAction;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.utilities.ListSelection;

public abstract class PCond_DoToCard extends PActiveNonCheckCond<PField_CardCategory>
{
    public PCond_DoToCard(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PCond_DoToCard(PSkillData<PField_CardCategory> data)
    {
        super(data);
    }

    public PCond_DoToCard(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PCond_DoToCard(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        return fields.hasGroups() ? TEXT.act_genericFrom(getActionTitle(), getAmountRawOrAllString(), fields.getFullCardString(), fields.getGroupString())
                : EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawOrAllString(), fields.getFullCardString());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return capital(childEffect == null ? (getSubText() + PCLCoreStrings.period(addPeriod)) : TEXT.cond_inOrderTo(getSubText(), childEffect.getText(false)), addPeriod);
    }

    @Override
    public String getAmountRawOrAllString()
    {
        return baseAmount <= 0 ? TEXT.subjects_all : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(), getAmountRawString()) : getAmountRawString();
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        for (PCLCardGroupHelper group : fields.groupTypes)
        {
            if (EUIUtils.filter(group.getCards(), c -> fields.getFullCardFilter().invoke(c)).size() < amount)
            {
                return false;
            }
        }
        return true;
    }

    public PCLAction<?> useImpl(PCLUseInfo info, ActionT0 onComplete, ActionT0 onFail)
    {
        return getActions().add(fields.getGenericPileAction(getAction(), info, extra))
                .addCallback(cards -> {
                    if (cards.size() >= amount)
                    {
                        info.setData(cards);
                        onComplete.invoke();
                    }
                    else
                    {
                        onFail.invoke();
                    }
                });
    }

    protected String getActionTitle()
    {
        return getActionTooltip().title;
    }
    public abstract EUITooltip getActionTooltip();
    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction();
}
