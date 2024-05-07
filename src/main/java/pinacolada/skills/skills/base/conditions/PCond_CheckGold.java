package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnGoldChangedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckGold extends PPassiveCond<PField_Not> implements OnGoldChangedSubscriber {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckGold.class, PField_Not.class).noTarget();

    public PCond_CheckGold(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckGold() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckGold(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, m -> fields.doesValueMatchThreshold(info, m.gold));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.gold.title)) : EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.gold.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String baseString = fields.getThresholdRawString(PGR.core.tooltips.gold.title, requestor);
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_gainOrdinal(getTargetOrdinalPerspective(perspective), baseString), perspective);
        }

        return getTargetHasStringPerspective(perspective, baseString);
    }

    @Override
    public int onGoldChanged(int amount) {
        AbstractCreature owner = getOwnerCreature();
        PCLUseInfo info = generateInfo(owner);
        if (fields.doesValueMatchThreshold(amount, refreshAmount(info))) {
            useFromTrigger(info.setData(amount));
        }
        return amount;
    }
}
