package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Trip extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/trip";
    public static final PCLCardData DATA = registerTemplate(Trip.class, com.megacrit.cardcrawl.cards.colorless.Trip.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Single)
            .setAffinities(PCLAffinity.Green)
            .setMultiformData(2)
            .setBranchFactor(1)
            .setColorless();

    public Trip() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.applyToSingle(2, PCLPowerData.Vulnerable).setCustomUpgrade((skill, form, upgrade) -> {
            skill.setTarget(form == 1 ? PCLCardTarget.AllEnemy : PCLCardTarget.Single);
        }));
    }
}