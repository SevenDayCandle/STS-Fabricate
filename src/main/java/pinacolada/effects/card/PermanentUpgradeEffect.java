package pinacolada.effects.card;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.PCLEffects;

import java.util.ArrayList;

public class PermanentUpgradeEffect extends PCLEffectWithCallback<AbstractCard> {
    private FuncT1<Boolean, AbstractCard> filter;
    private PCLCardSelection selection;
    private AbstractCard card;

    public PermanentUpgradeEffect() {
        super(0.2f, true);
    }

    @Override
    public void dispose() {
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        final ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade() && filter.invoke(c)) {
                upgradableCards.add(c);
            }
        }

        if (upgradableCards.size() > 0) {
            if (selection == null || selection == PCLCardSelection.Manual) {
                selection = PCLCardSelection.Random;
            }

            card = selection.get(upgradableCards, 0);
            if (card == null) {
                complete();
                return;
            }
            if (card instanceof PCLCard && ((PCLCard) card).isBranchingUpgrade()) {
                PCLEffects.Queue.add(new ChooseCardsForMultiformUpgradeEffect((PCLCard) card));
            }
            else {
                card.upgrade();
                final float x = Settings.WIDTH * (0.4f + (0.1f * EUIUtils.count(AbstractDungeon.topLevelEffects, e -> e instanceof PermanentUpgradeEffect)));
                final float y = Settings.HEIGHT * 0.5f;

                PCLEffects.TopLevelQueue.showCardBriefly(card.makeStatEquivalentCopy(), x + AbstractCard.IMG_WIDTH * 0.5f + 20f * Settings.scale, y);
                PCLEffects.TopLevelQueue.add(new UpgradeShineEffect(x, y));
            }
            AbstractDungeon.player.bottledCardUpgradeCheck(card);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
    }

    public PermanentUpgradeEffect setFilter(FuncT1<Boolean, AbstractCard> filter) {
        this.filter = filter;

        return this;
    }

    public PermanentUpgradeEffect setSelection(PCLCardSelection selection) {
        this.selection = selection;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (tickDuration(deltaTime)) {
            complete(card);
        }
    }
}
