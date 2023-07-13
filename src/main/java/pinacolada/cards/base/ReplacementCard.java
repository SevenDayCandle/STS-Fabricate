package pinacolada.cards.base;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.patches.library.CardLibraryPatches;
import pinacolada.skills.skills.PCustomCond;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class ReplacementCard extends PCLDynamicCard {
    protected final ReplacementCardData builder;
    protected AbstractCard original;

    public ReplacementCard(ReplacementCardData builder) {
        super(builder);
        this.builder = builder;
        // Intentionally bypassing getCard to avoid the original itself being replaced
        this.original = CardLibraryPatches.getDirectCard(builder.originalID).makeStatEquivalentCopy();
    }

    // Base game pop up textures are loaded from CustomCard
    // ImageMaster is fine here because we'll be disposing this texture once the popup is destroyed
    @Override
    protected Texture createPopupTexture() {
        if (original instanceof CustomCard) {
            return CustomCard.getPortraitImage((CustomCard) original);
        }
        if (!Settings.PLAYTESTER_ART_MODE && !UnlockTracker.betaCardPref.getBoolean(original.cardID, false)) {
            Texture texture = ImageMaster.loadImage("images/1024Portraits/" + original.assetUrl + ".png");
            if (texture == null) {
                texture = ImageMaster.loadImage("images/1024PortraitsBeta/" + original.assetUrl + ".png");
            }
            return texture;
        } else {
            return ImageMaster.loadImage("images/1024PortraitsBeta/" + original.assetUrl + ".png");
        }
    }

    @Override
    public ReplacementCard makeCopy() {
        return new ReplacementCard(builder);
    }

    public void setup(Object input) {
        addUseMove(new ReplacementMove(builder, this));
    }

    @Override
    protected void onUpgrade() {
        super.onUpgrade();
        original.upgrade();
    }

    protected void updateOriginal() {
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

    public static class ReplacementMove extends PCustomCond {
        protected ReplacementCard card;

        public ReplacementMove(PCLCardData data, ReplacementCard card) {
            super(data);
            this.card = card;
        }

        @Override
        public boolean canPlay(PCLUseInfo info) {
            return card.original.cardPlayable(GameUtilities.asMonster(info.target));
        }

        @Override
        public void refresh(PCLUseInfo info, boolean conditionMet) {
            card.original.calculateCardDamage(GameUtilities.asMonster(info.target));
        }

        protected boolean doCard(ActionT1<AbstractCard> childAction) {
            childAction.invoke(card.original);
            return true;
        }

        @Override
        public String getSubText() {
            return card.original.rawDescription;
        }

        protected void useImpl(PCLUseInfo info, PCLActions order) {
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
        public void triggerOnDiscard(AbstractCard c) {
            doCard(AbstractCard::triggerOnManualDiscard);
        }

        @Override
        public void triggerOnDraw(AbstractCard c) {
            doCard(AbstractCard::triggerWhenDrawn);
        }

        @Override
        public boolean triggerOnEndOfTurn(boolean isUsing) {
            boolean result = card.dontTriggerOnUseCard;
            doCard(AbstractCard::triggerOnEndOfTurnForPlayingCard);
            return result;
        }

        @Override
        public void triggerOnExhaust(AbstractCard c) {
            doCard(AbstractCard::triggerOnExhaust);
        }

        @Override
        public void triggerOnOtherCardPlayed(AbstractCard c) {
            doCard(card -> card.triggerOnOtherCardPlayed(c));
        }

        @Override
        public void triggerOnRetain(AbstractCard c) {
            doCard(AbstractCard::onRetained);
        }

        @Override
        public void triggerOnScry(AbstractCard c) {
            doCard(AbstractCard::triggerOnScry);
        }
    }
}