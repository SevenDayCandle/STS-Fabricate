package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerUnblockedHit extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerUnblockedHit.class, PField_Not.class).selfTarget();

    public PMod_PerUnblockedHit() {
        this(1);
    }

    public PMod_PerUnblockedHit(int amount) {
        super(DATA, amount);
    }

    public PMod_PerUnblockedHit(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        int total = 0;
        PCardPrimary_DealDamage damageEff = sourceCard != null ? source.getCardDamage() : null;
        if (damageEff != null && damageEff.target != null) {
            for (AbstractCreature t : damageEff.getTargetList(info)) {
                int unblocked = GameUtilities.getHealthBarAmount(t, damageEff.amount, true, false);
                for (int i = 0; i < damageEff.extra; i++) {
                    unblocked -= damageEff.amount;
                    total += 1;
                    if (unblocked <= 0) {
                        break;
                    }
                }
            }
        }
        return total;
    }

    @Override
    public String getSubText() {
        return TEXT.subjects_unblocked(TEXT.subjects_hits);
    }
}
