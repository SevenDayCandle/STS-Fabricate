package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCond_CheckPower extends PCond
{
    public static final PSkillData DATA = register(PCond_CheckPower.class, PCLEffectType.Power);

    public PCond_CheckPower(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_CheckPower()
    {
        super(DATA, PCLCardTarget.Self, 1, new PCLPowerHelper[]{});
    }

    public PCond_CheckPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        super(DATA, target, amount, powers);
    }

    public PCond_CheckPower(PCLCardTarget target, int amount, List<PCLPowerHelper> powers)
    {
        super(DATA, target, amount, powers.toArray(new PCLPowerHelper[]{}));
    }

    private boolean checkPowers(PCLPowerHelper po, AbstractCreature t)
    {
        return amount == 0 ? GameUtilities.getPowerAmount(t, po.ID) == amount :
                alt == (GameUtilities.getPowerAmount(t, po.ID) < amount);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.numNoun("X", TEXT.cardEditor.powers);
    }

    @Override
    public String getSubText()
    {
        String baseString = powers.isEmpty() ? plural(alt2 ? PGR.core.tooltips.buff : PGR.core.tooltips.debuff) : alt2 ? getPowerOrString() : getPowerAndString();
        baseString = alt ? EUIRM.strings.numNoun("< " + amount, baseString) : this.amount == 1 ? baseString : EUIRM.strings.numNoun((this.amount == 0 ? this.amount : this.amount + "+"), baseString);
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
    public boolean triggerOnApplyPower(AbstractCreature s, AbstractCreature t, AbstractPower c)
    {
        if (this.childEffect != null && powers.isEmpty() ? c.type == (alt2 ? AbstractPower.PowerType.BUFF : AbstractPower.PowerType.DEBUFF)
                : getPowerFilter().invoke(c) && s == getSourceCreature() && target == PCLCardTarget.Self ^ !(s == t))
        {
            this.childEffect.use(makeInfo(null));
        }
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        AbstractPower.PowerType targetType = alt2 ? AbstractPower.PowerType.BUFF : AbstractPower.PowerType.DEBUFF;
        List<AbstractCreature> targetList = getTargetList(info);
        return alt ^ ((powers.isEmpty() ?
                EUIUtils.any(targetList, t -> amount == 0 ? (t.powers == null || !EUIUtils.any(t.powers, po -> po.type == targetType)) : t.powers != null && EUIUtils.any(t.powers, po -> po.type == targetType && po.amount >= amount)) :
                EUIUtils.any(targetList, t -> alt2 ? EUIUtils.any(powers, po -> checkPowers(po, t)) : EUIUtils.all(powers, po -> checkPowers(po, t)))));
    }
}
