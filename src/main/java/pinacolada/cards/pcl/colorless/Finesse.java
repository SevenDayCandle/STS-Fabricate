package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Finesse extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/finesse";
    public static final PCLCardData DATA = registerTemplate(Finesse.class, com.megacrit.cardcrawl.cards.colorless.Finesse.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON)
            .setDamage(2, 2)
            .setColorless();

    public Finesse() {
        super(DATA);
    }

    public void setup(Object input) {
        addBlockMove();
        addUseMove(PMove.draw(1));
    }
}