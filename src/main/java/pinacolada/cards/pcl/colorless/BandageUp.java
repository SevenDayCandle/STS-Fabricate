package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.base.moves.PMove_Shuffle;

@VisibleCard(add = false)
public class BandageUp extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/bandage_up";
    public static final PCLCardData DATA = registerTemplate(BandageUp.class, com.megacrit.cardcrawl.cards.colorless.BandageUp.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Self)
            .setAffinities(PCLAffinity.Blue, PCLAffinity.Yellow)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public BandageUp() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.heal(4).setUpgrade(2));
    }
}