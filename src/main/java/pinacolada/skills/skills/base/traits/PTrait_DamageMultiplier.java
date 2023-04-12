package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDamageTrait;

@VisibleSkill
public class PTrait_DamageMultiplier extends PDamageTrait<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Damage.class, PField_Empty.class);

    public PTrait_DamageMultiplier()
    {
        this(1);
    }

    public PTrait_DamageMultiplier(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PTrait_DamageMultiplier(int amount)
    {
        super(DATA, amount);
    }

    public String getSampleAmount()
    {
        return "+X%";
    }

    @Override
    public String getSubDescText()
    {
        return getAttackTooltip().getTitleOrIcon();
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.subjects_damage;
    }

    @Override
    public float modifyDamage(PCLUseInfo info, float amount)
    {
        return amount * (1 + (this.amount / 100f));
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.numNoun(getAmountRawString() + "%", getSubDescText());
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }
}
