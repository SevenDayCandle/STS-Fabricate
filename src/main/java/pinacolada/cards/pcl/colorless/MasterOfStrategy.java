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
public class MasterOfStrategy extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/master_of_strategy";
    public static final PCLCardData DATA = registerTemplate(MasterOfStrategy.class, com.megacrit.cardcrawl.cards.colorless.MasterOfStrategy.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.RARE, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Blue)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public MasterOfStrategy() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.draw(3).setUpgrade(1));
    }
}