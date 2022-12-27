package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

public class PMove_StackPower extends PMove
{
    public static final PSkillData DATA = register(PMove_StackPower.class, PCLEffectType.Power, -999, 999);

    public PMove_StackPower()
    {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StackPower(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_StackPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        super(DATA, target, amount, powers);
    }

    @Override
    public void onDrag(AbstractMonster m)
    {
        if (m != null)
        {
            for (PCLPowerHelper power : powers)
            {
                GameUtilities.getIntent(m).addModifier(power.ID, amount);
            }
        }
    }

    @Override
    public ColoredString getColoredValueString()
    {
        return target == PCLCardTarget.Self ? getColoredValueString(Math.abs(baseAmount), Math.abs(amount)) : super.getColoredValueString();
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.applyAmount("X", TEXT.cardEditor.powers);
    }

    @Override
    public boolean isDetrimental()
    {
        return ((target == PCLCardTarget.None || target == PCLCardTarget.Self || target == PCLCardTarget.All) && EUIUtils.any(powers, po -> po.isDebuff)) ||
                ((target != PCLCardTarget.None && target != PCLCardTarget.Self && target != PCLCardTarget.All) && EUIUtils.any(powers, po -> !po.isDebuff));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (alt)
        {
            PCLPowerHelper power = GameUtilities.getRandomElement(powers);
            if (power != null)
            {
                getActions().applyPower(info.source, info.target, target, power, amount);
            }
        }
        else if (!powers.isEmpty())
        {
            for (PCLPowerHelper power : powers)
            {
                getActions().applyPower(info.source, info.target, target, power, amount);
            }
        }
        else
        {
            for (int i = 0; i < amount; i++)
            {
                getActions().applyPower(info.source, info.target, target, alt2 ? PCLPowerHelper.randomBuff() : PCLPowerHelper.randomDebuff(), amount);
            }
        }
        super.use(info);

    }

    @Override
    public String getSubText()
    {
        String joinedString;
        if (alt && !powers.isEmpty())
        {
            joinedString = getPowerOrString();
            switch (target)
            {
                case RandomEnemy:
                case AllEnemy:
                case All:
                case Team:
                    return TEXT.subjects.randomly(powers.size() > 0 && powers.get(0).isDebuff ? TEXT.actions.applyAmountToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
                case Single:
                case SingleAlly:
                    return TEXT.subjects.randomly(powers.size() > 0 && powers.get(0).isDebuff ? TEXT.actions.applyAmount(getAmountRawString(), joinedString) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
                case Self:
                    if (isFromCreature())
                    {
                        return TEXT.subjects.randomly(TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
                    }
                default:
                    return TEXT.subjects.randomly(amount < 0 ? TEXT.actions.loseAmount(getAmountRawString(), joinedString)
                            : TEXT.actions.gainAmount(getAmountRawString(), joinedString));
            }
        }
        joinedString = powers.isEmpty() ? TEXT.subjects.randomX(plural(alt2 ? PGR.core.tooltips.buff : PGR.core.tooltips.debuff)) : getPowerString();
        switch (target)
        {
            case RandomEnemy:
            case AllEnemy:
            case All:
            case Team:
                return powers.size() > 0 && powers.get(0).isDebuff ? TEXT.actions.applyAmountToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            case Single:
            case SingleAlly:
                return powers.size() > 0 && powers.get(0).isDebuff ? TEXT.actions.applyAmount(getAmountRawString(), joinedString) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            case Self:
                if (isFromCreature())
                {
                    return TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
                }
            default:
                return amount < 0 ? TEXT.actions.loseAmount(getAmountRawString(), joinedString)
                        : TEXT.actions.gainAmount(getAmountRawString(), joinedString);
        }
    }
}
