package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PMove;

@VisibleCard
public class Apparition extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/apparition";
    public static final PCLCardData DATA = registerTemplate(Apparition.class, com.megacrit.cardcrawl.cards.colorless.Apparition.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(1, CardRarity.SPECIAL, PCLCardTarget.Self)
            .setAffinities(PCLAffinity.Purple)
            .setTags(PCLCardTag.Ethereal.make(1, 0), PCLCardTag.Exhaust.make())
            .setColorless();

    public Apparition() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.gain(1, PCLPowerData.Intangible));
    }
}