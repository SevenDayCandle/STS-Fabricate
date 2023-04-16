package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMod_PerUnblockedDamage extends PMod_Per<PField_Not>
{

    public static final PSkillData<PField_Not> DATA = register(PMod_PerUnblockedDamage.class, PField_Not.class).selfTarget();

    public PMod_PerUnblockedDamage()
    {
        this(1);
    }

    public PMod_PerUnblockedDamage(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerUnblockedDamage(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {

        PCardPrimary_DealDamage damageEff = sourceCard != null ? source.getCardDamage() : null;
        if (damageEff != null && damageEff.target != null)
        {
            List<AbstractCreature> targetList = damageEff.getTargetList(info);
            return EUIUtils.sumInt(targetList, t -> damageEff.extra * GameUtilities.getHealthBarAmount(t, damageEff.amount, true, false));
        }
        return 0;
    }

    @Override
    public String getSubText()
    {
        return TEXT.subjects_unblocked(TEXT.subjects_damage);
    }
}
