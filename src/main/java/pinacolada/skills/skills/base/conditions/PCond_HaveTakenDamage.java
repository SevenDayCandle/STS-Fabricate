package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnAttackSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_HaveTakenDamage extends PPassiveCond<PField_Random> implements OnAttackSubscriber {
    public static final PSkillData<PField_Random> DATA = register(PCond_HaveTakenDamage.class, PField_Random.class);

    public PCond_HaveTakenDamage() {
        this(1);
    }

    public PCond_HaveTakenDamage(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PCond_HaveTakenDamage(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    public PCond_HaveTakenDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        // TODO last damage taken this combat check on monster
        return evaluateTargets(info, m -> fields.doesValueMatchThreshold(
                m instanceof AbstractPlayer ?
                        (fields.random ? GameActionManager.damageReceivedThisCombat : GameActionManager.damageReceivedThisTurn)
                        : m.lastDamageTaken, refreshAmount(info)));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(PGR.core.tooltips.attack.past()) : TEXT.cond_ifX(TEXT.act_takeDamage(TEXT.subjects_x));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            return getWheneverAreString(PGR.core.tooltips.attack.past(), perspective);
        }
        String base = TEXT.cond_ifTargetTook(getTargetStringPerspective(perspective), EUIRM.strings.numNoun(getAmountRawString(requestor), TEXT.subjects_damage));
        return fields.random ? TEXT.subjects_thisCombat(base) : TEXT.subjects_thisTurn(base);
    }

    // When the owner receives damage, triggers the effect onto the attacker (info.owner)
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature receiver) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo pInfo = generateInfo(owner, info.owner);
        boolean eval = evaluateTargets(pInfo, c -> c == receiver);
        if (info.type == DamageInfo.DamageType.NORMAL && eval) {
            useFromTrigger(pInfo.setData(info.output), isFromCreature() ? PCLActions.bottom : PCLActions.top);
        }
    }
}
