package pinacolada.effects.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.GenericCondition;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class GenericChooseCardsToRemoveEffect extends PCLEffectWithCallback<GenericChooseCardsToRemoveEffect>
{
    private static final int GROUP_SIZE = 3;
    public final ArrayList<AbstractCard> cards = new ArrayList<>();
    private final GenericCondition<AbstractCard> filter;
    private final Color screenColor;
    private int cardsToRemove;

    public GenericChooseCardsToRemoveEffect(int remove)
    {
        this(remove, null);
    }

    public GenericChooseCardsToRemoveEffect(int remove, FuncT1<Boolean, AbstractCard> filter)
    {
        super(0.75f, true);

        this.cardsToRemove = remove;
        this.filter = filter != null ? GenericCondition.fromT1(filter) : null;
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.screenColor.a = 0f;
        AbstractDungeon.overlayMenu.proceedButton.hide();
    }

    public void openpanelRemove()
    {
        CardGroup cardGroup = new CardGroup(player.masterDeck.type);
        for (AbstractCard card : player.masterDeck.getPurgeableCards().group)
        {
            if (filter == null || filter.check(card))
            {
                cardGroup.addToBottom(card);
            }
        }

        if (cardGroup.size() < cardsToRemove)
        {
            complete();
            return;
        }

        if (AbstractDungeon.isScreenUp)
        {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }

        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
        AbstractDungeon.gridSelectScreen.open(cardGroup, cardsToRemove, PGR.core.strings.gridSelection.chooseCards(cardsToRemove), false, false, false, true);
    }

    @Override
    public void render(SpriteBatch sb)
    {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        if (AbstractDungeon.screen == CurrentScreen.GRID)
        {
            AbstractDungeon.gridSelectScreen.render(sb);
        }
    }

    @Override
    protected void firstUpdate()
    {
        super.firstUpdate();

        if (cardsToRemove > 0)
        {
            openpanelRemove();
        }
        else
        {
            complete();
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (cardsToRemove > 0)
        {
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == cardsToRemove)
            {
                for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards)
                {
                    cards.add(card.makeCopy());
                    AbstractDungeon.player.masterDeck.removeCard(card);
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.gridSelectScreen.targetGroup.clear();
                cardsToRemove = 0;
            }
        }
        else if (tickDuration(deltaTime))
        {
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            complete(this);
        }
    }
}
