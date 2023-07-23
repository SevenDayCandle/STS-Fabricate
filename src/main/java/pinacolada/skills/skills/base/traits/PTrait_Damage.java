package pinacolada.skills.skills.base.traits;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDamageTrait;

@VisibleSkill
public class PTrait_Damage extends PDamageTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_Damage.class, PField_Empty.class)
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
    public String getSubText(PCLCardTarget perspective) {
        if (isVerbose()) {
            return TEXT.act_deal(getAmountRawString(), getSubDescText());
        }
        return super.getSubText(perspective);
    }

    @Override
    public String getSubDescText() {
        return getAttackTooltip().getTitleOrIcon();
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
        return amount + this.amount;
    }
}
