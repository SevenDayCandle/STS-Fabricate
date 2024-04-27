package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class FlashOfSteel extends PCLCard {
    public static final String ATLAS_URL = "colorless/attack/flash_of_steel";
    public static final PCLCardData DATA = registerTemplate(FlashOfSteel.class, com.megacrit.cardcrawl.cards.colorless.FlashOfSteel.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setAttack(0, CardRarity.UNCOMMON)
            .setDamage(3, 3)
            .setColorless();

    public FlashOfSteel() {
        super(DATA);
    }

    public void setup(Object input) {
        addDamageMove(PCLAttackVFX.SLASH_HORIZONTAL);
        addUseMove(PMove.draw(1));
    }
}