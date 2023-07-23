package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnAttackSubscriber;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_CheckDamage extends PPassiveCond<PField_Not> implements OnAttackSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckDamage.class, PField_Not.class);

    public PCond_CheckDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckDamage() {
        super(DATA, PCLCardTarget.AllEnemy, 1);
    }

    public PCond_CheckDamage(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, m -> fields.doesValueMatchThreshold(m instanceof AbstractMonster ? PCLIntentInfo.get((AbstractMonster) m).getDamage(true) : 0));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_whenSingle(PGR.core.tooltips.attack.present()) : EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.attack.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isWhenClause()) {
            return getWheneverString(TEXT.subjects_attacks(getTargetOrdinal(getTargetForPerspective(perspective))), perspective);
        }
        return getTargetHasStringPerspective(perspective, fields.getThresholdRawString(TEXT.subjects_damage));
    }

    // When the owner attacks, triggers the effect on the target
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature receiver) {
        PCLUseInfo pInfo = generateInfo(receiver);
        if (info.type == DamageInfo.DamageType.NORMAL && target.getTargets(getOwnerCreature(), info.owner, pInfo.targetList).contains(info.owner) && info.output >= amount) {
            useFromTrigger(pInfo.setData(damageAmount));
        }
    }
}
