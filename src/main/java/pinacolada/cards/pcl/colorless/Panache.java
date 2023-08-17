package pinacolada.cards.pcl.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PTrigger;

@VisibleCard
public class Panache extends PCLCard {
    public static final String ATLAS_URL = "colorless/power/panache";
    public static final PCLCardData DATA = registerTemplate(Panache.class, com.megacrit.cardcrawl.cards.colorless.Panache.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setPower(0, CardRarity.RARE)
            .setAffinities(PCLAffinity.Yellow)
            .setColorless();

    public Panache() {
        super(DATA);
    }

    public void setup(Object input) {
        addGainPower(PTrigger.whenEveryTimes(5, PCond.onOtherCardPlayed(),
                PMove.dealDamageToAll(10).setUpgrade(4)));
    }
}