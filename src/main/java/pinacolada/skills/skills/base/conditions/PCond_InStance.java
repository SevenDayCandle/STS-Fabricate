package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnStanceChangedSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Stance;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_InStance extends PPassiveCond<PField_Stance> implements OnStanceChangedSubscriber {

    public static final PSkillData<PField_Stance> DATA = register(PCond_InStance.class, PField_Stance.class, 1, 1)
            .noTarget();

    public PCond_InStance() {
        super(DATA);
    }

    public PCond_InStance(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_InStance(PCLStanceHelper... stance) {
        super(DATA);
        fields.setStance(stance);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return (fields.not || fields.random) ^ (fields.stances.isEmpty() ? !GameUtilities.inStance(NeutralStance.STANCE_ID) : EUIUtils.any(fields.stances, GameUtilities::inStance));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_enterStance(PGR.core.tooltips.stance.title)) : PGR.core.tooltips.stance.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = fields.getAnyStanceString();
        if (isWhenClause()) {
            if (fields.random) {
                return getWheneverYouString(TEXT.act_exitX(base));
            }
            return getWheneverYouString(TEXT.act_enterStance(base));
        }
        return fields.random || fields.not ? TEXT.cond_not(base) : base;
    }

    @Override
    public void onStanceChanged(AbstractStance oldStance, AbstractStance newStance) {
        if (fields.random) {
            if (fields.stances.isEmpty() || fields.stances.contains(PCLStanceHelper.get(oldStance.ID))) {
                useFromTrigger(generateInfo(null).setData(oldStance));
            }
        }
        else if (fields.stances.isEmpty() || fields.stances.contains(PCLStanceHelper.get(newStance.ID))) {
            useFromTrigger(generateInfo(null).setData(newStance));
        }
    }
}
