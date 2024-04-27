package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class DramaticEntrance extends PCLCard {
    public static final String ATLAS_URL = "colorless/attack/dramatic_entrance";
    public static final PCLCardData DATA = registerTemplate(DramaticEntrance.class, com.megacrit.cardcrawl.cards.colorless.DramaticEntrance.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setAttack(0, CardRarity.UNCOMMON, PCLAttackType.Normal, PCLCardTarget.AllEnemy)
            .setDamage(8, 4)
            .setTags(PCLCardTag.Innate, PCLCardTag.Exhaust)
            .setColorless();

    public DramaticEntrance() {
        super(DATA);
    }

    public void setup(Object input) {
        addDamageMove(PCLAttackVFX.SLASH_DIAGONAL);
    }
}