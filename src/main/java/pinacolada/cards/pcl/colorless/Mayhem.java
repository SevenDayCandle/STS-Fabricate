package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PTrigger;

@VisibleCard(add = false)
public class Mayhem extends PCLCard {
    public static final String ATLAS_URL = "colorless/power/mayhem";
    public static final PCLCardData DATA = registerTemplate(Mayhem.class, com.megacrit.cardcrawl.cards.colorless.Mayhem.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setPower(2, CardRarity.RARE)
            .setCostUpgrades(-1)
            .setAffinities(PCLAffinity.Red)
            .setColorless();

    public Mayhem() {
        super(DATA);
    }

    public void setup(Object input) {
        addGainPower(PTrigger.when(PCond.onTurnStart(), PMove.play(1, PCLCardTarget.RandomEnemy, PCLCardGroupHelper.DrawPile).edit(f -> f.setOrigin(PCLCardSelection.Top))));
    }
}