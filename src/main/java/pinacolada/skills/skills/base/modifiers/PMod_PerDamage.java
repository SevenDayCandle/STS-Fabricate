package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import static pinacolada.skills.PSkill.PCLEffectType.General;

public class PMod_PerDamage extends PMod
{

    public static final PSkillData DATA = register(PMod_PerDamage.class, General).selfTarget();

    public PMod_PerDamage()
    {
        this(1);
    }

    public PMod_PerDamage(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerDamage(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount * (sourceCard != null ? sourceCard.damage : 0) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", TEXT.subjects.damage);
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? TEXT.subjects.damage : EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects.damage);
    }
}
