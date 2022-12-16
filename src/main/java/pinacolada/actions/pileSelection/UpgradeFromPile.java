package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.megacritCopy.UpgradeShineEffect2;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class UpgradeFromPile extends SelectFromPile
{
    protected boolean permanent;

    public UpgradeFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.EXHAUST, sourceName, amount, groups);
        this.canCancel = false;
    }

    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return card.canUpgrade() && super.canSelect(card);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        for (AbstractCard card : result)
        {

            if (card.canUpgrade())
            {
                card.upgrade();
            }

            for (AbstractCard c : GameUtilities.getAllInBattleInstances(card.uuid))
            {
                if (c != card && c.canUpgrade())
                {
                    c.upgrade();
                }
            }

            if (permanent)
            {
                final AbstractCard c = GameUtilities.getMasterDeckInstance(card.uuid);
                if (c != null)
                {
                    if (c != card && c.canUpgrade())
                    {
                        c.upgrade();
                    }

                    player.bottledCardUpgradeCheck(c);
                }
            }

            final float x = (Settings.WIDTH / 4f) + ((result.size() - 1) * (AbstractCard.IMG_WIDTH * 0.75f));
            PCLEffects.TopLevelQueue.add(new UpgradeShineEffect2(x, Settings.HEIGHT / 2f));
            PCLEffects.TopLevelQueue.showCardBriefly(card.makeStatEquivalentCopy(), x, Settings.HEIGHT / 2f);
        }

        GameUtilities.refreshHandLayout(false);

        super.complete(result);
    }

    public UpgradeFromPile isPermanent(boolean isPermanent)
    {
        this.permanent = isPermanent;

        return this;
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.tooltips.upgrade.title;
    }
}