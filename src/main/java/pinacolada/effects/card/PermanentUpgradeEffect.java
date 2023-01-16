package pinacolada.effects.card;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.PCLEffects;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public class PermanentUpgradeEffect extends PCLEffectWithCallback<AbstractCard>
{
    private GenericCondition<AbstractCard> filter;
    private ListSelection<AbstractCard> selection;
    private AbstractCard card;

    public PermanentUpgradeEffect()
    {
        super(0.2f, true);
    }

    public PermanentUpgradeEffect setFilter(FuncT1<Boolean, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT1(filter);

        return this;
    }

    public <S> PermanentUpgradeEffect setFilter(S state, FuncT2<Boolean, S, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT2(filter, state);

        return this;
    }

    public PermanentUpgradeEffect setSelection(ListSelection<AbstractCard> selection)
    {
        this.selection = selection;

        return this;
    }

    @Override
    public void render(SpriteBatch sb)
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    protected void firstUpdate()
    {
        final ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
        for (AbstractCard c : player.masterDeck.group)
        {
            if (c.canUpgrade() && filter.check(c))
            {
                upgradableCards.add(c);
            }
        }

        if (upgradableCards.size() > 0)
        {
            if (selection == null)
            {
                selection = ListSelection.random(AbstractDungeon.miscRng);
            }

            card = selection.get(upgradableCards, 1, false);
            card.upgrade();
            player.bottledCardUpgradeCheck(card);

            final float x = Settings.WIDTH * (0.4f + (0.1f * EUIUtils.count(AbstractDungeon.topLevelEffects, e -> e instanceof PermanentUpgradeEffect)));
            final float y = Settings.HEIGHT * 0.5f;

            PCLEffects.TopLevelQueue.showCardBriefly(card.makeStatEquivalentCopy(), x + AbstractCard.IMG_WIDTH * 0.5f + 20f * Settings.scale, y);
            PCLEffects.TopLevelQueue.add(new UpgradeShineEffect(x, y));
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(card);
        }
    }
}
