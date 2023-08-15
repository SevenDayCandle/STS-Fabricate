package pinacolada.skills.skills.base.modifiers;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerCreatureHP extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerCreatureHP.class, PField_Not.class);

    public PMod_PerCreatureHP(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureHP() {
        super(DATA);
    }

    public PMod_PerCreatureHP(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureHP(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return sumTargets(info, t -> t.currentHealth + TempHPField.tempHp.get(t));
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return getTargetOnStringPerspective(perspective, getSubSampleText());
    }
}
