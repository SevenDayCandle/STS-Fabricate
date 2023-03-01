package pinacolada.actions.cards;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.utilities.EUIClassUtils;
import pinacolada.actions.PCLAction;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Copied and modified from STS-AnimatorMod
public class ReplaceCard extends PCLAction<Map<AbstractCard, AbstractCard>>
{
    protected final Map<AbstractCard, AbstractCard> newCards = new HashMap<>();
    protected boolean upgrade;
    protected boolean preserveStats;
    protected UUID cardUUID;

    public ReplaceCard(UUID cardUUID, AbstractCard replacement)
    {
        super(ActionType.CARD_MANIPULATION);

        this.cardUUID = cardUUID;
        this.card = replacement;

        initialize(1);
    }

    @Override
    protected void firstUpdate()
    {
        replace(player.limbo.group);
        replace(player.exhaustPile.group);
        replace(player.discardPile.group);
        replace(player.drawPile.group);
        replace(player.hand.group);

        for (PCLCardAlly summon : GameUtilities.getSummons(false))
        {
            PCLCard summonCard = summon.card;
            if (summonCard != null && cardUUID.equals(summonCard.uuid))
            {
                AbstractCard copy = replace(summonCard);
                if (copy instanceof PCLCard)
                {
                    summon.initializeForCard((PCLCard) copy, false, false);
                }
            }
        }

        for (int i = 0; i < AbstractDungeon.actionManager.actions.size(); i++)
        {
            UseCardAction action = EUIUtils.safeCast(AbstractDungeon.actionManager.actions.get(i), UseCardAction.class);
            if (action != null)
            {
                AbstractCard card = ReflectionHacks.getPrivate(action, UseCardAction.class, "targetCard");
                if (newCards.containsKey(card) || cardUUID.equals(card.uuid))
                {
                    EUIClassUtils.setField(action, "targetCard", replace(card));
                }
            }
        }

        if (player.cardInUse != null && (newCards.containsKey(player.cardInUse) || cardUUID.equals(player.cardInUse.uuid)))
        {
            player.cardInUse = replace(player.cardInUse);
        }

        complete(newCards);
    }

    public ReplaceCard preserveStats(boolean preserveStats)
    {
        this.preserveStats = preserveStats;

        return this;
    }

    protected void replace(ArrayList<AbstractCard> cards)
    {
        for (int i = 0; i < cards.size(); i++)
        {
            AbstractCard original = cards.get(i);
            if (cardUUID.equals(original.uuid))
            {
                cards.set(i, replace(original));
            }
        }
    }

    protected AbstractCard replace(AbstractCard original)
    {
        AbstractCard replacement;
        if (newCards.containsKey(original))
        {
            replacement = newCards.get(original);
        }
        else
        {
            replacement = card.makeStatEquivalentCopy();
            replacement.uuid = original.uuid;

            if (upgrade)
            {
                replacement.upgrade();
            }

            GameUtilities.copyVisualProperties(replacement, original);
            newCards.put(original, replacement);
        }

        return replacement;
    }

    public ReplaceCard setUpgrade(boolean upgrade)
    {
        this.upgrade = upgrade;

        return this;
    }
}
