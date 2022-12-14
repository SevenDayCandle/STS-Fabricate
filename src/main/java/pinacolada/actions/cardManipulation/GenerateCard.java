package pinacolada.actions.cardManipulation;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLEffects;
import pinacolada.utilities.ListSelection;

public class GenerateCard extends PCLActionWithCallback<AbstractCard>
{
    protected final CardGroup cardGroup;
    protected boolean upgrade;
    protected boolean makeCopy;
    protected boolean cancelIfFull;
    protected ListSelection<AbstractCard> destination;
    protected AbstractCard actualCard;
    private transient AbstractGameEffect effect = null;

    public GenerateCard(AbstractCard card, CardGroup group)
    {
        super(ActionType.CARD_MANIPULATION, Settings.ACTION_DUR_MED);

        this.card = card;
        this.cardGroup = group;

        if (!UnlockTracker.isCardSeen(card.cardID) || !card.isSeen)
        {
            UnlockTracker.markCardAsSeen(card.cardID);
            card.isLocked = false;
            card.isSeen = true;
        }

        initialize(1);
    }

    public GenerateCard cancelIfFull(boolean cancelIfFull)
    {
        this.cancelIfFull = cancelIfFull;

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (makeCopy)
        {
            actualCard = card.makeStatEquivalentCopy();
        }
        else
        {
            actualCard = card;
        }

        if (upgrade && actualCard.canUpgrade())
        {
            actualCard.upgrade();
        }

        switch (cardGroup.type)
        {
            case DRAW_PILE:
            {
                effect = PCLEffects.List.add(new ShowCardAndAddToDrawPileEffect(actualCard,
                        (float) Settings.WIDTH / 2f - ((25f * Settings.scale) + AbstractCard.IMG_WIDTH),
                        (float) Settings.HEIGHT / 2f, true, true, false));

                // For reasons unknown ShowCardAndAddToDrawPileEffect creates a copy of the card...
                actualCard = ReflectionHacks.getPrivate(effect,ShowCardAndAddToDrawPileEffect.class,"card");

                break;
            }

            case HAND:
            {
                if (player.hand.size() >= BaseMod.MAX_HAND_SIZE)
                {
                    if (cancelIfFull)
                    {
                        complete();
                        return;
                    }

                    player.createHandIsFullDialog();
                    effect = PCLEffects.List.add(new ShowCardAndAddToDiscardEffect(actualCard));
                }
                else
                {
                    // If you don't specify x and y it won't play the card obtain sfx
                    effect = PCLEffects.List.add(new ShowCardAndAddToHandEffect(actualCard,
                            (float) Settings.WIDTH / 2f - ((25f * Settings.scale) + AbstractCard.IMG_WIDTH),
                            (float) Settings.HEIGHT / 2f));
                }

                break;
            }

            case DISCARD_PILE:
            {
                effect = PCLEffects.List.add(new ShowCardAndAddToDiscardEffect(actualCard));

                break;
            }

            case EXHAUST_PILE:
            {
                effect = PCLEffects.List.add(new ExhaustCardEffect(actualCard));
                player.exhaustPile.addToTop(actualCard);
                break;
            }

            case MASTER_DECK:
            {
                PCLActions.top.add(new AddCardToDeckAction(actualCard));

                if (destination != null)
                {
                    EUIUtils.logWarning(this, "Destination for the master deck will be ignored.");
                    destination = null;
                }

                break;
            }

            case CARD_POOL:
            case UNSPECIFIED:
            default:
            {
                EUIUtils.logWarning(this, "Can't make temp card in " + cardGroup.type.name());
                complete();
                break;
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (effect != null && !effect.isDone)
        {
            effect.update();
        }

        if (tickDuration(deltaTime))
        {
            if (amount > 1)
            {
                GenerateCard copy = new GenerateCard(actualCard, cardGroup);
                copy.Import(this);
                copy.destination = destination;
                copy.makeCopy = makeCopy;
                copy.upgrade = upgrade;
                copy.cancelIfFull = cancelIfFull;
                copy.amount = amount - 1;
                PCLActions.top.add(copy);
            }

            complete(actualCard);

            if (destination != null && cardGroup.group.remove(actualCard))
            {
                destination.add(cardGroup.group, actualCard, 0);
            }
        }
    }

    public GenerateCard repeat(int times)
    {
        // Always makeCopy because repeating action with the same card will cause visual glitches
        this.makeCopy = true;
        this.amount = times;

        if (times > 2)
        {
            setDuration(times > 3 ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FASTER, isRealtime);
        }

        return this;
    }

    public GenerateCard setDestination(ListSelection<AbstractCard> destination)
    {
        this.destination = destination;

        return this;
    }

    public GenerateCard setMakeCopy(boolean makeCopy)
    {
        this.makeCopy = makeCopy;

        return this;
    }

    public GenerateCard setUpgrade(boolean upgrade)
    {
        this.upgrade = upgrade;

        return this;
    }

    public GenerateCard setUpgrade(boolean upgrade, boolean makeCopy)
    {
        this.makeCopy = makeCopy;
        this.upgrade = upgrade;

        return this;
    }
}
