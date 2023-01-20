package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnApplyPowerSubscriber;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PCond_CheckPower extends PCond<PField_Power> implements OnApplyPowerSubscriber
{
    public static final PSkillData<PField_Power> DATA = register(PCond_CheckPower.class, PField_Power.class);

    public PCond_CheckPower(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_CheckPower()
    {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    public PCond_CheckPower(PCLCardTarget target, int amount, List<PCLPowerHelper> powers)
    {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    private boolean checkPowers(PCLPowerHelper po, AbstractCreature t)
    {
        return amount == 0 ? GameUtilities.getPowerAmount(t, po.ID) == amount :
                fields.debuff == (GameUtilities.getPowerAmount(t, po.ID) < amount);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.numNoun(TEXT.subjects.x, TEXT.cardEditor.powers);
    }

    @Override
    public String getSubText()
    {
        String baseString = fields.getPowerSubjectString();
        baseString = fields.random ? EUIRM.strings.numNoun("< " + amount, baseString) : this.amount == 1 ? baseString : EUIRM.strings.numNoun((this.amount == 0 ? this.amount : this.amount + "+"), baseString);
        if (isTrigger())
        {
            return TEXT.conditions.wheneverYou(target == PCLCardTarget.Self ? TEXT.actions.gain(baseString) : TEXT.actions.apply(baseString));
        }

        switch (target)
        {
            case All:
            case Any:
                return TEXT.conditions.ifAnyCharacterHas(baseString);
            case AllEnemy:
                return TEXT.conditions.ifAnyEnemyHas(baseString);
            case Single:
                return TEXT.conditions.ifTheEnemyHas(baseString);
            case Self:
                return TEXT.conditions.ifYouHave(baseString);
            default:
                return baseString;
        }
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature t, AbstractCreature source)
    {
        if (this.childEffect != null && fields.powers.isEmpty() ? power.type == (fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF)
                : fields.getPowerFilter().invoke(power) && source == getSourceCreature() && target == PCLCardTarget.Self ^ !(source == t))
        {
            useFromTrigger(makeInfo(null).setData(power));
        }
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        AbstractPower.PowerType targetType = fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF;
        List<AbstractCreature> targetList = getTargetList(info);
        return fields.random ^ ((fields.powers.isEmpty() ?
                EUIUtils.any(targetList, t -> amount == 0 ? (t.powers == null || !EUIUtils.any(t.powers, po -> po.type == targetType)) : t.powers != null && EUIUtils.any(t.powers, po -> po.type == targetType && po.amount >= amount)) :
                EUIUtils.any(targetList, t -> fields.debuff ? EUIUtils.any(fields.powers, po -> checkPowers(po, t)) : EUIUtils.all(fields.powers, po -> checkPowers(po, t)))));
    }
}
