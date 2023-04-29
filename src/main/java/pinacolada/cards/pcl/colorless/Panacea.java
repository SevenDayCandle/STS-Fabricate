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
public class Panacea extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/panacea";
    public static final PCLCardData DATA = registerTemplate(Panacea.class, com.megacrit.cardcrawl.cards.colorless.Panacea.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Self)
            .setAffinities(PCLAffinity.Blue, PCLAffinity.Yellow)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public Panacea() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.gain(1, PCLPowerHelper.Artifact).setUpgrade(1));
    }
}