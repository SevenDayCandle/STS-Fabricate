package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.DamageInfo;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PTrait_TakeDamage extends PTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_TakeDamage.class, PField_Empty.class)
            .setSourceTypes(PSkillData.SourceType.Power);

    public PTrait_TakeDamage() {
        this(1);
    }

    public PTrait_TakeDamage(int amount) {
        super(DATA, amount);
    }

    public PTrait_TakeDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_take(getSampleAmount(), getSubSampleText());
    }

    @Override
    public String getSubText() {
        return TEXT.act_takeDamage(getAmountRawString());
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
    public float modifyDamageIncoming(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        return amount + this.amount;
    }
}