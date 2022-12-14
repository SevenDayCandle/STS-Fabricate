package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.Power;

public class PMod_PerDistinctPower extends PMod
{

    public static final PSkillData DATA = register(PMod_PerDistinctPower.class, Power);

    public PMod_PerDistinctPower(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerDistinctPower()
    {
        super(DATA);
    }

    public PMod_PerDistinctPower(int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerDistinctPower(PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, target, amount, powerHelpers);
    }

    public PMod_PerDistinctPower(int amount, List<PCLPowerHelper> powerHelpers)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount, powerHelpers.toArray(new PCLPowerHelper[]{}));
    }

    public PMod_PerDistinctPower(PCLCardTarget target, int amount, List<PCLPowerHelper> powerHelpers)
    {
        super(DATA, target, amount, powerHelpers.toArray(new PCLPowerHelper[]{}));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.cardEditor.powers);
    }

    @Override
    public String getSubText()
    {
        String baseString = (powers.isEmpty() ? PGR.core.tooltips.debuff.title : getPowerAndString());
        switch (target)
        {
            case All:
            case Any:
                return TEXT.subjects.onAnyCharacter(baseString);
            case AllEnemy:
                return TEXT.subjects.onAnyEnemy(baseString);
            case Single:
                return TEXT.subjects.onTheEnemy(baseString);
            case Self:
                return TEXT.subjects.onYou(baseString);
            default:
                return baseString;
        }
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.conditions.perDistinct(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getSubText() + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return powers.isEmpty() ? be.baseAmount * EUIUtils.sumInt(targetList, t -> EUIUtils.count(t.powers, po -> po.type == AbstractPower.PowerType.DEBUFF)) :
                be.baseAmount * EUIUtils.sumInt(targetList, t -> EUIUtils.count(powers, po -> GameUtilities.getPowerAmount(t, po.ID) >= this.amount));
    }
}
