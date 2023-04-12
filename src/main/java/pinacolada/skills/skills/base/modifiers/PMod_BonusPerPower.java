package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMod_BonusPerPower extends PMod_BonusPer<PField_Power>
{

    public static final PSkillData<PField_Power> DATA = register(PMod_BonusPerPower.class, PField_Power.class).selfTarget();

    public PMod_BonusPerPower(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_BonusPerPower()
    {
        this(0);
    }

    public PMod_BonusPerPower(int amount, PCLPowerHelper... powerHelpers)
    {
        this(PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_BonusPerPower(PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(DATA, target, amount);
        fields.setPower(powerHelpers);
    }

    @Override
    public String getSubText()
    {
        return TEXT.cedit_powers;
    }

    @Override
    public String getConditionText()
    {
        String baseString = fields.getPowerSubjectString();
        if (amount > 1)
        {
            baseString = EUIRM.strings.numNoun(getAmountRawString(), baseString);
        }
        switch (target)
        {
            case All:
            case Any:
                return TEXT.subjects_onAnyCharacter(baseString);
            case AllEnemy:
                return TEXT.subjects_onAnyEnemy(baseString);
            case Single:
                return TEXT.subjects_onTheEnemy(baseString);
            case Self:
                return TEXT.subjects_onYou(baseString);
            default:
                return baseString;
        }
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        List<AbstractCreature> targetList = getTargetList(info);
        return fields.powers.isEmpty() ?
                EUIUtils.sumInt(targetList, t -> t.powers != null ? EUIUtils.sumInt(t.powers, po -> po.type == AbstractPower.PowerType.DEBUFF ? po.amount : 0) : 0) :
                EUIUtils.sumInt(targetList, t -> EUIUtils.sumInt(fields.powers, po -> GameUtilities.getPowerAmount(t, po.ID)));
    }
}
