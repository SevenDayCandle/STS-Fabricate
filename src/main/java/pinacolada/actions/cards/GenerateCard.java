package pinacolada.actions.cards;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ShowCardEffect;
import pinacolada.patches.library.CardLibraryPatches;

// Copied and modified from STS-AnimatorMod
public class GenerateCard extends PCLAction<AbstractCard> {
    protected final CardGroup cardGroup;
    protected boolean makeCopy;
    protected boolean cancelIfFull;
    protected PCLCardSelection destination;
    protected AbstractCard actualCard;

    public GenerateCard(AbstractCard card, CardGroup group) {
        super(ActionType.CARD_MANIPULATION, Settings.ACTION_DUR_MED);

        this.card = CardLibraryPatches.getReplacement(card);
        if (this.card == null) {
            this.card = card;
        }
        this.cardGroup = group;

        if (!UnlockTracker.isCardSeen(this.card.cardID) || !this.card.isSeen) {
            UnlockTracker.markCardAsSeen(this.card.cardID);
            this.card.isLocked = false;
            this.card.isSeen = true;
        }

        initialize(1);
    }

    public GenerateCard cancelIfFull(boolean cancelIfFull) {
        this.cancelIfFull = cancelIfFull;

        return this;
    }

    @Override
    protected void firstUpdate() {
        switch (cardGroup.type) {
            // Master deck/unspecified should not trigger on create hooks
            case MASTER_DECK: {
                for (int i = 0; i < amount; i++) {
                    if (makeCopy) {
                        actualCard = card.makeStatEquivalentCopy();
                    }
                    else {
                        actualCard = card;
                    }
                    PCLEffects.List.add(new ShowCardAndObtainEffect(actualCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                }
                return;
            }
            case CARD_POOL:
            case UNSPECIFIED:
            default: {
                EUIUtils.logWarning(this, "Can't make temp card in " + cardGroup.type.name());
                completeImpl();
                return;
            }
            case DRAW_PILE: {
                generateCard(c -> AbstractDungeon.getCurrRoom().souls.onToDeck(c, true, true));
                break;
            }
            case HAND: {
                if (player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
                    if (cancelIfFull) {
                        completeImpl();
                        return;
                    }

                    player.createHandIsFullDialog();
                    generateCard(c -> AbstractDungeon.getCurrRoom().souls.discard(c, true));
                }
                else {
                    generateCard(null, 0.01f);
                    player.onCardDrawOrDiscard();
                    actualCard.triggerWhenCopied();
                    CombatManager.queueRefreshHandLayout();
                }

                break;
            }

            case DISCARD_PILE: {
                generateCard(c -> AbstractDungeon.getCurrRoom().souls.discard(c, true));
                break;
            }

            case EXHAUST_PILE: {
                generateCard(c -> PCLEffects.List.add(new ExhaustCardEffect(c)));
                break;
            }
        }
    }

    private void generateCard(ActionT1<AbstractCard> onComplete) {
        generateCard(onComplete, 1.3f);
    }

    private void generateCard(ActionT1<AbstractCard> onComplete, float duration) {
        if (shouldUpgradeCard() && actualCard.canUpgrade()) {
            actualCard.upgrade();
        }
        for (int i = 0; i < amount; i++) {
            if (makeCopy) {
                actualCard = card.makeStatEquivalentCopy();
            }
            else {
                actualCard = card;
            }

            if (destination != null && destination != PCLCardSelection.Manual) {
                destination.add(cardGroup.group, actualCard, 0);
            }
            // Draw pile by default goes to a random spot
            else if (cardGroup.type == CardGroup.CardGroupType.DRAW_PILE) {
                cardGroup.addToRandomSpot(actualCard);
            }
            else {
                cardGroup.addToTop(actualCard);
            }

            ShowCardEffect effect = PCLEffects.List.showCardBriefly(actualCard, duration)
                    .showPoof(true);
            if (onComplete != null) {
                effect.addCallback(() -> {
                    onComplete.invoke(actualCard);
                });
            }

            CombatManager.onCardCreated(actualCard, false);

            makeCopy = true;
        }
    }

    public GenerateCard repeat(int times) {
        // Always makeCopy because repeating action with the same card will cause visual glitches
        this.makeCopy = true;
        this.amount = times;

        if (times > 2) {
            setDuration(times > 3 ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FASTER, isRealtime);
        }

        return this;
    }

    public GenerateCard setDestination(PCLCardSelection destination) {
        this.destination = destination;

        return this;
    }

    // Hardcoded stuff
    // TODO add hook for determining card upgrade on creation
    private boolean shouldUpgradeCard() {
        return card.type != AbstractCard.CardType.CURSE && card.type != AbstractCard.CardType.STATUS && AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID);
    }
}
