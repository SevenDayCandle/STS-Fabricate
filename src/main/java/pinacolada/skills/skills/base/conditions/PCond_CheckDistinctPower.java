package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnApplyPowerSubscriber;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PCond_CheckDistinctPower extends PPassiveCond<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PCond_CheckDistinctPower.class, PField_Power.class);

    public PCond_CheckDistinctPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckDistinctPower() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckDistinctPower(PCLCardTarget target, int amount, PCLPowerHelper... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    public PCond_CheckDistinctPower(PCLCardTarget target, int amount, List<PCLPowerHelper> powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        AbstractPower.PowerType targetType = fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF;
        return ((fields.powers.isEmpty() ?
                evaluateTargets(info, t -> fields.doesValueMatchThreshold(EUIUtils.count(t.powers, po -> po.type == targetType))) :
                evaluateTargets(info, t -> fields.doesValueMatchThreshold(EUIUtils.count(t.powers, po -> EUIUtils.any(fields.powers, f -> f.ID.equals(po.ID)))))));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_distinct(TEXT.cedit_powers));
    }

    @Override
    public String wrapAmount(int input) {
        return fields.getThresholdValString(input);
    }

    @Override
    public String getSubText() {
        return getTargetHasString(fields.getThresholdRawString(TEXT.subjects_distinct(fields.getPowerSubjectString())));
    }
}
