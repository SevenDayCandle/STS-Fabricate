package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.utilities.EUIClassUtils;
import pinacolada.actions.PCLActions;
import pinacolada.actions.powers.ApplyOrReducePowerAction;
import pinacolada.actions.utility.SequentialAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.skills.skills.PActiveMod;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

@VisibleSkill
public class PMod_PayPerPower extends PActiveMod<PField_Power> {

    public static final PSkillData<PField_Power> DATA = register(PMod_PayPerPower.class, PField_Power.class).noTarget();

    public PMod_PayPerPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PayPerPower() {
        super(DATA);
    }

    public PMod_PayPerPower(int amount, PCLPowerData... orbs) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setPower(orbs);
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        return AbstractDungeon.player == null ? 0 : baseAmount * (fields.powers.isEmpty() ?
                sumTargets(info, t -> t.powers != null ? EUIUtils.sumInt(t.powers, po -> (po.type == AbstractPower.PowerType.DEBUFF) ^ !fields.debuff ? limitPer(po.amount) : 0) : 0) :
                sumTargets(info, t -> EUIUtils.sumInt(fields.powers, po -> limitPer(GameUtilities.getPowerAmount(t, po))))) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_pay(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return this.amount <= 1 ? fields.getPowerAndString() : EUIRM.strings.numNoun(getAmountRawString(), fields.getPowerAndString());
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String pay = extra > 0 ? TEXT.subjects_upToX(extra) : TEXT.subjects_all;
        String payString = target == PCLCardTarget.None || (target == PCLCardTarget.Single && !isFromCreature()) ? TEXT.act_pay(pay, fields.getPowerString()) : TEXT.act_removeFrom(EUIRM.strings.numNoun(pay, fields.getPowerString()), getTargetStringPerspective(perspective));
        return payString + EFFECT_SEPARATOR + TEXT.cond_xPerY(childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) : "",
                getSubText(perspective, requestor) + getXRawString()) + PCLCoreStrings.period(addPeriod);
    }

    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (shouldPay && childEffect != null) {
            useImpl(info, order, () -> childEffect.use(info, order));
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect != null) {
            useImpl(info, order, () -> childEffect.use(info, order));
        }
    }

    protected void useImpl(PCLUseInfo info, PCLActions order, ActionT0 callback) {
        AbstractCreature sourceCreature = getSourceCreature();
        if (extra > 0) {
            ArrayList<ApplyOrReducePowerAction> actions = EUIUtils.flattenList(EUIUtils.map(getTargetList(info), t ->
                    EUIUtils.mapAsNonnull(fields.powers, id -> {
                        PCLPowerData power = PCLPowerData.getStaticDataOrCustom(id);
                        return power != null ? new ApplyOrReducePowerAction(sourceCreature, t, power, -extra) : null;
                    })));
            order.sequential(actions).addCallback(() -> {
                info.setData(EUIUtils.map(actions, ApplyOrReducePowerAction::extractPower));
                callback.invoke();
            });
        }
        else {
            ArrayList<RemoveSpecificPowerAction> actions = EUIUtils.flattenList(EUIUtils.map(getTargetList(info), t ->
                    EUIUtils.map(fields.powers, power -> new RemoveSpecificPowerAction(sourceCreature, t, power))));
            order.callback(new SequentialAction(actions), () -> {
                info.setData(EUIUtils.map(actions, p -> (AbstractPower) EUIClassUtils.getField(p, "powerInstance")));
                callback.invoke();
            });
        }
    }

}
