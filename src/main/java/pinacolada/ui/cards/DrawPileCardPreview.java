package pinacolada.ui.cards;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.utilities.RotatingList;

// Copied and modified from STS-AnimatorMod
// TODO use this for a relic or remove
public class DrawPileCardPreview
{
    protected static final float REFRESH_DELAY = 0.625f;
    protected final FuncT1<Boolean, AbstractCard> findCard;
    protected final ActionT2<RotatingList<AbstractCard>, AbstractMonster> findCards;
    protected final RotatingList<AbstractCard> cards;
    protected final float delay;
    public boolean enabled = true;
    public boolean requireTarget;
    protected boolean render;
    protected float lastTime;
    protected float timer;
    protected AbstractCard card = null;
    protected AbstractMonster lastTarget = null;

    public DrawPileCardPreview(FuncT1<Boolean, AbstractCard> findCard)
    {
        this(findCard, null);
    }

    public DrawPileCardPreview(ActionT2<RotatingList<AbstractCard>, AbstractMonster> findCards)
    {
        this(null, findCards);
    }

    protected DrawPileCardPreview(FuncT1<Boolean, AbstractCard> findCard, ActionT2<RotatingList<AbstractCard>, AbstractMonster> findCards)
    {
        this.cards = new RotatingList<>();
        this.findCard = findCard;
        this.findCards = findCards;
        this.timer = this.delay = 0.2f;
    }

    public AbstractCard findCard(AbstractMonster target)
    {
        final int previousSize = cards.size();
        final int previousIndex = cards.getIndex();

        findCards(cards, target);

        cards.setIndex((cards.size() == previousSize) ? previousIndex : 0);

        return (cards.size() > 0) ? cards.next(true) : null;
    }

    protected void findCards(RotatingList<AbstractCard> cards, AbstractMonster target)
    {
        if (findCards != null)
        {
            findCards.invoke(cards, target);
            return;
        }

        cards.clear();
        for (AbstractCard c : AbstractDungeon.player.drawPile.group)
        {
            int index = cards.size();
            for (int i = 0; i < cards.size(); i++)
            {
                final AbstractCard temp = cards.get(i);
                if (temp.cardID.equals(c.cardID))
                {
                    index = -1;
                    break;
                }
                else if (c.name.compareTo(cards.get(i).name) < 0)
                {
                    index = i;
                    break;
                }
            }

            if (index >= 0 && (findCard == null || findCard.invoke(c)))
            {
                cards.add(index, c);
            }
        }
    }

    public AbstractCard getCurrentCard()
    {
        return card;
    }

    public void render(SpriteBatch sb)
    {
        if (render && card != null)
        {
            if ((EUI.time() - lastTime) > REFRESH_DELAY)
            {
                render = false;
            }
            else
            {
                card.render(sb);
            }
        }
    }

    public DrawPileCardPreview requireTarget(boolean requireTarget)
    {
        this.requireTarget = requireTarget;

        return this;
    }

    public DrawPileCardPreview setEnabled(boolean enabled)
    {
        this.enabled = enabled;

        return this;
    }

    public void update(AbstractCard source, AbstractMonster target)
    {
        final float currentTime = EUI.time();
        if (lastTarget != target || (currentTime - lastTime) > REFRESH_DELAY)
        {
            if (target == null && (requireTarget || AbstractDungeon.player.hoveredCard != source || !AbstractDungeon.player.isDraggingCard))
            {
                card = null;
            }
            else
            {
                final AbstractCard c = findCard(target);
                if (c != card && c != null)
                {
                    if (card != null)
                    {
                        timer = 0;
                    }
                    else
                    {
                        timer = delay;
                    }

                    card = c;
                    card.angle = 0;
                    card.unfadeOut();
                    card.lighten(true);
                    card.drawScale = 0.7f;
                    card.current_x = CardGroup.DRAW_PILE_X * 1.5f;
                    card.current_y = CardGroup.DRAW_PILE_Y * 3.5f;
                }
            }

            lastTime = currentTime;
            lastTarget = target;
        }
        else if (timer > 0)
        {
            timer -= EUI.delta();
        }

        render = timer <= 0;
    }
}
