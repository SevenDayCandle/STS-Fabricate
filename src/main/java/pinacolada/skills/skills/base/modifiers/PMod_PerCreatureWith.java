package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.Power;

public class PMod_PerCreatureWith extends PMod
{

    public static final PSkillData DATA = register(PMod_PerCreatureWith.class, Power);

    public PMod_PerCreatureWith(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerCreatureWith()
    {
        super(DATA);
    }

    public PMod_PerCreatureWith(int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerCreatureWith(PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, target, amount, powerHelpers);
    }

    public PMod_PerCreatureWith(int amount, List<PCLPowerHelper> powerHelpers)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount, powerHelpers.toArray(new PCLPowerHelper[]{}));
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return powers.isEmpty() ? be.baseAmount * EUIUtils.count(targetList, t -> t.powers != null && EUIUtils.any(t.powers, po -> po.type == AbstractPower.PowerType.DEBUFF)) :
                be.baseAmount * EUIUtils.count(targetList, t -> alt ? EUIUtils.any(powers, po -> GameUtilities.getPowerAmount(t, po.ID) >= amount) : EUIUtils.all(powers, po -> GameUtilities.getPowerAmount(t, po.ID) >= amount));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.subjects.enemyWithX("Y"));
    }

    @Override
    public String getSubText()
    {
        String baseString = (this.amount <= 1 ? "" : getAmountRawString() + " ") + (powers.isEmpty() ? plural(PGR.core.tooltips.debuff) : alt ? getPowerOrString() : getPowerAndString());
        return target == PCLCardTarget.Any ? TEXT.subjects.characterWithX(baseString) : TEXT.subjects.enemyWithX(baseString);
    }
}
