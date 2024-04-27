package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Impatience extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/impatience";
    public static final PCLCardData DATA = registerTemplate(Impatience.class, com.megacrit.cardcrawl.cards.colorless.Impatience.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.RARE, PCLCardTarget.None)
            .setColorless();

    public Impatience() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PCond.pileHas(0, PCLCardGroupHelper.Hand).edit(f -> f.setType(CardType.ATTACK)), PMove.draw(2).setUpgrade(1));
    }
}