package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard
public class Apotheosis extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/apotheosis";
    public static final PCLCardData DATA = registerTemplate(Apotheosis.class, com.megacrit.cardcrawl.cards.colorless.Apotheosis.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(2, CardRarity.RARE, PCLCardTarget.None)
            .setTags(PCLCardTag.Exhaust)
            .setAffinities(2, PCLAffinity.Yellow)
            .setCostUpgrades(-1)
            .setColorless();

    public Apotheosis() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.upgrade(0, PCLCardGroupHelper.Hand, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.DrawPile).edit(f -> f.setForced(true)));
    }
}