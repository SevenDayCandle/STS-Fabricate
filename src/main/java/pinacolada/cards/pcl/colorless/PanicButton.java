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
public class PanicButton extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/panic_button";
    public static final PCLCardData DATA = registerTemplate(PanicButton.class, com.megacrit.cardcrawl.cards.colorless.PanicButton.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Self)
            .setBlock(30, 10)
            .setAffinities(PCLAffinity.Silver)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public PanicButton() {
        super(DATA);
    }

    public void setup(Object input) {
        addBlockMove();
        addUseMove(PMove.gain(2, PCLPowerData.NoBlock));
    }
}