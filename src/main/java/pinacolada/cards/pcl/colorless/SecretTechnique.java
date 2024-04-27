package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class SecretTechnique extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/secret_technique";
    public static final PCLCardData DATA = registerTemplate(SecretTechnique.class, com.megacrit.cardcrawl.cards.colorless.SecretTechnique.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.RARE, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Green)
            .setRTags(PCLCardTag.Exhaust)
            .setColorless();

    public SecretTechnique() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.fetch(1, PCLCardGroupHelper.DrawPile).edit(f -> f.setType(CardType.SKILL)));
    }
}