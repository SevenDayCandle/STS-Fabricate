package pinacolada.cards.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.CostFilter;
import extendedui.utilities.RotatingList;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.ChooseMulticardAction;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.fields.PCLCardSaveData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.MysteryCard;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.providers.PointerProvider;
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
            inheritedCards.add(card);
            if (card instanceof PCLCard) {
                ((PCLCard) card).parent = this;
                ((PCLCard) card).changeForm(getForm(), timesUpgraded);
            }
            else {
                for (int i = 0; i < timesUpgraded; i++) {
                    card.upgrade();
                }
            }
            addCardProperties(card);
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (AbstractCard card : inheritedCards.getCards()) {
            card.applyPowers();
        }
    }

    @Override
    public int changeForm(Integer form, int timesUpgraded) {
        int res = super.changeForm(form, timesUpgraded);
        if (inheritedCards != null) {
            for (AbstractCard c : inheritedCards.getCards()) {
                if (c instanceof PCLCard) {
                    ((PCLCard) c).changeForm(form, timesUpgraded);
                }
            }
        }
        return res;
    }

    public PField_CardCategory createMysteryFilterFields() {
        return getFieldsForFilter().makeCopy().setCost(CostFilter.Cost0);
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

    public PField_CardCategory getFieldsForFilter() {
        return getMultiCardMove().fields;
    }

    // Should not use effects directly on it unless its primary skill is disabled
    @Override
    public ArrayList<PSkill<?>> getFullEffects() {
        ArrayList<PSkill<?>> original = getEffects();
        return !original.isEmpty() && original.get(0) instanceof PCLMultiCardMove ? original : super.getFullEffects();
    }

    public PCLMultiCardMove getMultiCardMove() {
        if (multiCardMove == null) {
            multiCardMove = createMulticardMove();
            addUseMove(multiCardMove);
        }
        return multiCardMove;
    }

    @Override
    public void fillPreviews(RotatingList<EUIPreview> previews) {
        previews.addAll(inheritedCards);
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
    public int maxForms() {
        if (inheritedCards != null && inheritedCards.size() > 0) {
            return EUIUtils.max(inheritedCards.getCards(), c -> c instanceof PointerProvider ? ((PointerProvider) c).maxForms() : 1);
        }
        return super.maxForms();
    }

    @Override
    public int maxUpgrades() {
        int res = super.maxUpgrades();
        if (inheritedCards != null && inheritedCards.size() > 0) {
            for (AbstractCard c : inheritedCards.getCards()) {
                if (c instanceof PointerProvider) {
                    int u = ((PointerProvider) c).maxUpgrades();
                    if (u < 0) {
                        return u;
                    }
                    else {
                        res = Math.max(res, u);
                    }
                }
            }
        }
        return res;
    }

    @Override
    public boolean onAddToDeck() {
        PCLEffects.Queue.callback(new ChooseMulticardAction(this));
        return super.onAddToDeck();
    }

    public void onCardsRemoved() {

    }

    @Override
    public void onLoad(PCLCardSaveData data) {
        super.onLoad(data);
        inheritedCards.clear();
        if (data.additionalData != null) {
            for (String id : data.additionalData) {
                AbstractCard card = CardLibrary.getCopy(id);
                addInheritedCard(card);
            }
        }
        initializeDescription();
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
        updateHeal(0);
        for (AbstractCard card : inheritedCards.getCards()) {
            addCardProperties(card);
        }
        initializeDescription();
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

    @Override
    protected int setForm(Integer form, int timesUpgraded) {
        int res = super.setForm(form, timesUpgraded);
        if (inheritedCards != null) {
            for (AbstractCard c : inheritedCards.getCards()) {
                if (c instanceof PCLCard) {
                    ((PCLCard) c).setForm(form, timesUpgraded);
                }
            }
        }
        cardData.tempCard = null; // Invalidate preview
        return res;
    }

    public void setup(Object input) {
        multiCardMove = createMulticardMove();
        addUseMove(multiCardMove);
    }

    @Override
    public void triggerWhenCreated(boolean startOfBattle) {
        if (inheritedCards.size() < multiCardMove.baseAmount) {
            PField_CardCategory filter = createMysteryFilterFields();
            while (inheritedCards.size() < multiCardMove.baseAmount) {
                addInheritedCard(new MysteryCard(false, filter));
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
        public PCLMultiCardMove edit(ActionT1<PField_CardCategory> editFunc) {
            super.edit(editFunc);
            return this;
        }

        @Override
        public String getSubText(PCLCardTarget perspective, Object requestor) {
            return multicard.getCards().size() > 0 ?
                    PGR.core.strings.act_has(PCLCoreStrings.joinWithAnd(c -> c.name, multicard.getCards())) : super.getSubText(perspective, requestor);
        }

        @Override
        public void refresh(PCLUseInfo info, boolean conditionMet, boolean isUsing) {
            super.refresh(info, conditionMet, isUsing);
            for (AbstractCard card : multicard.getCards()) {
                if (card instanceof PCLCard) {
                    ((PCLCard) card).refreshImpl(info, isUsing);
                }
                else {
                    card.calculateCardDamage(GameUtilities.asMonster(info.source));
                }
            }
        }

        @Override
        public float renderIntentIcon(SpriteBatch sb, PCLCardAlly ally, float startY, boolean isPreview) {
            for (AbstractCard card : multicard.getCards()) {
                if (card instanceof PCLCard) {
                    for (PSkill<?> skill : ((PCLCard) card).getEffects()) {
                        PSkill<?> cur = skill;
                        while (cur != null) {
                            startY = cur.renderIntentIcon(sb, ally, startY, isPreview);
                            cur = cur.getChild();
                        }
                    }
                }
            }

            return startY;
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
        public void triggerOnAllyTrigger(PCLCard c, AbstractCreature target, PCLCardAlly ally, PCLCardAlly caller) {
            doPCL(card -> card.triggerWhenTriggered(ally, target, caller));
        }

        @Override
        public void triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally, boolean triggerEffects) {
            doPCL(card -> card.triggerWhenWithdrawn(ally, triggerEffects));
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
        public void triggerOnFetch(AbstractCard c, CardGroup sourcePile) {
            doPCL(card -> card.triggerOnFetch(sourcePile));
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

        protected void useImpl(PCLUseInfo info, PCLActions order) {
            AbstractMonster m = EUIUtils.safeCast(info.target, AbstractMonster.class);
            ArrayList<AbstractCard> played = AbstractDungeon.actionManager.cardsPlayedThisTurn;
            // Allow Starter effects on inherited cards to take effect
            if (played != null && (played.isEmpty() || (played.size() == 1 && played.get(0) == source))) {
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
            if (played != null && !played.isEmpty() && played.get(played.size() - 1) != source && source instanceof AbstractCard) {
                AbstractDungeon.actionManager.cardsPlayedThisTurn.add((AbstractCard) source);
            }
        }
    }
}