package pinacolada.cards.pcl.colorless;

import extendedui.utilities.panels.card.CostFilter;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PMove;

@VisibleCard(add = false)
public class Enlightenment extends PCLCard {
    public static final String ATLAS_URL = "colorless/skill/enlightenment";
    public static final PCLCardData DATA = registerTemplate(Enlightenment.class, com.megacrit.cardcrawl.cards.colorless.Enlightenment.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Blue, PCLAffinity.Yellow)
            .setMultiformData(2)
            .setBranchFactor(1)
            .setColorless();

    public Enlightenment() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.modifyCostExactForTurn(1, 0, PCLCardGroupHelper.Hand).edit(f -> f.setCost(CostFilter.Cost2, CostFilter.Cost3, CostFilter.Cost4, CostFilter.Cost5)).setCustomUpgrade((skill, form, upgrade) -> {
            skill.edit(f -> f.setForced(form == 1));
        }));
    }
}