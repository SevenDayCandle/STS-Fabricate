package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PMod_PerDistinctPower extends PMod<PField_Power>
{

    public static final PSkillData<PField_Power> DATA = register(PMod_PerDistinctPower.class, PField_Power.class);

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
        this(PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerDistinctPower(PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, target, amount);
        fields.setPower(powerHelpers);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.cardEditor.powers);
    }

    @Override
    public String getSubText()
    {
        String baseString = fields.getPowerSubjectString();
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
        return fields.powers.isEmpty() ? be.baseAmount * EUIUtils.sumInt(targetList, t -> EUIUtils.count(t.powers, po -> po.type == AbstractPower.PowerType.DEBUFF)) :
                be.baseAmount * EUIUtils.sumInt(targetList, t -> EUIUtils.count(fields.powers, po -> GameUtilities.getPowerAmount(t, po.ID) >= this.amount));
    }
}
