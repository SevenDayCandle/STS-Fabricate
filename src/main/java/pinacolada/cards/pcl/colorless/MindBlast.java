package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.skills.PMod;

@VisibleCard(add = false)
public class MindBlast extends PCLCard {
    public static final String ATLAS_URL = "colorless/attack/mind_blast";
    public static final PCLCardData DATA = registerTemplate(MindBlast.class, com.megacrit.cardcrawl.cards.colorless.MindBlast.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setAttack(2, CardRarity.UNCOMMON, PCLAttackType.Ranged, PCLCardTarget.Single)
            .setDamage(0, 0)
            .setAffinities(2, PCLAffinity.Blue)
            .setTags(PCLCardTag.Innate)
            .setCostUpgrades(-1)
            .setColorless();

    public MindBlast() {
        super(DATA);
    }

    public void setup(Object input) {
        addDamageMove(PCLAttackVFX.SMALL_LASER).setBonus(PMod.perCard(1, PCLCardGroupHelper.DrawPile), 1);
    }
}