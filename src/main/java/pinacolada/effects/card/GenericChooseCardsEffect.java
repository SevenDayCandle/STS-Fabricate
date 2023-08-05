package pinacolada.effects.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public abstract class GenericChooseCardsEffect extends PCLEffectWithCallback<GenericChooseCardsEffect> {
    protected final Color screenColor;
    protected final FuncT1<Boolean, AbstractCard> filter;
    public final ArrayList<AbstractCard> cards = new ArrayList<>();
    protected int cardsToChoose;
    protected boolean selected;
    protected boolean canCancel;

    public GenericChooseCardsEffect(int choose) {
        this(choose, null);
    }

    public GenericChooseCardsEffect(int choose, FuncT1<Boolean, AbstractCard> filter) {
        super(0.75f, true);

        this.cardsToChoose = choose;
        this.filter = filter;
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.screenColor.a = 0f;
        AbstractDungeon.overlayMenu.proceedButton.hide();
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        super.firstUpdate(deltaTime);

        if (cardsToChoose > 0) {
            openPanel();
        }
        else {
            complete(this);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        if (AbstractDungeon.screen == CurrentScreen.GRID) {
            AbstractDungeon.gridSelectScreen.render(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (cardsToChoose > 0) {
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == cardsToChoose) {
                for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                    cards.add(card.makeCopy());
                    onCardSelected(card);
                }
                selected = true;
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.gridSelectScreen.targetGroup.clear();
                cardsToChoose = 0;
            }
        }
        if (selected) {
            complete(this);
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) // cancelled
        {
            complete();
        }
    }

    protected boolean forPurge() {
        return false;
    }

    protected boolean forTransform() {
        return false;
    }

    protected boolean forUpgrade() {
        return false;
    }

    protected String getSelectString() {
        return PGR.core.strings.grid_chooseCards(cardsToChoose);
    }

    protected void openGridScreen(CardGroup cardGroup) {
        AbstractDungeon.gridSelectScreen.open(cardGroup, cardsToChoose, getSelectString(), forUpgrade(), forTransform(), canCancel, forPurge());
    }

    public void openPanel() {
        CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : getGroup()) {
            if (filter == null || filter.invoke(card)) {
                cardGroup.addToTop(card);
            }
        }

        if (cardGroup.size() < cardsToChoose) {
            complete(this);
            return;
        }

        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }

        // Setting canCancel to true does not ensure the cancel button will be shown
        if (canCancel) {
            AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
        }

        openGridScreen(cardGroup);
    }

    protected abstract ArrayList<AbstractCard> getGroup();

    public abstract void onCardSelected(AbstractCard c);


}
