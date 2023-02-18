package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

public abstract class PMove_Modify<T extends PField_CardCategory> extends PMove<T>
{
    public PMove_Modify(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMove_Modify(PSkillData<T> data, int amount, int extraAmount, PCLCardGroupHelper... groups)
    {
        super(data, PCLCardTarget.None, amount, extraAmount);
        fields.setCardGroup(groups);
    }

    public void cardAction(List<AbstractCard> cards)
    {
        for (AbstractCard c : cards)
        {
            getAction().invoke(c);
        }
    }

    public abstract ActionT1<AbstractCard> getAction();

    public String getObjectSampleText()
    {
        return getObjectText();
    }

    public abstract String getObjectText();

    @Override
    public String wrapExtra(int input)
    {
        return input > 0 ? "+" + input : String.valueOf(input);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_giveTarget(TEXT.subjects_card, getObjectSampleText());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().selectFromPile(getName(), amount <= 0 ? Integer.MAX_VALUE : amount, fields.getCardGroup(info))
                .setOptions((amount <= 0 || fields.groupTypes.isEmpty() || useParent ? PCLCardSelection.Random : PCLCardSelection.Manual).toSelection(), true)
                .addCallback(this::cardAction);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String giveString = getObjectText();
        return useParent ? TEXT.act_giveTarget(getInheritedString(), giveString) :
                fields.hasGroups() ?
                        TEXT.act_giveFrom(EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects_all : getAmountRawString(), pluralCard()), fields.getGroupString(), giveString) :
                        TEXT.act_giveTarget(TEXT.subjects_thisX, giveString);
    }
}
