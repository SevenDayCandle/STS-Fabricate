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
public class PMod_PerCreatureMaxHP extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerCreatureMaxHP.class, PField_Not.class);

    public PMod_PerCreatureMaxHP(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureMaxHP() {
        super(DATA);
    }

    public PMod_PerCreatureMaxHP(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureMaxHP(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return sumTargets(info, t -> t.maxHealth);
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.maxHP.title;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return getTargetOnStringPerspective(perspective, getSubSampleText());
    }
}
