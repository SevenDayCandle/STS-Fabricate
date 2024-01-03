package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsForMultiformUpgradeEffect;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class UpgradeFromPile extends SelectFromPile {
    protected boolean permanent;

    public UpgradeFromPile(String sourceName, int amount, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, groups);
    }

    public UpgradeFromPile(String sourceName, int amount, PCLCardSelection origin, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, origin, groups);
    }

    public UpgradeFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, groups);
    }

    public UpgradeFromPile(String sourceName, AbstractCreature target, int amount, PCLCardSelection origin, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, origin, groups);
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return card.canUpgrade() && super.canSelect(card);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result) {
        for (AbstractCard card : result) {
            upgradeCard(card, (Settings.WIDTH / 4f) + ((result.size() - 1) * (AbstractCard.IMG_WIDTH * 0.75f)));
        }

        CombatManager.queueRefreshHandLayout();

        super.complete(result);
    }

    @Override
    public String getActionMessage() {
        return PGR.core.tooltips.upgrade.title;
    }

    public UpgradeFromPile isPermanent(boolean isPermanent) {
        this.permanent = isPermanent;

        return this;
    }

    protected void upgradeCard(AbstractCard card, float x) {
        if (card.canUpgrade()) {
            PCLCard pCard = EUIUtils.safeCast(card, PCLCard.class);
            if (pCard != null && pCard.isBranchingUpgrade()) {
                PCLEffects.Queue.add(new ChooseCardsForMultiformUpgradeEffect(pCard).addCallback(
                        result -> {
                            if (!result.cards.isEmpty()) {
                                upgradeOtherCopies(card, c -> {
                                    if (c instanceof PCLCard) {
                                        ((PCLCard) c).changeForm(pCard.getForm(), pCard.timesUpgraded);
                                    }
                                });
                            }
                        }
                ));
            }
            else {
                upgradeOtherCopies(card, AbstractCard::upgrade);
                card.upgrade();
                CombatManager.onCardUpgrade(card);
                PCLEffects.TopLevelQueue.add(new UpgradeShineEffect(x, Settings.HEIGHT / 2f));
                PCLEffects.TopLevelQueue.showCardBriefly(card.makeStatEquivalentCopy(), x, Settings.HEIGHT / 2f);
            }
        }
    }

    protected void upgradeOtherCopies(AbstractCard card, ActionT1<AbstractCard> upgradeFunc) {
        for (AbstractCard c : GameUtilities.getAllInBattleInstances(card.uuid)) {
            if (c != card && c.canUpgrade()) {
                upgradeFunc.invoke(c);
                CombatManager.onCardUpgrade(c);
            }
        }

        if (permanent) {
            final AbstractCard c = GameUtilities.getMasterDeckInstance(card.uuid);
            if (c != null) {
                if (c != card && c.canUpgrade()) {
                    upgradeFunc.invoke(c);
                }

                player.bottledCardUpgradeCheck(c);
            }
        }
    }
}