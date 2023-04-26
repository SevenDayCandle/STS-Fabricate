package pinacolada.actions.special;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.CombatManager;

public class HasteAction extends PCLAction<Void> {
    public HasteAction(AbstractCard card) {
        super(ActionType.SPECIAL);

        this.isRealtime = true;
        this.card = card;

        initialize(1);
    }

    @Override
    protected void firstUpdate() {
        if (PCLCardTag.Haste.has(card) && !CombatManager.hasteInfinitesThisTurn().contains(card)) {
            PCLActions.top.draw(1);
            PCLActions.top.flash(card);
        }
        else {
            completeImpl();
        }
    }

    @Override
    protected void completeImpl() {
        super.completeImpl();
        if (PCLCardTag.Haste.has(card) && !CombatManager.hasteInfinitesThisTurn().contains(card)) {
            CardCrawlGame.sound.playA("POWER_FLIGHT", MathUtils.random(0.3f, 0.4f));
            PCLCardTag.Haste.tryProgress(card);
            CombatManager.hasteInfinitesThisTurn().add(card);
        }
    }
}
