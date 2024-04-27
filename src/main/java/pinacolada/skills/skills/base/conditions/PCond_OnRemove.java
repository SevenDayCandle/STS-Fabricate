package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnRemovePowerSubscriber;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.skills.skills.PDelegateCond;

import java.util.Collections;

@VisibleSkill
public class PCond_OnRemove extends PDelegateCond<PField_Power> implements OnRemovePowerSubscriber {
    public static final PSkillData<PField_Power> DATA = register(PCond_OnRemove.class, PField_Power.class, 1, 1);

    public PCond_OnRemove() {
        super(DATA);
    }

    public PCond_OnRemove(PCLCardTarget target, PCLPowerData... powers) {
        super(DATA, target, 1);
        fields.setPower(powers);
    }

    public PCond_OnRemove(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isBranch()) {
            return getWheneverYouString(PGR.core.tooltips.remove.title);
        }
        if (isWhenClause()) {
            return TEXT.cond_aObjectIsOn(fields.getPowerOrString(), PGR.core.tooltips.remove.past(), getTargetStringPerspective(perspective));
        }
        return TEXT.cond_onGeneric(source instanceof AbstractOrb || requestor instanceof PCLDynamicOrbData ? PGR.core.tooltips.evoke.title : PGR.core.tooltips.remove.title);
    }

    @Override
    public void onRemovePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo info = generateInfo(owner, target);
        boolean eval = evaluateTargets(info, c -> c == target);
        if (fields.getPowerFilter().invoke(power) && eval) {
            info.setTempTargets(target);
            useFromTrigger(info.setData(power));
        }
    }

    @Override
    public void triggerOnRemove(Object o) {
        useFromTrigger(generateInfo(null).setData(Collections.singletonList(o)));
    }
}
