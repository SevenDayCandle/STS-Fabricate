package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class HandOfGreed extends PCLCard {
    public static final String ATLAS_URL = "colorless/attack/hand_of_greed";
    public static final PCLCardData DATA = registerTemplate(HandOfGreed.class, com.megacrit.cardcrawl.cards.colorless.HandOfGreed.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setAttack(2, CardRarity.RARE, PCLAttackType.Normal, PCLCardTarget.Single)
            .setDamage(20, 5)
            .setAffinities(PCLAffinity.Orange)
            .setColorless();

    public HandOfGreed() {
        super(DATA);
    }

    public void setup(Object input) {
        addDamageMove(PCLAttackVFX.BLUNT_HEAVY);
        addUseMove(PCond.fatal(), PMove.gainGold(20).setUpgrade(5));
    }
}