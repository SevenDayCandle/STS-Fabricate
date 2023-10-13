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

@VisibleCard(add = false)
public class SadisticNature extends PCLCard {
    public static final String ATLAS_URL = "colorless/power/sadistic_nature";
    public static final PCLCardData DATA = registerTemplate(SadisticNature.class, com.megacrit.cardcrawl.cards.colorless.SadisticNature.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setPower(0, CardRarity.RARE)
            .setAffinities(PCLAffinity.Purple)
            .setColorless();

    public SadisticNature() {
        super(DATA);
    }

    public void setup(Object input) {
        addGainPower(PTrigger.when(PCond.checkDistinctPower(PCLCardTarget.RandomEnemy, 1).edit(f -> f.setDebuff(true)),
                PMove.dealDamage(5, AbstractGameAction.AttackEffect.POISON, PCLCardTarget.UseParent).setUpgrade(2)));
    }
}