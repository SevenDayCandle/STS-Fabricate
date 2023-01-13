package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

public class PMove_StackTemporaryPower extends PMove<PField_Power>
{
    public static final PSkillData<PField_Power> DATA = register(PMove_StackTemporaryPower.class, PField_Power.class, -DEFAULT_MAX, DEFAULT_MAX);

    public PMove_StackTemporaryPower()
    {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StackTemporaryPower(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_StackTemporaryPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
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
    public String getSampleText()
    {
        return TEXT.actions.applyAmount(EUIUtils.format(TEXT.cardMods.tempPowerPrefix, "X"), TEXT.cardEditor.powers);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (PCLPowerHelper power : fields.powers)
        {
            getActions().applyPower(info.source, info.target, target, power, amount, true);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String joinedString = EUIUtils.format(TEXT.cardMods.tempPowerPrefix, fields.getPowerString());
        switch (target)
        {
            case RandomEnemy:
            case AllEnemy:
                return fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.actions.applyAmountToTarget(getAmountRawString(), joinedString, getTargetString()) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            case Single:
                return fields.powers.size() > 0 && fields.powers.get(0).isDebuff ? TEXT.actions.applyAmount(getAmountRawString(), joinedString) : TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), joinedString);
            default:
                return amount < 0 ? TEXT.actions.loseAmount(getAmountRawString(), joinedString)
                        : TEXT.actions.gainAmount(getAmountRawString(), joinedString);
        }
    }
}
