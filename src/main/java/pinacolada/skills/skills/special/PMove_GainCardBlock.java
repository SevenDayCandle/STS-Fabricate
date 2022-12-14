package pinacolada.skills.skills.special;

import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;

public class PMove_GainCardBlock extends PMove implements Hidden
{
    public static final PSkillData DATA = register(PMove_GainCardBlock.class, PCLEffectType.General);

    public PMove_GainCardBlock(PointerProvider card)
    {
        super(DATA, PCLCardTarget.Self, 0);
        setSource(card, PCLCardValueSource.Block, PCLCardValueSource.RightCount);
    }

    @Override
    public ColoredString getColoredValueString()
    {
        return getColoredValueString(Math.abs(baseAmount), Math.abs(amount));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount("X", PGR.core.tooltips.block);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (sourceCard instanceof EditorCard)
        {
            for (int i = 0; i < ((EditorCard) sourceCard).rightCount(); i++) {
                getActions().gainBlock(amount);
            }
        }
        else
        {
            getActions().gainBlock(amount);
        }

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
