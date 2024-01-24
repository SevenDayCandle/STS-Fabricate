package pinacolada.dungeon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.powers.PCLTriggerUsePool;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.Iterator;

public class ControllableCardPile {
    public static final float OFFSET_X = AbstractCard.IMG_WIDTH * 0.85f;
    public static final float OFFSET_Y = -AbstractCard.IMG_WIDTH * 0.1f;
    public static final float TOOLTIP_OFFSET_Y = AbstractCard.IMG_HEIGHT * 0.75f;
    public static final float SCALE = 0.65f;
    public static final float HOVER_TIME_OUT = 0.4F;
    public static EUITooltip tooltip;
    private final EUIHitbox hb = new EUIHitbox(144f * Settings.scale, 288 * Settings.scale, 96 * Settings.scale, 96f * Settings.scale);
    private final EUIButton cardButton;
    public final CardGroup subscribers = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    private AbstractCard currentCard;
    private boolean showPreview;
    private boolean isHidden = true;
    public int energy;

    public ControllableCardPile() {
        tooltip = new EUITooltip(PGR.core.strings.combat_controlPile, PGR.core.strings.combat_controlPileDescription);
        cardButton = new EUIButton(PCLCoreImages.Core.controllableCardPile.texture(), hb)
                .setBorder(PCLCoreImages.Core.controllableCardPileBorder.texture(), Color.WHITE)
                .setLabel(FontHelper.energyNumFontBlue, 1f, EUIUtils.EMPTY_STRING)
                .setOnClick(this::onButtonClick)
                .setOnRightClick(this::onButtonRightClick);
    }

    public void activate(AbstractCard card) {
        boolean isSummon = card.type == PCLEnum.CardType.SUMMON;
        PCLActions.top.selectCreature(card)
                .actForSummon(isSummon)
                .addCallback(card, (state, creature) -> {
                    activateOn(card, creature);
                });
    }

    public void activateOn(AbstractCard card, AbstractCreature creature) {
        if (card.type == PCLEnum.CardType.SUMMON && card instanceof PCLCard && creature instanceof PCLCardAlly) {
            PCLActions.bottom.summonAlly((PCLCard) card, (PCLCardAlly) creature)
                    .triggerWithdraw(false)
                    .addCallback(() -> {
                        energy -= card.energyOnUse;
                        remove(card);
                    });
        }
        else {
            PCLActions.bottom.playCard(card, creature)
                    .addCallback(() -> {
                        energy -= card.energyOnUse;
                        remove(card);
                    });
        }
    }

    public AbstractCard add(AbstractCard card) {
        int index = subscribers.group.indexOf(card);
        if (index < 0) {
            subscribers.group.add(card);
            refreshCard(card);
        }
        return card;
    }

    public boolean canUse(AbstractCard card) {
        return AbstractDungeon.getCurrMapNode() != null && card.energyOnUse <= energy;
    }

    public boolean contains(AbstractCard card) {
        return subscribers.contains(card);
    }

    protected void onButtonClick() {
        if (!AbstractDungeon.isScreenUp && currentCard != null && canUse(currentCard)) {
            activate(currentCard);
        }
    }

    protected void onButtonRightClick() {
        if (CombatManager.inBattle() && !AbstractDungeon.isScreenUp && !subscribers.isEmpty()) {
            for (AbstractCard c : subscribers.group) {
                c.drawScale = c.targetDrawScale = 0.75f;
            }
            PCLActions.top.selectFromPile("", 1, subscribers)
                    .setAnyNumber(false)
                    .addCallback(cards -> {
                        if (!cards.isEmpty()) {
                            setCurrentCard(cards.get(0));
                        }
                    });
        }
    }

    public void postRender(SpriteBatch sb) {
        if (!isHidden && currentCard != null) {
            currentCard.render(sb);
        }
    }

    protected boolean refreshCard(AbstractCard c) {
        if (c == null) {
            return false;
        }

        c.update();
        if (canUse(c)) {
            if (c.canUse(AbstractDungeon.player, null) && !AbstractDungeon.isScreenUp) {
                c.beginGlowing();
            }
            else {
                c.stopGlowing();
            }

            c.triggerOnGlowCheck();
            c.applyPowers();
        }

        return true;
    }

    public void refreshCards() {
        Iterator<AbstractCard> i = subscribers.group.iterator();
        while (i.hasNext()) {
            AbstractCard controller = i.next();
            if (!refreshCard(controller)) {
                if (currentCard == controller) {
                    currentCard = null;
                }
                i.remove();
            }
            else if (!canUse(controller) && currentCard == controller) {
                currentCard = null;
            }
        }

        if (currentCard == null && !subscribers.isEmpty()) {
            setCurrentCard(EUIUtils.find(subscribers.group, this::canUse));
        }

        if (currentCard != null && hb.hovered && !AbstractDungeon.isScreenUp) {
            currentCard.current_x = currentCard.target_x = hb.x + OFFSET_X;
            currentCard.current_y = currentCard.target_y = hb.y + OFFSET_Y;
            currentCard.drawScale = currentCard.targetDrawScale = SCALE;
            currentCard.fadingOut = false;
        }

        cardButton.setText(String.valueOf(subscribers.size()));

        if (tooltip.subHeader == null) {
            tooltip.subHeader = new ColoredString();
            tooltip.invalidateHeight();
        }
        tooltip.subHeader.color = energy == 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR;
        tooltip.subHeader.text = EUIRM.strings.numNoun(energy, PGR.core.strings.combat_uses);
        tooltip.setDescription(
                PGR.core.strings.combat_controlPileDescriptionFull(EUIHotkeys.cycle.getKeyString()));
    }

    public void remove(AbstractCard controller) {
        subscribers.group.remove(controller);
        if (currentCard == controller) {
            currentCard = null;
        }
        refreshCards();
    }

    public void render(SpriteBatch sb) {
        if (!isHidden) {
            sb.setColor(Color.WHITE);
            cardButton.renderImpl(sb);
        }
    }

    public void reset() {
        subscribers.clear();
        currentCard = null;
        energy = 0;
    }

    public void selectNextCard() {
        refreshCards();
        if (currentCard != null) {
            int startingIndex = subscribers.group.indexOf(currentCard);
            int index = startingIndex;
            index = (index + 1) % subscribers.size();
            while (index != startingIndex && !setCurrentCard(subscribers.group.get(index))) {
                index = (index + 1) % subscribers.size();
            }
        }
    }

    public boolean setCurrentCard(AbstractCard controller) {
        if (controller != null && canUse(controller)) {
            currentCard = controller;
            return true;
        }
        return false;
    }

    public void update() {
        isHidden = !CombatManager.inBattle() || subscribers.isEmpty();
        if (!AbstractDungeon.isScreenUp) {
            hb.update();
        }
        if (!isHidden) {
            refreshCards();
            cardButton.updateImpl();
            showPreview = hb.hovered && !AbstractDungeon.isScreenUp;

            if (showPreview) {
                EUI.addPostRender(this::postRender);
                if (EUIHotkeys.cycle.isJustPressed()) {
                    selectNextCard();
                }

                EUITooltip.queueTooltip(tooltip, hb.x, hb.y + TOOLTIP_OFFSET_Y);
            }

            if (PCLHotkeys.controlPileSelect.isJustPressed()) {
                onButtonClick();
            }
            else if (PCLHotkeys.controlPileChange.isJustPressed()) {
                onButtonRightClick();
            }
        }
    }
}
