package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.DamageInfo;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PTrait_TakeDamageMultiplier extends PTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_TakeDamageMultiplier.class, PField_Empty.class)
            .setSourceTypes(PSkillData.SourceType.Power);

    public PTrait_TakeDamageMultiplier() {
        this(1);
    }

    public PTrait_TakeDamageMultiplier(int amount) {
        super(DATA, amount);
    }

    public PTrait_TakeDamageMultiplier(PSkillSaveData content) {
        super(DATA, content);
    }

    public String getSampleAmount() {
        return "+X%";
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_take(getSampleAmount(), getSubSampleText());
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_takeDamage(getAmountRawString() + "%");
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
        return amount > 0;
    }

    @Override
    public float modifyDamageReceiveFirst(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        return amount * (1 + (this.amount / 100f));
    }
}
