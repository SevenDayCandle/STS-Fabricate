package pinacolada.skills.skills;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.interfaces.delegates.FuncT3;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Empty;

public class PSpecialPowerSkill extends PSkill<PField_Empty> implements SummonOnlyMove {
    public static final PSkillData<PField_Empty> DATA = register(PSpecialPowerSkill.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX);
    private final String description;
    private final FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> powerFunc;

    public PSpecialPowerSkill(String effectID, String description, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> powerFunc) {
        this(effectID, description, PCLCardTarget.Self, powerFunc, 1, 0);
    }
    public PSpecialPowerSkill(String effectID, String description, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> powerFunc, int amount) {
        this(effectID, description, PCLCardTarget.Self, powerFunc, amount, 0);
    }
    public PSpecialPowerSkill(String effectID, String description, PCLCardTarget target, FuncT3<? extends AbstractPower, AbstractCreature, AbstractCreature, PSpecialPowerSkill> powerFunc, int amount, int extra) {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        setTarget(target);
        this.effectID = effectID;
        this.description = description;
        this.powerFunc = powerFunc;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return description;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return EUIUtils.format(getSubText(perspective, requestor), getAmountRawString(), getExtraRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public PSpecialPowerSkill makeCopy() {
        return new PSpecialPowerSkill(effectID, description, target, powerFunc, amount, extra);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature c : getTargetListAsNew(info)) {
            order.applyPower(powerFunc.invoke(c, info.source, this)).allowNegative(true);
        }
    }
}
