package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerCreatureDamage extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerCreatureDamage.class, PField_Not.class);

    public PMod_PerCreatureDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureDamage() {
        super(DATA);
    }

    public PMod_PerCreatureDamage(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureDamage(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(TEXT.subjects_target, TEXT.subjects_damage);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return sumTargets(info, t -> t instanceof AbstractMonster ? PCLIntentInfo.get((AbstractMonster) t).getDamage(true) : 0);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return getTargetOnStringPerspective(perspective, TEXT.subjects_damage);
    }
}
