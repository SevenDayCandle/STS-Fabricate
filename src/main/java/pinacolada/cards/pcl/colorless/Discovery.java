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
public class Discovery extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/discovery";
    public static final PCLCardData DATA = registerTemplate(Discovery.class, com.megacrit.cardcrawl.cards.colorless.Discovery.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(1, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Blue, PCLAffinity.Orange)
            .setRTags(PCLCardTag.Exhaust)
            .setColorless();

    public Discovery() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.createRandom(1, 3, PCLCardGroupHelper.Hand), PMove.modifyCostExactForTurn(0).useParent(true));
    }
}