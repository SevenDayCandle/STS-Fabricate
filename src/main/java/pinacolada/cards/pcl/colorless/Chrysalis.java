package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard
public class Chrysalis extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/chrysalis";
    public static final PCLCardData DATA = registerTemplate(Chrysalis.class, com.megacrit.cardcrawl.cards.colorless.Chrysalis.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(2, CardRarity.RARE)
            .setTags(PCLCardTag.Exhaust)
            .setAffinities(PCLAffinity.Blue)
            .setColorless();

    public Chrysalis() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.createRandom(3, 3, PCLCardGroupHelper.DrawPile).setUpgrade(2).setUpgradeExtra(2).edit(f -> f.setType(CardType.SKILL)), PMove.modifyCostExactForTurn(0, 99).useParent(true));
    }
}
