package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnIntensifySubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.skills.skills.PPassiveCond;

import java.util.ArrayList;

@VisibleSkill
public class PCond_CheckLevel extends PPassiveCond<PField_Affinity> implements OnIntensifySubscriber {
    public static final PSkillData<PField_Affinity> DATA = register(PCond_CheckLevel.class, PField_Affinity.class)
            .pclOnly()
            .selfTarget();

    public PCond_CheckLevel() {
        this(1);
    }

    public PCond_CheckLevel(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckLevel(int amount, PCLAffinity... stance) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setAffinity(stance);
    }

    public int getQualifierRange() {
        return fields.getQualiferRange();
    }

    public String getQualifierText(int i) {
        return fields.getQualifierText(i);
    }

    public ArrayList<Integer> getQualifiers(PCLUseInfo info, boolean conditionPassed) {
        return fields.getQualifiers(info);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        if (fields.affinities.isEmpty()) {
            return fields.not ^ CombatManager.playerSystem.getLevel(PCLAffinity.General) >= amount;
        }
        else {
            for (PCLAffinity affinity : fields.affinities) {
                if (CombatManager.playerSystem.getLevel(affinity) < amount) {
                    return fields.not;
                }
            }
        }
        return !fields.not;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_levelItem(TEXT.subjects_x, PGR.core.tooltips.affinityGeneral.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isBranch()) {
            return TEXT.cond_wheneverYou(PGR.core.tooltips.level.title);
        }
        if (isWhenClause()) {
            return TEXT.cond_wheneverYou(EUIRM.strings.verbNoun(PGR.core.tooltips.level.title, fields.getAffinityChoiceString()));
        }
        return TEXT.cond_ifX(TEXT.cond_levelItem(getAmountRawString(), fields.getAffinityChoiceString()));
    }

    @Override
    public void onIntensify(PCLAffinity aff) {
        if (fields.affinities.isEmpty() || fields.affinities.contains(aff)) {
            useFromTrigger(generateInfo(null).setData(aff));
        }
    }
}
