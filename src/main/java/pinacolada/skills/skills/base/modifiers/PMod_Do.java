package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLAction;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PActiveMod;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public abstract class PMod_Do extends PActiveMod<PField_CardCategory>
{

    public PMod_Do(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMod_Do(PSkillData<PField_CardCategory> data)
    {
        super(data);
    }

    public PMod_Do(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod_Do(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount);
        fields.setCardGroup(groups);
    }

    protected PCLAction<ArrayList<AbstractCard>> createPileAction(PCLUseInfo info)
    {
        SelectFromPile action = getAction().invoke(getName(), info.target, amount <= 0 ? Integer.MAX_VALUE : amount, fields.origin.toSelection(), fields.getCardGroup(info))
                .setOptions((amount <= 0 ? PCLCardSelection.Random : fields.origin).toSelection(), true);
        if (fields.forced)
        {
            action = action.setFilter(c -> fields.getFullCardFilter().invoke(c));
        }
        return action;
    }

    public String getMoveString(boolean addPeriod)
    {
        String amString = amount <= 0 ? TEXT.subjects_all : getAmountRawString();
        return !fields.groupTypes.isEmpty() ?
                TEXT.act_genericFrom(getActionTitle(), amString, fields.forced ? fields.getFullCardString() : fields.getShortCardString(), fields.getGroupString())
                : EUIRM.strings.verbNoun(getActionTitle(), amString);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x) + " " + TEXT.cond_doX(TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        return fields.getFullCardStringSingular();
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getMoveString(addPeriod) + LocalizedStrings.PERIOD + (childEffect != null ? (" " +
                (childEffect.useParent ? childEffect.getText(addPeriod) :
                        (TEXT.cond_per(capital(childEffect.getText(false), addPeriod), EUIRM.strings.nounVerb(getSubText(), getActionPast())) + PCLCoreStrings.period(addPeriod))
                )) : "");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().add(createPileAction(info))
                .addCallback(cards -> {
                    if (this.childEffect != null)
                    {
                        info.setData(cards);
                        if (!this.childEffect.useParent)
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
        getActions().add(createPileAction(info))
                .addCallback(cards -> {
                    if (this.childEffect != null)
                    {
                        info.setData(cards);
                        if (!this.childEffect.useParent)
                        {
                            updateChildAmount(info);
                        }
                        this.childEffect.use(info, index);
                    }
                });
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        ArrayList<AbstractCard> cards = info.getData(new ArrayList<>());
        return cards == null || be == null ? 0 : be.baseAmount * (fields.forced ? cards.size() : (EUIUtils.count(cards,
                c -> fields.getFullCardFilter().invoke(c)
        )));
    }

    protected String getActionTitle()
    {
        return getActionTooltip().title;
    }
    protected String getActionPast()
    {
        return getActionTooltip().past;
    }
    public abstract EUITooltip getActionTooltip();
    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction();
}
