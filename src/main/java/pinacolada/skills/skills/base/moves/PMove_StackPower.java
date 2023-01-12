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
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

public class PMove_StackPower extends PMove<PField_Power>
{
    public static final PSkillData<PField_Power> DATA = register(PMove_StackPower.class, PField_Power.class, -DEFAULT_MAX, DEFAULT_MAX);

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
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    @Override
    public void onDrag(AbstractMonster m)
    {
        if (m != null)
        {
            for (PCLPowerHelper power : fields.powers)
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
        return ((target.targetsSelf()) && EUIUtils.any(fields.powers, po -> po.isDebuff)) ||
                ((!target.targetsSelf()) && EUIUtils.any(fields.powers, po -> !po.isDebuff));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (fields.random)
        {
            PCLPowerHelper power = GameUtilities.getRandomElement(fields.powers);
            if (power != null)
            {
                getActions().applyPower(info.source, info.target, target, power, amount);
            }
        }
        else if (!fields.powers.isEmpty())
        {
            for (PCLPowerHelper power : fields.powers)
            {
                getActions().applyPower(info.source, info.target, target, power, amount);
            }
        }
        else
        {
            for (int i = 0; i < amount; i++)
            {
                getActions().applyPower(info.source, info.target, target, fields.debuff ? PCLPowerHelper.randomDebuff() : PCLPowerHelper.randomBuff(), amount);
            }
        }
        super.use(info);

    }

    @Override
    public String getSubText()
    {
        String joinedString;
        if (fields.random && !fields.powers.isEmpty())
        {
            joinedString = fields.getPowerOrString();
            switch (target)
            {
                case RandomEnemy:
                case AllAlly:
                case AllEnemy:
                case All:
                case Team:
                    return TEXT.subjects.randomly(fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.actions.applyAmountToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
                case Single:
                case SingleAlly:
                    return TEXT.subjects.randomly(fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.actions.applyAmount(getAmountRawString(), joinedString) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString));
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
        joinedString = fields.powers.isEmpty() ? TEXT.subjects.randomX(plural(fields.debuff ? PGR.core.tooltips.debuff : PGR.core.tooltips.buff)) : fields.getPowerString();
        switch (target)
        {
            case RandomEnemy:
            case AllEnemy:
            case All:
            case Team:
                return fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.actions.applyAmountToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            case Single:
            case SingleAlly:
                return fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.actions.applyAmount(getAmountRawString(), joinedString) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
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
