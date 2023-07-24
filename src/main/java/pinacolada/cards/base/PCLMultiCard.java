package pinacolada.cards.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.CostFilter;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.ChooseMulticardAction;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.fields.PCLCardSaveData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.MysteryCard;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCustomCond;
import pinacolada.utilities.CardPreviewList;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class PCLMultiCard extends PCLCard {
    protected CardPreviewList inheritedCards = new CardPreviewList();
    protected PCLMultiCardMove multiCardMove;
    protected boolean hasAttackOrSkill;

    public PCLMultiCard(PCLCardData cardData) {
        super(cardData);
    }

    // Augments are applied to children
    @Override
    public void addAugment(PCLAugment augment, boolean save) {
        for (AbstractCard c : getCards()) {
            if (c instanceof PCLCard) {
                ((PCLCard) c).addAugment(augment, false);
            }
        }
        super.addAugment(augment, save);
    }

    // Should not use effects directly on it unless its primary skill is disabled
    @Override
    public ArrayList<PSkill<?>> getFullEffects() {
        ArrayList<PSkill<?>> original = getEffects();
        return original.size() > 0 && original.get(0) instanceof PCLMultiCardMove ? original : super.getFullEffects();
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

    @Override
    public void renderUpgradePreview(SpriteBatch sb) {
        PCLMultiCard upgrade = EUIUtils.safeCast(cardData.tempCard, PCLMultiCard.class);
        if (upgrade == null || upgrade.uuid != this.uuid || (upgrade.timesUpgraded != (timesUpgraded + 1))) {
            cardData.tempCard = upgrade = (PCLMultiCard) this.makeSameInstanceOf();
            upgrade.isPreview = true;
            for (AbstractCard iCard : inheritedCards.getCards()) {
                upgrade.addInheritedCard(iCard.makeSameInstanceOf());
            }
            upgrade.refreshProperties();
            upgrade.previousAffinities = new ArrayList<>(upgrade.affinities.sorted);
            upgrade.upgrade();
            upgrade.displayUpgrades();
        }

        upgrade.current_x = this.current_x;
        upgrade.current_y = this.current_y;
        upgrade.drawScale = this.drawScale;
        upgrade.render(sb, false);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (AbstractCard card : inheritedCards.getCards()) {
            card.applyPowers();
        }
    }

    @Override
    public boolean onAddToDeck() {
        PCLEffects.Queue.callback(new ChooseMulticardAction(this));
        return super.onAddToDeck();
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

    @Override
    protected void onUpgrade() {
        for (EUICardPreview preview : inheritedCards) {
            AbstractCard card = preview.defaultPreview;
            if (card instanceof PCLCard && ((PCLCard) card).isMultiUpgrade() && card.timesUpgraded < this.timesUpgraded) {
                card.upgrade();
                preview.upgradedPreview.upgrade();
            }
            else if (!card.upgraded) {
                card.upgrade();
            }
        }
        refreshProperties();
    }

    @Override
    public PCLAugment removeAugment(int index, boolean save) {
        for (AbstractCard c : getCards()) {
            if (c instanceof PCLCard) {
                ((PCLCard) c).removeAugment(index, false);
            }
        }
        return super.removeAugment(index, save);
    }

    public void setup(Object input) {
        multiCardMove = createMulticardMove();
        addUseMove(multiCardMove);
    }

    @Override
    public void triggerWhenCreated(boolean startOfBattle) {
        if (inheritedCards.size() < multiCardMove.baseAmount) {
            while (inheritedCards.size() < multiCardMove.baseAmount) {
                addInheritedCard(new MysteryCard(false, createMysteryFilterFields()));
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

    // TODO make configurable using skill
    protected void addCardProperties(AbstractCard card) {
        if (this.cost == -2 || card.cost == -1) {
            this.cost = this.costForTurn = card.cost;
        }
        else if (card.cost > 0 && this.cost > -1) {
            this.cost = this.costForTurn = this.cost + card.cost;
        }

        refreshCardType(card);

        for (PCLCardTag tag : PCLCardTag.getAll()) {
            tag.set(this, tag.getInt(card));
        }

        if (card.type == PCLEnum.CardType.SUMMON) {
            updateHeal(card.baseHeal + baseHeal);
        }
    }

    public void addInheritedCard(AbstractCard card) {
        if (card != null) {
            for (int i = 0; i < timesUpgraded; i++) {
                card.upgrade();
            }
            inheritedCards.add(card);
            if (card instanceof PCLCard) {
                ((PCLCard) card).parent = this;
            }
            addCardProperties(card);
        }
    }

    public AbstractCard getCard(int index) {
        return inheritedCards.getCard(index);
    }

    public ArrayList<AbstractCard> getCards() {
        if (inheritedCards == null) {
            inheritedCards = new CardPreviewList();
        }
        return inheritedCards.getCards();
    }

    public PCLMultiCardMove getMultiCardMove() {
        if (multiCardMove == null) {
            multiCardMove = createMulticardMove();
            addUseMove(multiCardMove);
        }
        return multiCardMove;
    }

    @Override
    public EUICardPreview getPreview() {
        EUICardPreview currentPreview;
        if (EUIHotkeys.cycle.isJustPressed()) {
            currentPreview = inheritedCards.next(true);
        }
        else {
            currentPreview = inheritedCards.current();
        }

        if (currentPreview != null) {
            currentPreview.isMultiPreview = true;
        }
        return currentPreview;
    }

    public void onCardsRemoved() {

    }

    // TODO make configurable using skill
    protected void refreshCardType(AbstractCard card) {
        if (type != PCLEnum.CardType.SUMMON) {
            if (card.type == PCLEnum.CardType.SUMMON) {
                setCardType(PCLEnum.CardType.SUMMON);
            }
            else if (card.type == CardType.ATTACK) {
                if (this.type == CardType.POWER) {
                    PCLCardTag.Purge.set(this, 1);
                }
                hasAttackOrSkill = true;
                setCardType(CardType.ATTACK);
            }
            else if (card.type == CardType.POWER) {
                if (hasAttackOrSkill) {
                    PCLCardTag.Purge.set(this, 1);
                }
                else if (this.type == CardType.SKILL) {
                    setCardType(CardType.POWER);
                }
            }
            else if (card.type == CardType.SKILL) {
                if (this.type == CardType.POWER) {
                    PCLCardTag.Purge.set(this, 1);
                    setCardType(CardType.SKILL);
                }
                hasAttackOrSkill = true;
            }
        }
    }

    protected void refreshProperties() {
        this.cost = -2;
        for (PCLCardTag tag : PCLCardTag.getAll()) {
            tag.set(this, 0);
        }
        setCardType(CardType.SKILL);
        for (AbstractCard card : inheritedCards.getCards()) {
            addCardProperties(card);
        }
        updateHeal(0);
        initializeDescription();
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
        }).addCallback(this::onCardsRemoved);
    }

    public void replaceInheritedCard(AbstractCard original, AbstractCard incoming) {
        int index = inheritedCards.getMatchingIndex(original);
        if (incoming != null && index >= 0) {
            inheritedCards.set(index, incoming);
            if (incoming instanceof PCLCard) {
                ((PCLCard) incoming).parent = this;
            }
        }
        refreshProperties();
    }

    public PField_CardCategory createFilterFields() {
        return new PField_CardCategory();
    }

    public PField_CardCategory createMysteryFilterFields() {
        return createFilterFields().setCost(CostFilter.Cost0);
    }

    protected abstract PCLMultiCardMove createMulticardMove();

    public static class PCLMultiCardMove extends PCustomCond {
        protected PCLMultiCard multicard;

        public PCLMultiCardMove(PCLCardData data, PCLMultiCard multicard, int amount) {
            super(data, 0, amount);
            this.multicard = multicard;
        }

        @Override
        public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
            return EUIUtils.find(multicard.getCards(), card -> !card.cardPlayable(GameUtilities.asMonster(info.target))) == null;
        }

        @Override
        public void refresh(PCLUseInfo info, boolean conditionMet) {
            super.refresh(info, conditionMet);
            for (AbstractCard card : multicard.getCards()) {
                if (card instanceof PCLCard) {
                    ((PCLCard) card).refresh(GameUtilities.asMonster(info.source));
                }
                else {
                    card.calculateCardDamage(GameUtilities.asMonster(info.source));
                }
            }
        }

        protected void doCard(ActionT1<AbstractCard> childAction) {
            for (AbstractCard c : multicard.getCards()) {
                childAction.invoke(c);
            }
        }

        protected void doPCL(ActionT1<PCLCard> childAction) {
            for (AbstractCard c : multicard.getCards()) {
                if (c instanceof PCLCard) {
                    childAction.invoke((PCLCard) c);
                }
            }
        }

        @Override
        public String getSubText(PCLCardTarget perspective) {
            return multicard.getCards().size() > 0 ?
                    PGR.core.strings.act_has(PCLCoreStrings.joinWithAnd(c -> c.name, multicard.getCards())) : super.getSubText(perspective);
        }

        protected void useImpl(PCLUseInfo info, PCLActions order) {
            AbstractMonster m = EUIUtils.safeCast(info.target, AbstractMonster.class);
            ArrayList<AbstractCard> played = AbstractDungeon.actionManager.cardsPlayedThisTurn;
            // Allow Starter effects on inherited cards to take effect
            if (played != null && (played.isEmpty() || (played.size() == 1 && played.get(0) == sourceCard))) {
                AbstractDungeon.actionManager.cardsPlayedThisTurn.clear();
            }
            for (AbstractCard card : multicard.getCards()) {
                if (card instanceof PCLCard) {
                    ((PCLCard) card).onUse(info);
                }
                else {
                    card.use(AbstractDungeon.player, m);
                }
            }
            if (played != null && !played.isEmpty() && played.get(played.size() - 1) != sourceCard) {
                AbstractDungeon.actionManager.cardsPlayedThisTurn.add(sourceCard);
            }
        }

        @Override
        public void triggerOnAllyDeath(PCLCard c, PCLCardAlly ally) {
            doPCL(card -> card.triggerWhenKilled(ally));
        }

        @Override
        public void triggerOnAllySummon(PCLCard c, PCLCardAlly ally) {
            doPCL(card -> card.triggerWhenSummoned(ally));
        }

        @Override
        public void triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally) {
            doPCL(card -> card.triggerWhenTriggered(ally));
        }

        @Override
        public void triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally) {
            doPCL(card -> card.triggerWhenWithdrawn(ally));
        }

        @Override
        public void triggerOnCreate(AbstractCard c, boolean startOfBattle) {
            doPCL(card -> card.triggerWhenCreated(startOfBattle));
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
            boolean result = EUIUtils.any(multicard.getCards(), c -> c.dontTriggerOnUseCard);
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
        public void triggerOnPurge(AbstractCard c) {
            doPCL(PCLCard::triggerOnPurge);
        }

        @Override
        public void triggerOnReshuffle(AbstractCard c, CardGroup sourcePile) {
            doPCL(card -> card.triggerOnReshuffle(sourcePile));
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