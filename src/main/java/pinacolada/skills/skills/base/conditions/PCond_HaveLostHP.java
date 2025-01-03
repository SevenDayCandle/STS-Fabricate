package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnLoseHPSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_HaveLostHP extends PPassiveCond<PField_Random> implements OnLoseHPSubscriber {
    public static final PSkillData<PField_Random> DATA = register(PCond_HaveLostHP.class, PField_Random.class);

    public PCond_HaveLostHP() {
        this(1);
    }

    public PCond_HaveLostHP(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PCond_HaveLostHP(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, m -> fields.doesValueMatchThreshold(
                fields.random ? CombatManager.hpLostThisCombat(m) : CombatManager.hpLostThisTurn(m), refreshAmount(info)));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(TEXT.act_loseAmount(TEXT.subjects_x, PGR.core.tooltips.hp.title));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_lose(amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(requestor) + "+", PGR.core.tooltips.hp.title) : PGR.core.tooltips.hp.title), perspective);
        }
        String base = TEXT.cond_ifTargetLost(getTargetStringPerspective(perspective), EUIRM.strings.numNoun(getAmountRawString(requestor), PGR.core.tooltips.hp.title));
        return fields.random ? TEXT.subjects_thisCombat(base) : TEXT.subjects_thisTurn(base);
    }

    @Override
    public int onLoseHP(AbstractCreature p, DamageInfo info, int amount) {
        PCLUseInfo i = generateInfo(getOwnerCreature(), p);
        boolean eval = evaluateTargets(i, c -> c == p);
        if (eval && amount >= refreshAmount(i)) {
            useFromTrigger(generateInfo(getOwnerCreature(), info.owner).setData(amount), isFromCreature() ? PCLActions.bottom : PCLActions.top);
        }
        return amount;
    }
}
