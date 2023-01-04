package pinacolada.cards.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.StartupCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.cards.pcl.special.MysteryCard;
import pinacolada.effects.PCLEffects;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PCustomCond;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLPreviewList;

import java.util.ArrayList;

public abstract class PCLMultiCard extends PCLCard
{
    public static final int COPIED_CARDS = 2;
    protected PCLPreviewList inheritedCards = new PCLPreviewList();
    protected boolean hasAttackOrSkill;

    public PCLMultiCard(PCLCardData cardData)
    {
        super(cardData);
    }

    @Override
    public PCLCardSaveData onSave() {
        ArrayList<String> ids = new ArrayList<>();
        for (AbstractCard card : getCards()) {
            ids.add(card.cardID);
        }
        auxiliaryData.additionalData = ids;
        return auxiliaryData;
    }

    @Override
    public void onLoad(PCLCardSaveData data) {
        super.onLoad(data);
        inheritedCards.clear();
        if (data.additionalData != null) {
            for (String id : data.additionalData) {
                AbstractCard card = CardLibrary.getCard(id);
                addInheritedCard(card);
            }
        }
        initializeDescription();
    }

    public AbstractCard getCard(int index) {
        return inheritedCards.getCard(index);
    }

    public ArrayList<AbstractCard> getCards() {
        if (inheritedCards == null) {
            inheritedCards = new PCLPreviewList();
        }
        return inheritedCards.getCards();
    }

    @Override
    public PCLMultiCard makeStatEquivalentCopy() {
        PCLMultiCard other = (PCLMultiCard) super.makeStatEquivalentCopy();
        for (AbstractCard iCard : getCards()) {
            other.addInheritedCard(iCard.makeStatEquivalentCopy());
        }
        other.refreshProperties();

        return other;
    }

    // Augments are applied to children
    @Override
    public void addAugment(PCLAugment augment, boolean save) {
        for (AbstractCard c : getCards())
        {
            if (c instanceof PCLCard)
            {
                ((PCLCard) c).addAugment(augment, false);
            }
        }
        super.addAugment(augment, save);
    }

    @Override
    public PCLAugment removeAugment(int index, boolean save) {
        for (AbstractCard c : getCards())
        {
            if (c instanceof PCLCard)
            {
                ((PCLCard) c).removeAugment(index, false);
            }
        }
        return super.removeAugment(index, save);
    }

    // Should not use effects directly on it unless its primary skill is disabled
    @Override
    public ArrayList<PSkill> getFullEffects() {
        ArrayList<PSkill> original = getEffects();
        return original.size() > 0 && original.get(0) instanceof PCLMultiCardMove ? original : super.getFullEffects();
    }

    @Override
    public void triggerWhenCreated(boolean startOfBattle) {
        if (inheritedCards.size() < COPIED_CARDS) {
            while (inheritedCards.size() < COPIED_CARDS) {
                addInheritedCard(new MysteryCard(false));
            }
        }
        for (AbstractCard card : inheritedCards.getCards()) {
            if (card instanceof MysteryCard) {
                AbstractCard newCard = ((MysteryCard) card).createObscuredCard();
                replaceInheritedCard(card, newCard);
            }
            card.isLocked = false;
            card.isSeen = true;
        }

        refreshProperties();

        super.triggerWhenCreated(startOfBattle);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (AbstractCard card : inheritedCards.getCards()) {
            card.applyPowers();
        }
    }

    public void removeInheritedCards() {
        PCLEffects.Queue.callback(() -> {
            for (EUICardPreview card : inheritedCards) {
                if (!(card.defaultPreview instanceof MysteryCard)) {
                    PCLEffects.TopLevelList.showAndObtain(card.defaultPreview.makeStatEquivalentCopy());
                }
            }
            this.inheritedCards.clear();
            refreshProperties();
        }).addCallback((ActionT0) this::onCardsRemoved);
    }


    public void replaceInheritedCard(AbstractCard original, AbstractCard incoming) {
        int index = inheritedCards.getMatchingIndex(original);
        if (incoming != null && index >= 0) {
            inheritedCards.set(index, incoming);
            if (incoming instanceof PCLCard)
            {
                ((PCLCard) incoming).parent = this;
            }
        }
        refreshProperties();
    }

    public void addInheritedCard(AbstractCard card) {
        if (card != null) {
            for (int i = 0; i < timesUpgraded; i++) {
                card.upgrade();
            }
            inheritedCards.add(card);
            if (card instanceof PCLCard)
            {
                ((PCLCard) card).parent = this;
            }
            addCardProperties(card);
        }
    }

    protected void addCardProperties(AbstractCard card) {
        if (this.cost == -2 || card.cost == -1) {
            this.cost = this.costForTurn = card.cost;
        } else if (card.cost > 0 && this.cost > -1) {
            this.cost = this.costForTurn = this.cost + card.cost;
        }

        if (card.type == CardType.ATTACK) {
            if (this.type == CardType.POWER) {
                PCLCardTag.Purge.set(this,1);
            }
            hasAttackOrSkill = true;
            this.type = CardType.ATTACK;
        } else if (card.type == CardType.POWER) {
            if (hasAttackOrSkill) {
                PCLCardTag.Purge.set(this,1);
            } else if (this.type == CardType.SKILL) {
                this.type = CardType.POWER;
            }
        } else if (card.type == CardType.SKILL) {
            if (this.type == CardType.POWER) {
                PCLCardTag.Purge.set(this,1);
                this.type = CardType.SKILL;
            }
            hasAttackOrSkill = true;
        }

        for (PCLCardTag tag : PCLCardTag.getAll()) {
            tag.set(this, tag.getInt(card));
        }
    }

    protected void refreshProperties() {
        this.cost = -2;
        for (PCLCardTag tag : PCLCardTag.getAll()) {
            tag.set(this, 0);
        }
        this.type = CardType.SKILL;
        for (AbstractCard card : inheritedCards.getCards()) {
            addCardProperties(card);
        }
        initializeDescription();
    }

    @Override
    protected void onUpgrade() {
        for (EUICardPreview preview : inheritedCards) {
            AbstractCard card = preview.defaultPreview;
            if (card instanceof PCLCard && ((PCLCard) card).isMultiUpgrade() && card.timesUpgraded < this.timesUpgraded)
            {
                card.upgrade();
                preview.upgradedPreview.upgrade();
            }
            else if (!card.upgraded)
            {
                card.upgrade();
            }
        }
        refreshProperties();
    }

    @Override
    public void renderUpgradePreview(SpriteBatch sb) {
        PCLMultiCard upgrade = EUIUtils.safeCast(cardData.tempCard, PCLMultiCard.class);
        if (upgrade == null || upgrade.uuid != this.uuid || (upgrade.timesUpgraded != (timesUpgraded + 1))) {
            cardData.tempCard = upgrade = (PCLMultiCard) this.makeSameInstanceOf();
            upgrade.isPreview = true;
            for (AbstractCard iCard : inheritedCards.getCards()) {
                ((PCLMultiCard) upgrade).addInheritedCard(iCard.makeSameInstanceOf());
            }
            ((PCLMultiCard) upgrade).refreshProperties();

            upgrade.upgrade();
            upgrade.displayUpgrades();
        }

        upgrade.current_x = this.current_x;
        upgrade.current_y = this.current_y;
        upgrade.drawScale = this.drawScale;
        upgrade.render(sb, false);
    }

    @Override
    public EUICardPreview getPreview() {
        EUICardPreview currentPreview;
        if (EUIHotkeys.cycle.isJustPressed()) {
            currentPreview = inheritedCards.next(true);
        } else {
            currentPreview = inheritedCards.current();
        }

        if (currentPreview != null) {
            currentPreview.isMultiPreview = true;
        }
        return currentPreview;
    }

    public void onCardsRemoved()
    {

    }

    public boolean isBanned(AbstractCard c)
    {
        return false;
    }

    public void setup(Object input)
    {
        addUseMove(new PCLMultiCardMove(cardData, this));
    }

    public static class PCLMultiCardMove extends PCustomCond
    {
        protected PCLMultiCard multicard;

        public PCLMultiCardMove(PCLCardData data, PCLMultiCard multicard)
        {
            super(data);
            this.multicard = multicard;
        }

        protected void useImpl(PCLUseInfo info)
        {
            AbstractMonster m = EUIUtils.safeCast(info.target, AbstractMonster.class);
            ArrayList<AbstractCard> played = AbstractDungeon.actionManager.cardsPlayedThisTurn;
            // Allow Starter effects on inherited cards to take effect
            if (played != null && (played.isEmpty() || (played.size() == 1 && played.get(0) == sourceCard))) {
                AbstractDungeon.actionManager.cardsPlayedThisTurn.clear();
            }
            for (AbstractCard card : multicard.getCards()) {
                if (card instanceof PCLCard) {
                    ((PCLCard) card).useEffects(info);
                } else {
                    card.use(AbstractDungeon.player, m);
                }
            }
            if (played != null && !played.isEmpty() && played.get(played.size() - 1) != sourceCard) {
                AbstractDungeon.actionManager.cardsPlayedThisTurn.add(sourceCard);
            }
        }

        @Override
        public boolean canPlay(AbstractCard c, AbstractMonster m)
        {
            return EUIUtils.find(multicard.getCards(), card -> !card.cardPlayable(m)) == null;
        }

        @Override
        public boolean triggerOnAllyDeath(PCLCard c, PCLCardAlly ally)
        {
            return doPCL(card -> card.triggerWhenKilled(ally));
        }

        @Override
        public boolean triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
        {
            return doPCL(card -> card.triggerWhenSummoned(ally));
        }

        @Override
        public boolean triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
        {
            return doPCL(card -> card.triggerWhenTriggered(ally));
        }

        @Override
        public boolean triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally)
        {
            return doPCL(card -> card.triggerWhenWithdrawn(ally));
        }

        @Override
        public boolean triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
        {
            return doPCL(card -> card.triggerOnReshuffle(sourcePile));
        }

        @Override
        public boolean triggerOnCreate(AbstractCard c, boolean startOfBattle)
        {
            return doPCL(card -> card.triggerWhenCreated(startOfBattle));
        }

        @Override
        public boolean triggerOnDiscard(AbstractCard c)
        {
            return doCard(AbstractCard::triggerOnManualDiscard);
        }

        @Override
        public boolean triggerOnDraw(AbstractCard c)
        {
            return doCard(AbstractCard::triggerWhenDrawn);
        }

        @Override
        public boolean triggerOnEndOfTurn(boolean isUsing)
        {
            return doCard(AbstractCard::triggerOnEndOfTurnForPlayingCard);
        }

        @Override
        public boolean triggerOnExhaust(AbstractCard c)
        {
            return doCard(AbstractCard::triggerOnExhaust);
        }

        @Override
        public boolean triggerOnOtherCardPlayed(AbstractCard c)
        {
            return doCard(card -> card.triggerOnOtherCardPlayed(c));
        }

        @Override
        public boolean triggerOnPurge(AbstractCard c)
        {
            return doPCL(PCLCard::triggerOnPurge);
        }

        @Override
        public boolean triggerOnScry()
        {
            return doCard(AbstractCard::triggerOnScry);
        }

        @Override
        public boolean triggerOnStartOfTurn()
        {
            return doCard(AbstractCard::atTurnStartPreDraw);
        }

        @Override
        public boolean triggerOnStartup()
        {
            return doCard((c) -> {
                if (c instanceof StartupCard) {
                    ((StartupCard) c).atBattleStartPreDraw();
                }
            });
        }

        @Override
        public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
        {
            super.refresh(m, c, conditionMet);
            for (AbstractCard card : multicard.getCards()) {
                if (card instanceof PCLCard) {
                    ((PCLCard) card).refresh(GameUtilities.asMonster(m));
                }
                else {
                    card.calculateCardDamage(GameUtilities.asMonster(m));
                }
            }
        }

        @Override
        public String getSubText()
        {
            return multicard.getCards().size() > 0 ?
                    PGR.core.strings.actions.has(PCLCoreStrings.joinWithAnd(EUIUtils.map(multicard.getCards(), c -> c.name))) : EUIUtils.format(cardData.strings.EXTENDED_DESCRIPTION[0], COPIED_CARDS);
        }


        protected boolean doCard(ActionT1<AbstractCard> childAction)
        {
            for (AbstractCard c : multicard.getCards()) {
                childAction.invoke(c);
            }
            return true;
        }

        protected boolean doPCL(ActionT1<PCLCard> childAction)
        {
            for (AbstractCard c : multicard.getCards()) {
                if (c instanceof PCLCard) {
                    childAction.invoke((PCLCard) c);
                }
            }
            return true;
        }
    }
}