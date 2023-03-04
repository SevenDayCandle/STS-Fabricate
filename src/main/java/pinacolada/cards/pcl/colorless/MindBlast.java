package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PMod;
import pinacolada.skills.PTrait;

@VisibleCard
public class MindBlast extends PCLCard
{
    public static final String ATLAS_URL = "colorless/attack/mind_blast";
    public static final PCLCardData DATA = register(MindBlast.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setAttack(2, CardRarity.UNCOMMON, PCLAttackType.Piercing, PCLCardTarget.Single)
            .setDamage(3, 0)
            .setAffinities(2, PCLAffinity.Blue)
            .setCostUpgrades(-1)
            .setColorless();

    public MindBlast()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addDamageMove().setChain(PMod.perCard(1, PCLCardGroupHelper.DrawPile).setUpgrade(-1), PTrait.damage(1));
    }
}