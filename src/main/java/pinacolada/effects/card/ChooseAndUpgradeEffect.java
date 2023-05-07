package pinacolada.effects.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import pinacolada.effects.PCLEffects;

import java.util.Iterator;
import java.util.function.Predicate;

public class ChooseAndUpgradeEffect extends AbstractGameEffect {
    private static final UIStrings uiStrings;
    private static final float DUR = 1.5f;
    public static final String[] TEXT;

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("CampfireSmithEffect");
        TEXT = uiStrings.TEXT;
    }

    private final boolean canCancel;
    private final Color screenColor;
    private boolean openedScreen = false;
    Predicate<AbstractCard> filter;

    public ChooseAndUpgradeEffect(Predicate<AbstractCard> filter) {
        this(filter, true);
    }

    public ChooseAndUpgradeEffect(Predicate<AbstractCard> filter, boolean canCancel) {
        this.duration = 1.5f;
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.screenColor.a = 0f;
        AbstractDungeon.overlayMenu.proceedButton.hide();

        this.filter = filter;
        this.canCancel = canCancel;
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.updateBlackScreenColor();

        Iterator var1;
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && AbstractDungeon.gridSelectScreen.forUpgrade) {
            var1 = AbstractDungeon.gridSelectScreen.selectedCards.iterator();

            while (var1.hasNext()) {
                AbstractCard c = (AbstractCard) var1.next();
                PCLEffects.Queue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2f, (float) Settings.HEIGHT / 2f));
                ++CardCrawlGame.metricData.campfire_upgraded;
                CardCrawlGame.metricData.addCampfireChoiceData("SMITH", c.getMetricID());
                c.upgrade();
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                PCLEffects.Queue.showCardBriefly(c.makeStatEquivalentCopy());
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            this.isDone = true;
            //((RestRoom) AbstractDungeon.getCurrRoom()).fadeIn();
        }

        if (this.duration < 1f && !this.openedScreen) {
            this.openedScreen = true;

            CardGroup upgradeable = AbstractDungeon.player.masterDeck.getUpgradableCards();

            CardGroup filteredUpgradeable = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

            for (AbstractCard card : upgradeable.group) {
                if (filter == null || filter.test(card)) {
                    filteredUpgradeable.addToBottom(card);
                }
            }

            if (filteredUpgradeable.isEmpty()) {
                isDone = true;
                return;
            }

            AbstractDungeon.gridSelectScreen.open(filteredUpgradeable, 1, TEXT[0], true, false, canCancel
                    , false);
            var1 = AbstractDungeon.player.relics.iterator();

            while (var1.hasNext()) {
                AbstractRelic r = (AbstractRelic) var1.next();
                r.onSmith();
            }
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        if (AbstractDungeon.screen == CurrentScreen.GRID) {
            AbstractDungeon.gridSelectScreen.render(sb);
        }
    }

    public void dispose() {
    }

    private void updateBlackScreenColor() {
        if (this.duration > 1f) {
            this.screenColor.a = Interpolation.fade.apply(1f, 0f, (this.duration - 1f) * 2f);
        }
        else {
            this.screenColor.a = Interpolation.fade.apply(0f, 1f, this.duration / 1.5f);
        }

    }
}
