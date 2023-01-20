package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

public abstract class PCond_DoTo extends PCond<PField_CardCategory>
{
    public PCond_DoTo(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PCond_DoTo(PSkillData<PField_CardCategory> data)
    {
        super(data);
    }

    public PCond_DoTo(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PCond_DoTo(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects.x);
    }

    @Override
    public String getSubText()
    {
        return fields.hasGroups() ? TEXT.actions.genericFrom(getActionTitle(), getAmountRawString(), fields.getFullCardString(), fields.getGroupString())
                : EUIRM.strings.verbNoun(getActionTitle(), getAmountRawString());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info, index));
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (isUsing && childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return childEffect == null ? (getSubText() + PCLCoreStrings.period(addPeriod)) : TEXT.conditions.inOrderTo(getSubText(), capital(childEffect.getText(false), addPeriod)) + PCLCoreStrings.period(addPeriod);
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

    protected void useImpl(PCLUseInfo info, ActionT0 callback)
    {
        getActions().add(fields.getGenericPileAction(getAction(), info))
                .addCallback(cards -> {
                    if (cards.size() >= amount)
                    {
                        info.setData(cards);
                        callback.invoke();
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
