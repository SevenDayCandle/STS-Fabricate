package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Blind extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/blind";
    public static final PCLCardData DATA = registerTemplate(Blind.class, com.megacrit.cardcrawl.cards.colorless.Blind.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Single)
            .setAffinities(PCLAffinity.Yellow)
            .setMultiformData(2)
            .setBranchFactor(1)
            .setColorless();

    public Blind() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.applyToSingle(2, PCLPowerData.Weak).setCustomUpgrade((skill, form, upgrade) -> {
            skill.setTarget(form == 1 ? PCLCardTarget.AllEnemy : PCLCardTarget.Single);
        }));
    }
}