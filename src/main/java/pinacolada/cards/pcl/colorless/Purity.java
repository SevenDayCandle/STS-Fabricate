package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Purity extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/purity";
    public static final PCLCardData DATA = registerTemplate(Purity.class, com.megacrit.cardcrawl.cards.colorless.Purity.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Self)
            .setAffinities(PCLAffinity.Yellow)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public Purity() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.exhaust(3).setUpgrade(2));
    }
}