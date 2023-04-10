package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.utilities.ListSelection;

public abstract class PMove_Select<T extends PField_CardGeneric> extends PCallbackMove<T>
{
    public PMove_Select(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMove_Select(PSkillData<T> data, int amount, PCLCardGroupHelper... h)
    {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(h);
    }

    public PMove_Select(PSkillData<T> data, PCLCardTarget target, int amount, PCLCardGroupHelper... h)
    {
        super(data, target, amount);
        fields.setCardGroup(h);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info, ActionT1<PCLUseInfo> callback)
    {
        fields.getGenericPileAction(getAction(), info, extra)
                .addCallback(cards -> {
                    info.setData(cards);
                    callback.invoke(info);
                    if (this.childEffect != null)
                    {
                        this.childEffect.use(info);
                    }
                });
    }

    @Override
    public String getSubText()
    {
        return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedString()) :
                !fields.groupTypes.isEmpty() ? TEXT.act_genericFrom(getActionTitle(), getAmountRawOrAllString(), fields.getFullCardString(), fields.getGroupString())
                        : EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_thisCard);
    }

    @Override
    public String getAmountRawOrAllString()
    {
        return baseAmount <= 0 ? fields.forced ? TEXT.subjects_all : TEXT.subjects_any
                : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(), getAmountRawString())
                : (fields.forced || fields.origin != PCLCardSelection.Manual) ? getAmountRawString() : getRangeToAmountRawString();
    }

    protected String getActionTitle()
    {
        return getActionTooltip().title;
    }

    public abstract EUITooltip getActionTooltip();
    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction();
}
