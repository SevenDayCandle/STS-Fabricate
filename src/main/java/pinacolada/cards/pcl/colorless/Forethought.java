package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;

@VisibleCard
public class Forethought extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/forethought";
    public static final PCLCardData DATA = registerTemplate(Forethought.class, com.megacrit.cardcrawl.cards.colorless.Forethought.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Blue)
            .setColorless();

    public Forethought() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.reshuffle(1, PCLCardGroupHelper.Hand)
                .setUpgrade(-1)
                .edit(f -> f.setDestination(PCLCardSelection.Bottom)), PMove.modifyCostExact(0).edit(f -> f.setOr(true)).useParent(true));
    }
}