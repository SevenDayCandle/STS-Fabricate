package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;
import pinacolada.skills.fields.PField_CardGeneric;

@VisibleCard
public class Madness extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/madness";
    public static final PCLCardData DATA = register(Madness.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(1, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Star)
            .setCostUpgrades(-1)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public Madness() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.modifyCost(-5, 1, PCLCardGroupHelper.Hand).edit(PField_CardGeneric::setRandom));
    }
}