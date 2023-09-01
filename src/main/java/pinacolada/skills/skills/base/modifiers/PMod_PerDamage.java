package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerDamage extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerDamage.class, PField_Not.class).noTarget();

    public PMod_PerDamage() {
        this(1);
    }

    public PMod_PerDamage(int amount) {
        super(DATA, amount);
    }

    public PMod_PerDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return (sourceCard != null ? sourceCard.damage / PGR.dungeon.getDivisor() : 0);
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(TEXT.subjects_card, TEXT.subjects_damage);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.subjects_damage;
    }
}
