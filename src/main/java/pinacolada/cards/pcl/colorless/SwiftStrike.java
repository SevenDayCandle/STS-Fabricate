package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.effects.PCLAttackVFX;

@VisibleCard(add = false)
public class SwiftStrike extends PCLCard {
    public static final String ATLAS_URL = "colorless/attack/swift_strike";
    public static final PCLCardData DATA = registerTemplate(SwiftStrike.class, com.megacrit.cardcrawl.cards.colorless.SwiftStrike.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setAttack(0, CardRarity.UNCOMMON)
            .setDamage(7, 3)
            .setAffinities(PCLAffinity.Green)
            .setColorless();

    public SwiftStrike() {
        super(DATA);
    }

    public void setup(Object input) {
        addDamageMove(PCLAttackVFX.BLUNT_LIGHT);
    }
}