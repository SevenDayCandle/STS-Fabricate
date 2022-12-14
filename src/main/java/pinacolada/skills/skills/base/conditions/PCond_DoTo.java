package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.SelectFromPileMarker;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.CardSelection;

import java.util.ArrayList;

public abstract class PCond_DoTo extends PCond implements SelectFromPileMarker
{

    public PCond_DoTo(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_DoTo(PSkillData data)
    {
        super(data);
    }

    public PCond_DoTo(PSkillData data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PCond_DoTo(PSkillData data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount, groups);
    }

    public PCond_DoTo(PSkillData data, PCLCardTarget target, int amount, PSkill effect)
    {
        super(data, target, amount, effect);
    }

    public PCond_DoTo(PSkillData data, PCLCardTarget target, int amount, PSkill... effect)
    {
        super(data, target, amount, effect);
    }

    public PCond_DoTo(PSkillData data, PCLCardTarget target, int amount, PCLAffinity... affinities)
    {
        super(data, target, amount, affinities);
    }

    public PCond_DoTo(PSkillData data, PCLCardTarget target, int amount, PCLOrbHelper... orbs)
    {
        super(data, target, amount, orbs);
    }

    public PCond_DoTo(PSkillData data, PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(data, target, amount, powerHelpers);
    }

    protected PCLActionWithCallback<ArrayList<AbstractCard>> createPileAction()
    {
        return getAction().invoke(getName(), amount, getCardGroup())
                .setFilter(c -> getFullCardFilter().invoke(c))
                .setOptions(alt ? CardSelection.Random : origin, true);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(tooltipTitle(), "X");
    }

    @Override
    public String getSubText()
    {
        return !groupTypes.isEmpty() ? TEXT.actions.genericFrom(tooltipTitle(), getAmountRawString(), getFullCardString(), getGroupString())
                : EUIRM.strings.verbNoun(tooltipTitle(), getAmountRawString());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (childEffect != null)
        {
            useImpl(() -> childEffect.use(info));
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (childEffect != null)
        {
            useImpl(() -> childEffect.use(info, index));
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (isUsing && childEffect != null)
        {
            useImpl(() -> childEffect.use(info));
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
        ArrayList<PCLCardGroupHelper> tempGroups = groupTypes;
        if (tempGroups.isEmpty())
        {
            tempGroups = new ArrayList<>();
            tempGroups.add(PCLCardGroupHelper.Hand);
        }
        for (PCLCardGroupHelper group : tempGroups)
        {
            if (EUIUtils.filter(group.getCards(), c -> getFullCardFilter().invoke(c)).size() < amount)
            {
                return false;
            }
        }
        return true;
    }

    protected void useImpl(ActionT0 callback)
    {
        ArrayList<PCLCardGroupHelper> tempGroups = groupTypes;
        if (tempGroups.isEmpty())
        {
            tempGroups = new ArrayList<>();
            tempGroups.add(PCLCardGroupHelper.Hand);
        }
        getActions().add(createPileAction())
                .addCallback(cards -> {
                    if (cards.size() >= amount)
                    {
                        childEffect.setCards(cards);
                        callback.invoke();
                    }
                });
    }
}
