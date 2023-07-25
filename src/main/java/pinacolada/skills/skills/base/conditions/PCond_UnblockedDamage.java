package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnAttackSubscriber;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveNonCheckCond;
import pinacolada.skills.skills.PLimit;

import java.util.HashMap;

@VisibleSkill
public class PCond_UnblockedDamage extends PActiveNonCheckCond<PField_Not> implements OnAttackSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PCond_UnblockedDamage.class, PField_Not.class, 1, 1);

    public PCond_UnblockedDamage() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_UnblockedDamage(PCLCardTarget target) {
        super(DATA, target, 0);
    }

    public PCond_UnblockedDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_whenSingle(TEXT.act_deals(TEXT.subjects_unblocked(TEXT.subjects_x))) : super.getSampleText(callingSkill, parentSkill);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String baseString = TEXT.subjects_unblocked(TEXT.subjects_damage);
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_deal(TEXT.subjects_any, baseString), perspective);
        }

        return TEXT.cond_ifTargetTook(getTargetSubjectString(target), baseString);
    }

    // When the owner deals unblocked damage, triggers the effect on the target
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature t) {
        PCLUseInfo pInfo = generateInfo(t);
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL && target.getTargets(getOwnerCreature(), t, pInfo.targetList).contains(info.owner)) {
            useFromTrigger(pInfo.setData(damageAmount), isFromCreature() ? PCLActions.bottom : PCLActions.top);
        }
    }

    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        // Checks to see if any of the targets' health is decreased after this card is used
        HashMap<? extends AbstractCreature, Integer> healthMap = EUIUtils.hashMap(getTargetList(info), c -> c.currentHealth);
        return PCLActions.last.callback(healthMap, (targets, __) -> {
            if (EUIUtils.any(targets.keySet(), t -> t.currentHealth < targets.get(t)) && (!(parent instanceof PLimit) || ((PLimit) parent).tryActivate(info))) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        });
    }
}
