package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.card.PermanentUpgradeEffect;
import pinacolada.interfaces.listeners.OnAddingToCardRewardListener;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLRuntimeLoadout;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardBonus extends EUIBase
{

    private final PCLCoreStrings.Rewards rEWARDS = PGR.core.strings.rewards;
    private final ArrayList<CardRewardBundle> bundles = new ArrayList<>();
    private RewardItem rewardItem;

    public PCLCardRewardBonus()
    {
        this(null);
    }

    public PCLCardRewardBonus(RewardItem rewardItem)
    {
        this.rewardItem = rewardItem;
    }

    public void add(AbstractCard card)
    {
        CardRewardBundle cardRewardBundle = getBundle(card);
        if (cardRewardBundle != null)
        {
            bundles.add(cardRewardBundle);
        }
    }

    public void close()
    {
        rewardItem = null;
        bundles.clear();
    }

    private CardRewardBundle getBundle(AbstractCard card)
    {
/*        if (card instanceof FoolCard_UltraRare)
        {
            return GetCursedRelicBundle(card);
        }*/
        if (card instanceof PCLCard)
        {
            for (PCLRuntimeLoadout series : PGR.core.dungeon.loadouts)
            {
                if (MathUtils.randomBoolean(0.25f) && series.bonus < 8)
                {
                    if (series.getCardPoolInPlay().containsKey(card.cardID) && GameUtilities.getMasterDeckCopies(card.cardID).isEmpty())
                    {
                        switch (series.bonus % 3)
                        {
                            case 0:
                                return getGoldBundle(card, series.bonus >= 4 ? 20 : 10);
                            case 1:
                                return getMaxHPBundle(card, series.bonus >= 6 ? 2 : 1);
                            case 2:
                                return getUpgradeBundle(card);
                        }
                    }
                }
            }
        }

        return null;
    }

    private CardRewardBundle getGoldBundle(AbstractCard card, int gold)
    {
        return new CardRewardBundle(card, this::receiveGold).setAmount(gold)
                .setIcon(ImageMaster.UI_GOLD, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.545f)
                .setText(rEWARDS.goldBonus(gold), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    private CardRewardBundle getMaxHPBundle(AbstractCard card, int maxHP)
    {
        return new CardRewardBundle(card, this::receiveMaxHP).setAmount(maxHP)
                .setIcon(ImageMaster.TP_HP, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.545f)
                .setText(rEWARDS.maxHPBonus(maxHP), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    private CardRewardBundle getUpgradeBundle(AbstractCard card)
    {
        return new CardRewardBundle(card, this::receiveUpgrade).setAmount(1)
                .setIcon(ImageMaster.TP_ASCENSION, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.545f)
                .setText(rEWARDS.commonUpgrade, Color.WHITE, -AbstractCard.RAW_W * 0.01f, -AbstractCard.RAW_H * 0.54f);
    }

    public void onCardObtained(AbstractCard hoveredCard)
    {
        for (CardRewardBundle cardRewardBundle : bundles)
        {
            if (cardRewardBundle.card == hoveredCard)
            {
                cardRewardBundle.acquired();
            }
        }
    }

    public void open(RewardItem rewardItem, ArrayList<AbstractCard> cards)
    {
        this.rewardItem = rewardItem;
        this.bundles.clear();

        final ArrayList<AbstractCard> toRemove = new ArrayList<>();
        for (AbstractCard card : cards)
        {
            if (card instanceof OnAddingToCardRewardListener && ((OnAddingToCardRewardListener) card).shouldCancel())
            {
                toRemove.add(card);
                continue;
            }

            add(card);
        }

        for (AbstractCard card : toRemove)
        {
            final AbstractCard replacement = PGR.core.dungeon.getRandomRewardCard(cards, true, false);
            if (replacement != null)
            {
                GameUtilities.copyVisualProperties(replacement, card);
                cards.remove(card);
                cards.add(replacement);
                if (rewardItem.cards != cards)
                {
                    rewardItem.cards.remove(card);
                    rewardItem.cards.add(replacement);
                }

                add(replacement);
            }
        }
    }

/*    private CardRewardBundle GetCursedRelicBundle(AbstractCard card)
    {
        return new CardRewardBundle(card, c -> PCLGameEffects.Queue.ObtainRelic(new CursedGlyph()))
                .SetIcon(CURSED_GLYPH.img, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.52f)
                .SetTooltip(CURSED_GLYPH.name, CURSED_GLYPH.description)
                .SetText(REWARDS.CursedRelic, Settings.RED_TEXT_COLOR, -AbstractCard.RAW_W * 0.10f, -AbstractCard.RAW_H * 0.54f);
    }*/

    private void receiveGold(CardRewardBundle bundle)
    {
        for (PCLRuntimeLoadout series : PGR.core.dungeon.loadouts)
        {
            if (series.getCardPoolInPlay().containsKey(bundle.card.cardID))
            {
                SFX.play(SFX.GOLD_GAIN);
                AbstractDungeon.player.gainGold(bundle.amount);
                series.bonus += 1;

                EUIUtils.logInfoIfDebug(this, "Obtained Gold Bonus (+" + bundle.amount + "): " + bundle.card.cardID);
                return;
            }
        }
    }

    private void receiveMaxHP(CardRewardBundle bundle)
    {
        for (PCLRuntimeLoadout series : PGR.core.dungeon.loadouts)
        {
            if (series.getCardPoolInPlay().containsKey(bundle.card.cardID))
            {
                AbstractDungeon.player.increaseMaxHp(bundle.amount, true);
                series.bonus += 1;

                EUIUtils.logInfoIfDebug(this, "Obtained Max HP Bonus (+" + bundle.amount + "): " + bundle.card.cardID);
                return;
            }
        }
    }

    private void receiveUpgrade(CardRewardBundle bundle)
    {
        for (PCLRuntimeLoadout series : PGR.core.dungeon.loadouts)
        {
            if (series.getCardPoolInPlay().containsKey(bundle.card.cardID))
            {
                PCLEffects.TopLevelQueue.add(new PermanentUpgradeEffect()).setFilter(c -> AbstractCard.CardRarity.COMMON.equals(c.rarity));
                series.bonus += 1;

                EUIUtils.logInfoIfDebug(this, "Obtained Common Upgrade Bonus (+" + bundle.amount + "): " + bundle.card.cardID);
                return;
            }
        }
    }

    public void remove(AbstractCard card)
    {
        for (int i = 0; i < bundles.size(); i++)
        {
            if (bundles.get(i).card == card)
            {
                bundles.remove(i);
                return;
            }
        }
    }

    public void updateImpl()
    {
        for (CardRewardBundle cardRewardBundle : bundles)
        {
            cardRewardBundle.update();
        }
    }

    public void renderImpl(SpriteBatch sb)
    {
        for (CardRewardBundle cardRewardBundle : bundles)
        {
            cardRewardBundle.render(sb);
        }
    }
}
