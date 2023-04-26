package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMod;
import pinacolada.skills.PMove;

@VisibleCard
public class Transmutation extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/transmutation";
    public static final PCLCardData DATA = register(Transmutation.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(-1, CardRarity.RARE, PCLCardTarget.None)
            .setTags(PCLCardTag.Exhaust)
            .setAffinities(PCLAffinity.Blue, PCLAffinity.Yellow)
            .setColorless();

    public Transmutation() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMod.xEnergy(0).setUpgrade(1), PMove.createRandom(1, 1, PCLCardGroupHelper.Hand).edit(f -> f.setColor(CardColor.COLORLESS)), PMove.modifyCost(-5, 99).useParent(true));
    }
}