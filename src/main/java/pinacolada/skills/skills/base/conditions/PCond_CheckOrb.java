package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PCond_CheckOrb extends PCond
{
    public static final PSkillData DATA = register(PCond_CheckOrb.class, PCLEffectType.Orb)
            .selfTarget();

    public PCond_CheckOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_CheckOrb()
    {
        super(DATA, PCLCardTarget.None, 1, new PCLOrbHelper[]{});
    }

    public PCond_CheckOrb(int amount, PCLOrbHelper... powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers);
    }

    public PCond_CheckOrb(int amount, List<PCLOrbHelper> powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers.toArray(new PCLOrbHelper[]{}));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.evoke("X");
    }

    @Override
    public String getSubText()
    {
        Object tt = alt ? getOrbOrString(getRawString(EFFECT_CHAR)) : getOrbAndString(getRawString(EFFECT_CHAR));
        return TEXT.conditions.ifYouHave(amount == 1 ? tt : EUIRM.strings.numNoun(amount <= 0 ? amount : amount + "+", tt));
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (orbs.isEmpty())
        {
            return amount <= 0 ? GameUtilities.getOrbCount() == 0 : GameUtilities.getOrbCount() >= amount;
        }
        return alt ? EUIUtils.any(orbs, o -> GameUtilities.getOrbCount(o.ID) >= amount) : EUIUtils.all(orbs, o -> GameUtilities.getOrbCount(o.ID) >= amount);
    }
}
