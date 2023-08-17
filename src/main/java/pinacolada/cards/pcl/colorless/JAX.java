package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;

@VisibleCard
public class JAX extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/jax";
    public static final PCLCardData DATA = registerTemplate(JAX.class, com.megacrit.cardcrawl.cards.colorless.JAX.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.Self)
            .setAffinities(PCLAffinity.Red, PCLAffinity.Blue)
            .setColorless();

    public JAX() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.loseHp(PCLCardTarget.Self, 3));
        addUseMove(PMove.gain(2, PCLPowerHelper.Strength).setUpgrade(1));
    }
}