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
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.GenericCondition;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.PGR;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;

public class GenericChooseCardsToObtainEffect extends PCLEffectWithCallback<GenericChooseCardsToObtainEffect> {
    public final ArrayList<AbstractCard> cards = new ArrayList<>();
    private final CardGroup[] groups;
    private final GenericCondition<AbstractCard> filter;
    private final RandomizedList<AbstractCard> offeredCards = new RandomizedList<>();
    private final Color screenColor;
    private final int groupSize;
    private int cardsToAdd;

    public GenericChooseCardsToObtainEffect(int obtain, int groupSize) {
        this(obtain, groupSize, null, AbstractDungeon.commonCardPool, AbstractDungeon.uncommonCardPool, AbstractDungeon.rareCardPool);
    }

    public GenericChooseCardsToObtainEffect(int obtain, int groupSize, FuncT1<Boolean, AbstractCard> filter, CardGroup... groups) {
        super(0.75f, true);

        this.cardsToAdd = obtain;
        this.groupSize = groupSize;
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.screenColor.a = 0f;
        this.groups = groups;
        this.filter = filter != null ? GenericCondition.fromT1(filter) : null;
        AbstractDungeon.overlayMenu.proceedButton.hide();
    }

    public GenericChooseCardsToObtainEffect(int obtain, int groupSize, FuncT1<Boolean, AbstractCard> filter) {
        this(obtain, groupSize, filter, AbstractDungeon.commonCardPool, AbstractDungeon.uncommonCardPool, AbstractDungeon.rareCardPool);
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
    protected void firstUpdate() {
        super.firstUpdate();

        if (cardsToAdd > 0) {
            openpanelAdd();
        }
        else {
            complete();
        }
    }

    public void openpanelAdd() {
        final CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (offeredCards.size() == 0) {
            for (CardGroup cGroup : groups) {
                for (AbstractCard card : cGroup.group) {
                    if (filter == null || filter.check(card)) {
                        offeredCards.add(card);
                    }
                }
            }
            if (offeredCards.size() < cardsToAdd) {
                EUIUtils.logWarning(this, "Not enough cards");
                complete(this);
                return;
            }
        }

        for (int i = 0; i < groupSize; i++) {
            cardGroup.group.add(offeredCards.retrieve(AbstractDungeon.cardRandomRng, true).makeCopy());
        }

        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
        AbstractDungeon.gridSelectScreen.open(cardGroup, cardsToAdd, PGR.core.strings.grid_chooseCards(cardsToAdd), false, false, false, false);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (cardsToAdd > 0) {
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == cardsToAdd) {
                float displayCount = 0f;
                for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                    cards.add(card.makeCopy());
                    PCLEffects.Queue.showAndObtain(card.makeCopy(), (float) Settings.WIDTH / 3f + displayCount, (float) Settings.HEIGHT / 2f, false);
                    displayCount += (float) Settings.WIDTH / 6f;
                }
                cardsToAdd = 0;
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.gridSelectScreen.targetGroup.clear();
            }
        }
        else if (tickDuration(deltaTime)) {
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            complete(this);
        }
    }
}
