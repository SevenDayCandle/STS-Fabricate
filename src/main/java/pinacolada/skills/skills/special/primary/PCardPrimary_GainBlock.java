package pinacolada.skills.skills.special.primary;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PCardPrimary;

public class PCardPrimary_GainBlock extends PCardPrimary<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PCardPrimary_GainBlock.class, PField_Empty.class);

    public PCardPrimary_GainBlock()
    {
        super(DATA, PCLCardTarget.Self, 0);
    }

    public PCardPrimary_GainBlock(PointerProvider card)
    {
        super(DATA, card);
    }

    public PCardPrimary_GainBlock setProvider(PointerProvider card)
    {
        setSource(card, PCLCardValueSource.Block, PCLCardValueSource.RightCount);
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount(TEXT.subjects.x, PGR.core.tooltips.block);
    }

    @Override
    public ColoredString getColoredValueString(Object displayBase, Object displayAmount)
    {
        return new ColoredString(displayAmount,
                (sourceCard != null ?
                        sourceCard.upgradedBlock ? Settings.GREEN_TEXT_COLOR :
                                sourceCard.isBlockModified ? (amount > baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR)
                                        : Settings.CREAM_COLOR : Settings.CREAM_COLOR));
    }

    @Override
    public void useImpl(PCLUseInfo info)
    {
        // TODO card gain block action
        for (AbstractCreature c : getTargetList(info))
        {
            // Extra has the value of right count
            for (int i = 0; i < extra; i++) {
                getActions().gainBlock(amount);
            }
        }
    }

    @Override
    public PCardPrimary_GainBlock makeCopy()
    {
        return (PCardPrimary_GainBlock) super.makeCopy();
    }

    @Override
    public String getSubText()
    {
        int count = source != null ? getExtraFromCard() : 1;
        String amountString = count > 1 ? getAmountRawString() + "x" + getExtraRawString() : getAmountRawString();
        if (target == PCLCardTarget.Self)
        {
            return TEXT.actions.gainAmount(amountString, PGR.core.tooltips.block);
        }
        return TEXT.actions.giveTargetAmount(getTargetString(), amountString, PGR.core.tooltips.block);
    }
}
