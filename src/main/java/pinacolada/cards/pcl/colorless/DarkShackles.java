package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.common.ShacklesPower;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class DarkShackles extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/dark_shackles";
    public static final PCLCardData DATA = registerTemplate(DarkShackles.class, com.megacrit.cardcrawl.cards.colorless.DarkShackles.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Single)
            .setAffinities(PCLAffinity.Purple)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public DarkShackles() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.applyToSingle(9, ShacklesPower.DATA).setUpgrade(6));
    }
}