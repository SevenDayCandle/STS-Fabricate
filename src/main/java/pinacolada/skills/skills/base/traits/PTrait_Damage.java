package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PStatTrait;

@VisibleSkill
public class PTrait_Damage extends PStatTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_Damage.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_Damage() {
        this(1);
    }

    public PTrait_Damage(int amount) {
        super(DATA, amount);
    }

    public PTrait_Damage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective, Object requestor) {
        return EUIRM.strings.numNoun(getAmountRawString(requestor), getDamageString(perspective));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_damage;
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public float modifyDamageGiveFirst(PCLUseInfo info, float amount) {
        int actualAmount = refreshAmount(info);
        return fields.not ? actualAmount : amount + actualAmount;
    }
}
