package pinacolada.skills.skills.base.conditions;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_HP extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_HP.class, PField_Not.class);

    public PCond_HP(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_HP() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_HP(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PCond_HP(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, t -> fields.not ? (t.currentHealth + TempHPField.tempHp.get(t)) <= amount : (t.currentHealth + TempHPField.tempHp.get(t)) >= amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_generic2(PGR.core.tooltips.hp.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String baseString = amount + (fields.not ? "- " : "+ ") + PGR.core.tooltips.hp.title;
        return getTargetHasStringPerspective(perspective, baseString);
    }
}
