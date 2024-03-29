package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Miracle extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/miracle";
    public static final PCLCardData DATA = registerTemplate(Miracle.class, com.megacrit.cardcrawl.cards.tempCards.Miracle.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Yellow)
            .setTags(PCLCardTag.Purge.make(), PCLCardTag.Retain.make(-1))
            .setColorless();

    public Miracle() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.gainEnergy(1).setUpgrade(1));
    }
}