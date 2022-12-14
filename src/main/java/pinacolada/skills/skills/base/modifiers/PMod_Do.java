package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.SelectFromPileMarker;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.CardSelection;

import java.util.ArrayList;

public abstract class PMod_Do extends PMod implements SelectFromPileMarker
{

    public PMod_Do(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_Do(PSkillData data)
    {
        super(data);
    }

    public PMod_Do(PSkillData data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod_Do(PSkillData data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount, groups);
    }

    public PMod_Do(PSkillData data, PCLCardTarget target, int amount, PSkill effect)
    {
        super(data, target, amount, effect);
    }

    public PMod_Do(PSkillData data, PCLCardTarget target, int amount, PSkill... effect)
    {
        super(data, target, amount, effect);
    }

    public PMod_Do(PSkillData data, PCLCardTarget target, int amount, PCLAffinity... affinities)
    {
        super(data, target, amount, affinities);
    }

    public PMod_Do(PSkillData data, PCLCardTarget target, int amount, PCLOrbHelper... orbs)
    {
        super(data, target, amount, orbs);
    }

    public PMod_Do(PSkillData data, PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(data, target, amount, powerHelpers);
    }

    protected PCLActionWithCallback<ArrayList<AbstractCard>> createPileAction()
    {
        SelectFromPile action = getAction().invoke(getName(), amount <= 0 ? Integer.MAX_VALUE : amount, getCardGroup())
                .setOptions(alt || amount <= 0 ? CardSelection.Random : origin, true);
        if (alt2)
        {
            action = action.setFilter(c -> getFullCardFilter().invoke(c));
        }
        return action;
    }

    public String getMoveString(boolean addPeriod)
    {
        String amString = amount <= 0 ? TEXT.subjects.all : getAmountRawString();
        return !groupTypes.isEmpty() ?
                TEXT.actions.genericFrom(tooltipTitle(), amString, alt2 ? getFullCardString() : getShortCardString(), getGroupString())
                : EUIRM.strings.verbNoun(tooltipTitle(), amString);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(tooltipTitle(), "X") + " " + TEXT.conditions.doX("Y");
    }

    @Override
    public String getSubText()
    {
        return getFullCardOrString(1);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getMoveString(addPeriod) + LocalizedStrings.PERIOD + (childEffect != null ? (" " +
                (childEffect.useParent ? childEffect.getText(addPeriod) :
                        (TEXT.conditions.per(capital(childEffect.getText(false), addPeriod), EUIRM.strings.nounVerb(getSubText(), tooltipPast())) + PCLCoreStrings.period(addPeriod))
                )) : "");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().add(createPileAction())
                .addCallback(cards -> {
                    this.cards = cards;
                    if (this.childEffect != null)
                    {
                        if (this.childEffect.useParent)
                        {
                            this.childEffect.setCards(cards);
                        }
                        else
                        {
                            updateChildAmount(info);
                        }
                        this.childEffect.use(info);
                    }
                });
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        getActions().add(createPileAction())
                .addCallback(cards -> {
                    this.cards = cards;
                    if (this.childEffect != null)
                    {
                        if (this.childEffect.useParent)
                        {
                            this.childEffect.setCards(cards);
                        }
                        else
                        {
                            updateChildAmount(info);
                        }
                        this.childEffect.use(info, index);
                    }
                });
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return cards == null || be == null ? 0 : be.baseAmount * (alt2 ? cards.size() : (EUIUtils.count(cards,
                c -> getFullCardFilter().invoke(c)
        )));
    }
}
