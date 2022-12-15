package pinacolada.ui.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActions;
import pinacolada.interfaces.subscribers.OnCardMovedSubscriber;
import pinacolada.interfaces.subscribers.OnCardPurgedSubscriber;
import pinacolada.interfaces.subscribers.OnPhaseChangedSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Iterator;

public class ControllableCardPile implements OnPhaseChangedSubscriber, OnCardPurgedSubscriber, OnCardMovedSubscriber
{
    public static final float OFFSET_X = AbstractCard.IMG_WIDTH * 0.85f;
    public static final float OFFSET_Y = -AbstractCard.IMG_WIDTH * 0.1f;
    public static final float TOOLTIP_OFFSET_Y = AbstractCard.IMG_HEIGHT * 0.75f;
    public static final float SCALE = 0.65f;
    public static final float HOVER_TIME_OUT = 0.4F;
    public static EUITooltip tooltip;
    public final ArrayList<ControllableCard> subscribers = new ArrayList<>();
    protected final EUIButton cardButton;
    private final EUIHitbox hb = new EUIHitbox(144f * Settings.scale, 288 * Settings.scale, 96 * Settings.scale, 96f * Settings.scale);
    public boolean isHidden = true;
    protected ControllableCard currentCard;
    protected boolean showPreview;

    public ControllableCardPile()
    {
        tooltip = new EUITooltip(PGR.core.strings.combat.controlPile, PGR.core.strings.combat.controlPileDescription);
        cardButton = new EUIButton(PGR.core.images.core.controllableCardPile.texture(), hb)
                .setBorder(PGR.core.images.core.controllableCardPileBorder.texture(), Color.WHITE)
                .setFont(FontHelper.energyNumFontBlue, 1f)
                .setOnClick(() -> {
                    if (!AbstractDungeon.isScreenUp && currentCard != null && currentCard.canUse())
                    {
                        currentCard.select();
                    }
                })
                .setOnRightClick(() -> {
                    if (GameUtilities.inBattle() && !AbstractDungeon.isScreenUp)
                    {
                        CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (ControllableCard c : subscribers)
                        {
                            if (c.canUse())
                            {
                                cardGroup.addToBottom(c.card);
                                c.card.drawScale = c.card.targetDrawScale = 0.75f;
                            }
                        }
                        if (cardGroup.size() > 0)
                        {
                            PCLActions.top.selectFromPile("", 1, cardGroup)
                                    .setOptions(false, false)
                                    .addCallback(cards -> {
                                        if (cards.size() > 0)
                                        {
                                            ControllableCard co = EUIUtils.find(subscribers, c -> c.card == cards.get(0));
                                            if (co != null)
                                            {
                                                setCurrentCard(co);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public ControllableCard add(AbstractCard card)
    {
        ControllableCard controller = find(card);
        if (controller == null)
        {
            controller = new ControllableCard(this, card);
            subscribers.add(controller);
            refreshCard(controller);
        }
        return controller;
    }

    public boolean canUse(AbstractCard card)
    {
        ControllableCard chosen = EUIUtils.find(subscribers, c -> c.card == card);
        return chosen != null && chosen.canUse();
    }

    public void clear()
    {
        CombatManager.onPhaseChanged.subscribe(this);
        CombatManager.onCardPurged.subscribe(this);
        CombatManager.onCardMoved.subscribe(this);
        subscribers.clear();
        currentCard = null;
    }

    public boolean contains(AbstractCard card)
    {
        return card != null && find(card) != null;
    }

    public ControllableCard find(AbstractCard card)
    {
        return EUIUtils.find(subscribers, c -> c.card == card);
    }

    public int getUsableCount()
    {
        return EUIUtils.count(subscribers, ControllableCard::canUse);
    }

    @Override
    public void onCardMoved(AbstractCard card, CardGroup source, CardGroup destination)
    {
        PCLActions.last.callback(this::refreshCards);
    }

    @Override
    public void onPhaseChanged(GameActionManager.Phase phase)
    {
        refreshCards();
    }

    @Override
    public void onPurge(AbstractCard card)
    {
        PCLActions.last.callback(this::refreshCards);
    }

    public void postRender(SpriteBatch sb)
    {
        if (!isHidden && currentCard != null)
        {
            currentCard.render(sb);
        }
    }

    protected boolean refreshCard(ControllableCard c)
    {
        final AbstractCard card = c.card;
        if (card == null)
        {
            return false;
        }

        c.update();
        if (c.canUse())
        {
            if (card.canUse(AbstractDungeon.player, null) && !AbstractDungeon.isScreenUp)
            {
                card.beginGlowing();
            }
            else
            {
                card.stopGlowing();
            }

            card.triggerOnGlowCheck();
            card.applyPowers();
        }

        return true;
    }

    public void refreshCards()
    {
        Iterator<ControllableCard> i = subscribers.iterator();
        while (i.hasNext())
        {
            ControllableCard controller = i.next();
            if (!refreshCard(controller))
            {
                if (currentCard == controller)
                {
                    currentCard = null;
                }
                i.remove();
            }
            else if (!controller.canUse() && currentCard == controller)
            {
                currentCard = null;
            }
        }

        if (currentCard == null && subscribers.size() > 0)
        {
            setCurrentCard(EUIUtils.find(subscribers, ControllableCard::canUse));
        }

        if (currentCard != null && hb.hovered && !AbstractDungeon.isScreenUp)
        {
            currentCard.card.current_x = currentCard.card.target_x = hb.x + OFFSET_X;
            currentCard.card.current_y = currentCard.card.target_y = hb.y + OFFSET_Y;
            currentCard.card.drawScale = currentCard.card.targetDrawScale = SCALE;
            currentCard.card.fadingOut = false;
        }
    }

    public void remove(ControllableCard controller)
    {
        subscribers.remove(controller);
        refreshCards();
    }

    public void render(SpriteBatch sb)
    {
        if (!isHidden)
        {
            sb.setColor(Color.WHITE);
            cardButton.renderImpl(sb);
        }
    }

    public void selectNextCard()
    {
        refreshCards();
        if (currentCard != null)
        {
            int startingIndex = subscribers.indexOf(currentCard);
            int index = startingIndex;
            index = (index + 1) % subscribers.size();
            while (index != startingIndex && !setCurrentCard(subscribers.get(index)))
            {
                index = (index + 1) % subscribers.size();
            }
        }
    }

    public boolean setCurrentCard(ControllableCard controller)
    {
        if (controller != null && controller.canUse())
        {
            currentCard = controller;
            return true;
        }
        return false;
    }

    public void update()
    {
        isHidden = !GameUtilities.inBattle() || subscribers.size() == 0;
        if (!AbstractDungeon.isScreenUp)
        {
            hb.update();
        }
        if (!isHidden)
        {
            refreshCards();
            cardButton.setText(String.valueOf(getUsableCount()));
            cardButton.updateImpl();
            showPreview = hb.hovered && !AbstractDungeon.isScreenUp;

            if (showPreview)
            {
                EUI.addPostRender(this::postRender);
                if (EUIHotkeys.cycle.isJustPressed())
                {
                    selectNextCard();
                }

                if (tooltip != null)
                {
                    tooltip.setDescription(PGR.core.strings.combat.controlPileDescriptionFull(EUIHotkeys.cycle.getKeyString()));
                    EUITooltip.queueTooltip(tooltip, hb.x, hb.y + TOOLTIP_OFFSET_Y);
                }
            }

            if (PCLHotkeys.controlPileSelect.isJustPressed())
            {
                cardButton.onLeftClick.complete(cardButton);
            }
            else if (PCLHotkeys.controlPileChange.isJustPressed())
            {
                cardButton.onRightClick.complete(cardButton);
            }
        }
    }
}
