package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.base.moves.PMove_Shuffle;

@VisibleCard(add = false)
public class DeepBreath extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/deep_breath";
    public static final PCLCardData DATA = registerTemplate(DeepBreath.class, com.megacrit.cardcrawl.cards.colorless.DeepBreath.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Blue)
            .setColorless();

    public DeepBreath() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(new PMove_Shuffle());
        addUseMove(PMove.draw(1).setUpgrade(1));
    }
}