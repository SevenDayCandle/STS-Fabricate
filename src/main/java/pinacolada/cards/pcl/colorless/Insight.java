package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Insight extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/insight";
    public static final PCLCardData DATA = registerTemplate(Insight.class, com.megacrit.cardcrawl.cards.tempCards.Insight.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Yellow)
            .setTags(PCLCardTag.Purge.make(), PCLCardTag.Retain.make(-1))
            .setColorless();

    public Insight() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.draw(2).setUpgrade(1));
    }
}