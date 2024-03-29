package pinacolada.skills.skills.base.modifiers;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerUnblockedDamage extends PMod_Per<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PMod_PerUnblockedDamage.class, PField_Not.class);

    public PMod_PerUnblockedDamage() {
        this(1);
    }

    public PMod_PerUnblockedDamage(int amount) {
        super(DATA, PCLCardTarget.Single, amount);
    }

    public PMod_PerUnblockedDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        // Source of truth should be the last taken damage obtained from the effects above this
        if (isUsing) {
            return sumTargets(info, t -> t.lastDamageTaken) / PGR.dungeon.getDivisor();
        }

        // Otherwise, estimate from the damage effect
        PCardPrimary_DealDamage damageEff = source instanceof PointerProvider ? ((PointerProvider) source).getCardDamage() : null;
        if (damageEff != null && damageEff.target != null) {
            return sumTargets(info, t -> damageEff.extra * GameUtilities.getHealthBarAmount(t, damageEff.amount, true, false)) / PGR.dungeon.getDivisor();
        }
        return 0;
    }

    public String getSubSampleText() {
        return TEXT.subjects_unblocked(TEXT.subjects_damage);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return getTargetOnStringPerspective(perspective, TEXT.subjects_unblocked(TEXT.subjects_damage));
    }

    // Must be called through a callback to ensure lastDamageTaken is properly recorded by attacks
    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(__ -> {
            super.use(info, order);
        });
    }
}
