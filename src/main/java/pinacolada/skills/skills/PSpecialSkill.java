package pinacolada.skills.skills;

import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;

public class PSpecialSkill extends PSkill
{
    public static final PSkillData DATA = register(PSpecialSkill.class, PCLEffectType.General)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget();
    private final String description;
    private final ActionT2<PSpecialSkill, PCLUseInfo> onUse;

    public PSpecialSkill(String effectID, String description, ActionT2<PSpecialSkill, PCLUseInfo> onUse)
    {
        this(effectID, description, onUse, 1, 0);
    }

    public PSpecialSkill(String effectID, String description, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount)
    {
        this(effectID, description, onUse, amount, 0);
    }

    public PSpecialSkill(String effectID, String description, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount, int extra)
    {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = effectID;
        this.description = description;
        this.onUse = onUse;
    }

    public PSpecialSkill(String effectID, FuncT1<String, PSpecialSkill> strFunc, ActionT2<PSpecialSkill, PCLUseInfo> onUse)
    {
        this(effectID, strFunc, onUse, 1, 0);
    }

    public PSpecialSkill(String effectID, FuncT1<String, PSpecialSkill> strFunc, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount)
    {
        this(effectID, strFunc, onUse, amount, 0);
    }

    public PSpecialSkill(String effectID, FuncT1<String, PSpecialSkill> strFunc, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int amount, int extra)
    {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = effectID;
        this.description = strFunc.invoke(this);
        this.onUse = onUse;
    }

    @Override
    public String getSubText()
    {
        return description;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return EUIUtils.format(getSubText(), getAmountRawString(), getExtraRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public PSkill makeCopy()
    {
        return new PSpecialSkill(effectID, description, onUse, amount, extra);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        onUse.invoke(this, info);
    }
}
