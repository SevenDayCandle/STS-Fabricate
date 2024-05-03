package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PDelay;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class TheBomb extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/the_bomb";
    public static final PCLCardData DATA = registerTemplate(TheBomb.class, com.megacrit.cardcrawl.cards.colorless.TheBomb.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(2, CardRarity.UNCOMMON, PCLCardTarget.Self)
            .setAffinities(PCLAffinity.Red)
            .setColorless();

    public TheBomb() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PDelay.turnEnd(2), PMove.dealDamageToAll(40).setUpgrade(10));
    }
}