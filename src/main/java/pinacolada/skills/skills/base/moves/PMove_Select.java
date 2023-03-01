package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

public abstract class PMove_Select extends PMove<PField_CardCategory>
{
    public PMove_Select(PSkillData<PField_CardCategory> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMove_Select(PSkillData<PField_CardCategory> data, int amount, PCLCardGroupHelper... h)
    {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(h);
    }

    public PMove_Select(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... h)
    {
        super(data, target, amount);
        fields.setCardGroup(h);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        fields.getGenericPileAction(getAction(), info)
                .addCallback(cards -> {
                    if (this.childEffect != null)
                    {
                        info.setData(cards);
                        this.childEffect.use(info);
                    }
                });
    }

    @Override
    public String getSubText()
    {
        return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedString()) :
                !fields.groupTypes.isEmpty() ? TEXT.act_genericFrom(getActionTitle(), amount <= 0 ? TEXT.subjects_all : getAmountRawString(), fields.getFullCardString(), fields.getGroupString())
                        : EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_thisObj);
    }

    protected String getActionTitle()
    {
        return getActionTooltip().title;
    }

    public abstract EUITooltip getActionTooltip();
    public abstract FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction();
}
