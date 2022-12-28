package pinacolada.cards.pcl.replacement;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.conditions.PCond_Fatal;
import pinacolada.skills.skills.base.moves.PMove_RemovePower;
import pinacolada.skills.skills.special.moves.PMove_PermanentUpgrade;

public class RitualDagger extends PCLCard
{
    public static final PCLCardData DATA = register(RitualDagger.class)
            .setAttack(1, CardRarity.SPECIAL, PCLAttackType.Normal, PCLCardTarget.Single)
            .setDamage(9, 3)
            .setAffinities(PCLAffinity.Yellow, PCLAffinity.Purple)
            .setTags(PCLCardTag.Exhaust)
            .setUnique(true, -1)
            .setColorless();

    public RitualDagger()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addDamageMove(AbstractGameAction.AttackEffect.NONE).setDamageEffect(PCLEffekseerEFX.SWORD16);
        addUseMove(
                new PMove_RemovePower(PCLCardTarget.Single, PCLPowerHelper.Intangible, PCLPowerHelper.Artifact));
        addUseMove(
                PCond.limited(), new PCond_Fatal(), new PMove_PermanentUpgrade(1)
        );
    }
}