package pinacolada.skills.skills;

import extendedui.interfaces.delegates.FuncT3;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLUseInfo;

public class PSpecialCond extends PCustomCond
{
    private final FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse;

    public PSpecialCond(PCLCardData cardData, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse)
    {
        this(cardData, 0, onUse, 1, 0);
    }

    public PSpecialCond(PCLCardData cardData, int index, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse)
    {
        this(cardData, index, onUse, 1, 0);
    }

    public PSpecialCond(PCLCardData cardData, int index, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount)
    {
        this(cardData, index, onUse, amount, 0);
    }

    public PSpecialCond(PCLCardData cardData, int index, FuncT3<Boolean, PSpecialCond, PCLUseInfo, Boolean> onUse, int amount, int extra)
    {
        super(cardData, index, amount, extra);
        this.onUse = onUse;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return onUse.invoke(this, info, isUsing);
    }

    @Override
    public PSpecialCond makeCopy()
    {
        return new PSpecialCond(cardData, descIndex, onUse, amount, extra);
    }
}
