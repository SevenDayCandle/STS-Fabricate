package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PGR;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class FakeLoadout extends PCLLoadout {
    public FakeLoadout() {
        super(AbstractCard.CardColor.COLORLESS, PGR.BASE_PREFIX, 0);
    }

    @Override
    public PCLCardData getSymbolicCard() {
        return QuestionMark.DATA;
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        return startingDeck;
    }

    @Override
    public void onVictory(int ascensionLevel, int trophyLevel, int score) {
        //
    }

    @Override
    public PCLTrophies getTrophies() {
        return null;
    }
}
