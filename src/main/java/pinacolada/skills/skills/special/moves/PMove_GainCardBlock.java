package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_GainCardBlock extends PMove<PField_Empty> implements Hidden
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainCardBlock.class, PField_Empty.class);

    public PMove_GainCardBlock(PointerProvider card)
    {
        super(DATA, PCLCardTarget.Self, 0);
        setSource(card, PCLCardValueSource.Block, PCLCardValueSource.RightCount);
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
