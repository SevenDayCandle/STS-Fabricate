package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PTrigger;

@VisibleCard(add = false)
public class Magnetism extends PCLCard {
    public static final String ATLAS_URL = "colorless/power/magnetism";
    public static final PCLCardData DATA = registerTemplate(Magnetism.class, com.megacrit.cardcrawl.cards.colorless.Magnetism.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setPower(2, CardRarity.RARE)
            .setAffinities(PCLAffinity.Yellow)
            .setCostUpgrades(-1)
            .setColorless();

    public Magnetism() {
        super(DATA);
    }

    public void setup(Object input) {
        addGainPower(PTrigger.when(PCond.onTurnStart(), PMove.createRandom(1, 1, PCLCardGroupHelper.Hand).edit(f -> f.setColor(CardColor.COLORLESS))));
    }
}