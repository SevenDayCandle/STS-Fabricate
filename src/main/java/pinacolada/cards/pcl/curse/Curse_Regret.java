package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.cards.curses.Regret;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.modifiers.PMod_PerCard;
import pinacolada.skills.skills.base.moves.PMove_GainTempHP;
import pinacolada.skills.skills.base.moves.PMove_LoseHP;

@VisibleCard
public class Curse_Regret extends PCLCard {
    public static final String ATLAS_URL = "curse/regret";
    public static final PCLCardData DATA = registerTemplate(Curse_Regret.class, Regret.ID)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Regret() {
        super(DATA);
    }

    @Override
    public void setup(Object input) {
        addUseMove(PCond.onTurnEnd(), new PMod_PerCard(1, PCLCardGroupHelper.Hand), new PMove_LoseHP(1));
        addUseMove(PCond.onExhaust(), new PMove_GainTempHP(2));
    }
}