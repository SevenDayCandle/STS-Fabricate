package pinacolada.dungeon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardAffinities;
import pinacolada.cards.base.fields.PCLCardAffinity;
import pinacolada.interfaces.subscribers.OnAfterlifeSubscriber;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class AfterlifePile {
    protected static AbstractCard currentCard;

    public static void add(AbstractCard card) {
        if (GameUtilities.inBattle() && !isAdded(card)) {
            CombatManager.controlPile.add(card)
                    .setUseCondition(control -> canUse(control.card))
                    .onSelect(control ->
                    {
                        if (canUse(control.card)) {
                            currentCard = control.card;
                            PCLCardAffinities pAffinities = GameUtilities.getPCLCardAffinities(control.card);
                            PCLActions.bottom.selectCreature(control.card).addCallback(control, (state, creature) ->
                            {
                                PCLActions.bottom.purgeFromPile(control.card.name, 9999, player.exhaustPile, player.hand)
                                        .setFilter(c -> canPurge(control.card, c, pAffinities))
                                        .setCompletionRequirement(AfterlifePile::conditionMet)
                                        .setDynamicMessage(AfterlifePile::getDynamicLabel)
                                        .addCallback((c) -> {
                                            currentCard = null;
                                            if (c.size() > 0) {
                                                PCLActions.bottom.playCard(state.card, EUIUtils.safeCast(creature, AbstractMonster.class))
                                                        .spendEnergy(false)
                                                        .addCallback(() -> {
                                                            CombatManager.subscriberDo(OnAfterlifeSubscriber.class, t -> t.onAfterlife(state.card, c));
                                                        });
                                            }
                                        });

                            });
                        }
                    });
        }

    }

    protected static boolean booleanArrayMet(boolean[] values) {
        for (boolean b : values) {
            if (b) {
                return false;
            }
        }
        return true;
    }

    private static boolean canPurge(AbstractCard controlCard, AbstractCard card, PCLCardAffinities pAffinities) {
        return !controlCard.cardID.equals(card.cardID)
                && (pAffinities == null || (pAffinities.hasStar() && GameUtilities.getPCLCardAffinityLevel(card, PCLAffinity.General, true) > 0)
                || EUIUtils.any(pAffinities.getCardAffinities(true), af -> GameUtilities.getPCLCardAffinityLevel(card, af.type, true) > 0));
    }


    private static boolean canUse(AbstractCard card) {
        if (!card.canUse(player, null)) {
            return false;
        }
        return conditionMet(card, player.exhaustPile.group, player.hand.group);
    }

    @SafeVarargs
    protected static boolean conditionMet(AbstractCard card, ArrayList<AbstractCard>... cardLists) {
        if (card != null) {
            PCLCardAffinities pAffinities = GameUtilities.getPCLCardAffinities(card);
            if (pAffinities != null) {
                return booleanArrayMet(getRequiredAffinities(card, cardLists));
            }
        }
        return false;
    }

    protected static boolean conditionMet(ArrayList<AbstractCard> cards) {
        return conditionMet(currentCard, cards);
    }

    protected static String getDynamicLabel(ArrayList<AbstractCard> cards) {
        final boolean[] required = getRequiredAffinities(currentCard, cards);

        return booleanArrayMet(required) ? PGR.core.strings.combat_afterlifeMet : EUIUtils.format(PGR.core.strings.combat_afterlifeRequirement,
                EUIUtils.joinStringsMap(", ", af -> af.getTooltip().getTitleOrIcon(), EUIUtils.filter(PCLAffinity.basic(), af -> required[af.id])));
    }

    @SafeVarargs
    protected static boolean[] getRequiredAffinities(AbstractCard card, ArrayList<AbstractCard>... cardLists) {
        final boolean[] requiredAffinities = new boolean[7];
        PCLCardAffinities pAffinities = GameUtilities.getPCLCardAffinities(card);
        if (pAffinities == null) {
            return requiredAffinities;
        }
        if (pAffinities.hasStar()) {
            for (PCLAffinity af : PCLAffinity.basic()) {
                requiredAffinities[af.id] = true;
            }
        }
        else {
            for (PCLCardAffinity cf : pAffinities.getCardAffinities(true)) {
                requiredAffinities[cf.type.id] = true;
            }
        }

        for (ArrayList<AbstractCard> list : cardLists) {
            for (AbstractCard c2 : list) {
                PCLCardAffinities pAffinities2 = GameUtilities.getPCLCardAffinities(c2);
                if (!c2.cardID.equals(card.cardID) && pAffinities2 != null) {
                    if (pAffinities2.hasStar()) {
                        Arrays.fill(requiredAffinities, false);
                    }
                    else {
                        for (PCLCardAffinity cf : GameUtilities.getPCLCardAffinities(c2).getCardAffinities(false)) {
                            requiredAffinities[cf.type.id] = requiredAffinities[cf.type.id] & cf.level == 0;
                        }
                    }
                }
            }
        }

        return requiredAffinities;
    }

    public static boolean isAdded(AbstractCard card) {
        return CombatManager.controlPile.find(card) != null;
    }
}

