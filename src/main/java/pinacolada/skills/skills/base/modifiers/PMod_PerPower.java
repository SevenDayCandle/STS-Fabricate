package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
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

public class PMod_PerPower extends PMod
{

    public static final PSkillData DATA = register(PMod_PerPower.class, Power);

    public PMod_PerPower(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerPower()
    {
        super(DATA);
    }

    public PMod_PerPower(int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerPower(PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, target, amount, powerHelpers);
    }

    public PMod_PerPower(int amount, List<PCLPowerHelper> powerHelpers)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount, powerHelpers.toArray(new PCLPowerHelper[]{}));
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return powers.isEmpty() ? be.baseAmount *
                EUIUtils.sumInt(targetList, t -> t.powers != null ? EUIUtils.sumInt(t.powers, po -> po.type == AbstractPower.PowerType.DEBUFF ? po.amount : 0) : 0) / Math.max(1, this.amount) :
                be.baseAmount * EUIUtils.sumInt(targetList, t -> EUIUtils.sumInt(powers, po -> GameUtilities.getPowerAmount(t, po.ID))) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.cardEditor.powers);
    }

    @Override
    public String getSubText()
    {
        String baseString = powers.isEmpty() ? plural(PGR.core.tooltips.debuff) : getPowerAndString();
        if (amount > 1)
        {
            baseString = EUIRM.strings.numNoun(getAmountRawString(), baseString);
        }
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
}
