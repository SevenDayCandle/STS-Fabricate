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
public class JackOfAllTrades extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/jack_of_all_trades";
    public static final PCLCardData DATA = registerTemplate(JackOfAllTrades.class, com.megacrit.cardcrawl.cards.colorless.JackOfAllTrades.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Self)
            .setAffinities(PCLAffinity.Star)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public JackOfAllTrades() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.createRandom(1, 1, PCLCardGroupHelper.Hand).setUpgrade(1).edit(f -> f.setColor(CardColor.COLORLESS)));
    }
}