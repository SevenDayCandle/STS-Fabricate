package pinacolada.skills.skills;

import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import pinacolada.actions.PCLActions;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Empty;

public class PSpecialPowerSkill extends PSkill<PField_Empty> implements SummonOnlyMove
{
    public static final PSkillData<PField_Empty> DATA = register(PSpecialPowerSkill.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget();
    private final String description;
    private final FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> powerFunc;

    public PSpecialPowerSkill(String effectID, String description, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> powerFunc)
    {
        this(effectID, description, powerFunc, 1, 0);
    }

    public PSpecialPowerSkill(String effectID, String description, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> powerFunc, int amount)
    {
        this(effectID, description, powerFunc, amount, 0);
    }

    public PSpecialPowerSkill(String effectID, String description, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> powerFunc, int amount, int extra)
    {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = effectID;
        this.description = description;
        this.powerFunc = powerFunc;
    }

    public PSpecialPowerSkill(String effectID, FuncT1<String, PSpecialPowerSkill> strFunc, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> powerFunc)
    {
        this(effectID, strFunc, powerFunc, 1, 0);
    }

    public PSpecialPowerSkill(String effectID, FuncT1<String, PSpecialPowerSkill> strFunc, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> powerFunc, int amount)
    {
        this(effectID, strFunc, powerFunc, amount, 0);
    }

    public PSpecialPowerSkill(String effectID, FuncT1<String, PSpecialPowerSkill> strFunc, FuncT2<? extends PCLPower, PSpecialPowerSkill, PCLUseInfo> powerFunc, int amount, int extra)
    {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = effectID;
        this.description = strFunc.invoke(this);
        this.powerFunc = powerFunc;
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
    public PSpecialPowerSkill makeCopy()
    {
        return new PSpecialPowerSkill(effectID, description, powerFunc, amount, extra);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        PCLActions.bottom.applyPower(powerFunc.invoke(this, info)).allowNegative(true);
    }
}
