package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnApplyPowerSubscriber;
import pinacolada.interfaces.subscribers.OnTryReducePowerSubscriber;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PCond_CheckPower extends PPassiveCond<PField_Power> implements OnApplyPowerSubscriber, OnTryReducePowerSubscriber {
    public static final PSkillData<PField_Power> DATA = register(PCond_CheckPower.class, PField_Power.class);

    public PCond_CheckPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckPower() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckPower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        AbstractPower.PowerType targetType = fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF;
        return ((fields.powers.isEmpty() ?
                evaluateTargets(info, t -> amount == 0 ? (t.powers == null || !EUIUtils.any(t.powers, po -> po.type == targetType)) : t.powers != null && EUIUtils.any(t.powers, po -> po.type == targetType && po.amount >= refreshAmount(info))) :
                evaluateTargets(info, t -> fields.allOrAnyPower(info, t))));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_gainAmount(TEXT.subjects_x, TEXT.cedit_powers)) : EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            if (amount < 0) {
                return getWheneverString(TEXT.act_loseOrdinal(getTargetOrdinalPerspective(perspective), fields.getThresholdRawString(fields.powers.isEmpty() ? fields.getBuffString(2) : fields.getPowerOrString(), requestor)), perspective);
            }
            return getWheneverString(TEXT.act_gainOrdinal(getTargetOrdinalPerspective(perspective), fields.getThresholdRawString(fields.powers.isEmpty() ? fields.getBuffString(2) : fields.getPowerOrString(), requestor)), perspective);
        }

        return getTargetHasStringPerspective(perspective, fields.getThresholdRawString(fields.getPowerSubjectString(), requestor));
    }

    // When the specified creatures gain a power, triggers the effect on that target
    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature source, AbstractCreature target) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo info = generateInfo(owner, target);
        boolean eval = evaluateTargets(info, c -> c == target);
        // For single target powers, the power target needs to match the owner of this skill
        if ((fields.powers.isEmpty() ? power.type == (fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF) : (fields.getPowerFilter().invoke(power)))
                && eval && fields.doesValueMatchThreshold(info, power.amount)) {
            info.setTempTargets(target);
            useFromTrigger(info.setData(power));
        }
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRBoolean(editor, TEXT.cedit_or, null);
    }

    @Override
    public boolean tryReducePower(AbstractPower power, AbstractCreature source, AbstractCreature target, AbstractGameAction action) {
        if (amount < 0) {
            AbstractCreature owner = getOwnerCreature();
            PCLUseInfo info = generateInfo(owner, target);
            boolean eval = evaluateTargets(info, c -> c == target);
            // For single target powers, the power target needs to match the owner of this skill
            if ((fields.powers.isEmpty() ? power.type == (fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF) : (fields.getPowerFilter().invoke(power)))
                    && eval && fields.doesValueMatchThreshold(info, -power.amount)) {
                info.setTempTargets(target);
                useFromTrigger(info.setData(power));
            }
        }
        return true;
    }
}
