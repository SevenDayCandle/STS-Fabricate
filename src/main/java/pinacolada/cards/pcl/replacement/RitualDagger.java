package pinacolada.cards.pcl.replacement;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.skills.PLimit;
import pinacolada.skills.skills.base.conditions.PCond_Fatal;
import pinacolada.skills.skills.base.moves.PMove_RemovePower;
import pinacolada.skills.skills.special.moves.PMove_PermanentUpgrade;

@VisibleCard
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
                PLimit.limited(), new PCond_Fatal(), new PMove_PermanentUpgrade(1)
        );
    }
}