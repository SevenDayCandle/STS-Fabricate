package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Violence extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/violence";
    public static final PCLCardData DATA = registerTemplate(Violence.class, com.megacrit.cardcrawl.cards.colorless.Violence.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.RARE, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Red)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public Violence() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.fetch(3, PCLCardGroupHelper.DrawPile).edit(f -> f.setType(CardType.ATTACK).setOrigin(PCLCardSelection.Random)).setUpgrade(1));
    }
}