package pinacolada.cards.base;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.skills.PCustomCond;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class ReplacementCard extends PCLDynamicCard
{
    protected final ReplacementCardBuilder builder;
    protected AbstractCard original;

    public ReplacementCard(ReplacementCardBuilder builder)
    {
        super(builder);
        this.builder = builder;
        this.original = builder.original;
    }

    @Override
    protected void onUpgrade()
    {
        super.onUpgrade();
        original.upgrade();
    }

    @Override
    public ReplacementCard makeCopy()
    {
        return new ReplacementCard(builder);
    }

    protected void updateOriginal()
    {
        original.baseDamage = this.baseDamage;
        original.baseBlock = this.baseBlock;
        original.baseMagicNumber = this.baseMagicNumber;
        original.isDamageModified = this.isDamageModified;
        original.isBlockModified = this.isBlockModified;
        original.isMagicNumberModified = this.isMagicNumberModified;
        original.multiDamage = this.multiDamage;
        original.damage = this.damage;
        original.block = this.block;
        original.magicNumber = this.magicNumber;
        original.costForTurn = this.costForTurn;
    }

    public void setup(Object input)
    {
        addUseMove(new ReplacementMove(builder, this));
    }

    public static class ReplacementMove extends PCustomCond
    {
        protected ReplacementCard card;

        public ReplacementMove(PCLCardData data, ReplacementCard card)
        {
            super(data);
            this.card = card;
        }

        protected void useImpl(PCLUseInfo info)
        {
            AbstractMonster m = EUIUtils.safeCast(info.target, AbstractMonster.class);
            ArrayList<AbstractCard> played = AbstractDungeon.actionManager.cardsPlayedThisTurn;
            // Allow Starter effects on inherited cards to take effect
            if (played != null && (played.isEmpty() || (played.size() == 1 && played.get(0) == sourceCard))) {
                AbstractDungeon.actionManager.cardsPlayedThisTurn.clear();
            }
            card.updateOriginal();
            card.original.use(AbstractDungeon.player, m);
            if (played != null && !played.isEmpty() && played.get(played.size() - 1) != sourceCard) {
                AbstractDungeon.actionManager.cardsPlayedThisTurn.add(sourceCard);
            }
        }

        @Override
        public boolean canPlay(AbstractCard c, AbstractMonster m)
        {
            return card.original.cardPlayable(m);
        }

        @Override
        public void triggerOnDiscard(AbstractCard c)
        {
            doCard(AbstractCard::triggerOnManualDiscard);
        }

        @Override
        public void triggerOnDraw(AbstractCard c)
        {
            doCard(AbstractCard::triggerWhenDrawn);
        }

        @Override
        public boolean triggerOnEndOfTurn(boolean isUsing)
        {
            boolean result = card.dontTriggerOnUseCard;
            doCard(AbstractCard::triggerOnEndOfTurnForPlayingCard);
            return result;
        }

        @Override
        public void triggerOnExhaust(AbstractCard c)
        {
            doCard(AbstractCard::triggerOnExhaust);
        }

        @Override
        public void triggerOnOtherCardPlayed(AbstractCard c)
        {
            doCard(card -> card.triggerOnOtherCardPlayed(c));
        }

        @Override
        public void triggerOnRetain(AbstractCard c)
        {
            doCard(AbstractCard::onRetained);
        }

        @Override
        public void triggerOnScry(AbstractCard c)
        {
            doCard(AbstractCard::triggerOnScry);
        }

        @Override
        public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
        {
            card.original.calculateCardDamage(GameUtilities.asMonster(m));
        }

        @Override
        public String getSubText()
        {
            return card.original.rawDescription;
        }


        protected boolean doCard(ActionT1<AbstractCard> childAction)
        {
            childAction.invoke(card.original);
            return true;
        }
    }
}