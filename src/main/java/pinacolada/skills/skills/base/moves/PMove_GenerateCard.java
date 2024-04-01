package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.CostFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.CardFlag;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PMove_GenerateCard extends PCallbackMove<PField_CardModify> {
    public PMove_GenerateCard(PSkillData<PField_CardModify> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, int amount, int extra) {
        super(data, PCLCardTarget.None, amount, extra);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, int amount, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount, extra);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, PCLCardTarget target, int amount, PCLCardGroupHelper... h) {
        super(data, target, amount);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, PCLCardTarget target, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, target, amount, extra);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, PCLCardTarget target, int amount, String... cardData) {
        super(data, target, amount);
        fields.setCardIDs(cardData);
    }

    public PMove_GenerateCard(PSkillData<PField_CardModify> data, PCLCardTarget target, int amount, int extra, String... cardData) {
        super(data, target, amount, extra);
        fields.setCardIDs(cardData);
    }

    protected boolean canMakeCopy(AbstractCard card) {
        return true;
    }

    protected String getActionTitle() {
        return getActionTooltip().title;
    }

    @Override
    public String getAmountRawOrAllString(Object requestor) {
        return isOutOf() ? TEXT.subjects_xOfY(getAmountRawString(requestor), getExtraRawString(requestor)) : getAmountRawString(requestor);
    }

    protected ArrayList<AbstractCard> getBaseCards(PCLUseInfo info) {
        final int limit = Math.max(extra, refreshAmount(info));
        // When sourcing cards from the parent skill, make exact copies of the cards
        if (useParent && info != null) {
            List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
            if (cards != null) {
                ArrayList<AbstractCard> created = new ArrayList<>();
                for (AbstractCard card : cards) {
                    if (canMakeCopy(card)) {
                        for (int i = 0; i < limit; i++) {
                            created.add(card.makeStatEquivalentCopy());
                        }
                    }
                }
                return created;
            }
        }
        // For these actions, also treat the "not" parameter as a self-target to allow users to create effects that create copies of the calling card in a specific pile
        else if (fields.not && source instanceof AbstractCard) {
            ArrayList<AbstractCard> created = new ArrayList<>();
            if (canMakeCopy((AbstractCard) source)) {
                for (int i = 0; i < limit; i++) {
                    created.add(((AbstractCard) source).makeStatEquivalentCopy());
                }
            }
            return created;
        }
        // Otherwise, we prioritize making card ID copies first if they exist, then color-specific cards if colors exist, then any cards
        else {
            ArrayList<AbstractCard> created;
            if (fields.targetsSpecificCards()) {
                created = new ArrayList<>();
                // When creating specific cards in an X of Y effect, only create up to Y cards.
                if (isOutOf()) {
                    for (String cd : fields.cardIDs) {
                        AbstractCard c = PField_CardModify.getCard(cd);
                        if (c != null) {
                            created.add(c.makeCopy());
                        }
                    }
                    // If the list is not empty and we have less than Y cards, we can create 1 of each card until we have Y cards
                    if (!created.isEmpty()) {
                        int ind = 0;
                        while (created.size() < limit) {
                            created.add(created.get(0).makeCopy());
                            ind++;
                        }
                    }
                }
                // If "or" mode, randomly choose copies up to X
                else if (fields.random) {
                    for (int i = 0; i < limit; i++) {
                        AbstractCard c = PField_CardModify.getCard(GameUtilities.getRandomElement(fields.cardIDs));
                        if (c != null) {
                            created.add(c.makeCopy());
                        }
                    }
                }
                // Otherwise, create X copies of each card
                else {
                    for (String cd : fields.cardIDs) {
                        AbstractCard c = PField_CardModify.getCard(cd);
                        if (c != null) {
                            for (int i = 0; i < limit; i++) {
                                created.add(c.makeCopy());
                            }
                        }
                    }
                }
            }
            else {
                created = EUIUtils.map(getSourceCards(limit),
                        AbstractCard::makeCopy);
            }

            // For created cards, upgrade them as necessary
            for (int i = 0; i < extra2; i++) {
                for (AbstractCard c : created) {
                    c.upgrade();
                }
            }

            return created;
        }

        return new ArrayList<>();
    }

    protected String getCopiesOfString(Object requestor) {
        return useParent ? TEXT.subjects_copiesOf(getInheritedThemString())
                : (fields.not && source != null) ? TEXT.subjects_copiesOf(TEXT.subjects_thisCard())
                : fields.cardIDs.size() >= 4 ? fields.getShortCardString()
                : isOutOf() ? fields.getFullCardOrString(getExtraRawString(requestor)) :
                fields.random ? fields.getFullCardOrString(getAmountRawString(requestor)) :
                fields.getFullCardAndString(getAmountRawString(requestor));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x);
    }

    protected Collection<AbstractCard> getSourceCards(int limit) {
        if (fields.random || fields.isFilterSolo()) {
            return getSourceCardsImpl(getSourceFilter(), limit);
        }
        else if (!fields.isFilterEmpty()) {
            ArrayList<AbstractCard> cards = new ArrayList<>();
            for (AbstractCard.CardColor co : fields.colors) {
                cards.addAll(getSourceCardsImpl(c -> c.color == co, limit));
            }
            for (AbstractCard.CardRarity co : fields.rarities) {
                cards.addAll(getSourceCardsImpl(c -> c.rarity == co, limit));
            }
            for (AbstractCard.CardType co : fields.types) {
                cards.addAll(getSourceCardsImpl(c -> c.type == co, limit));
            }
            for (PCLAffinity af : fields.affinities) {
                cards.addAll(getSourceCardsImpl(c -> GameUtilities.hasAffinity(c, af), limit));
            }
            for (PCLCardTag af : fields.tags) {
                cards.addAll(getSourceCardsImpl(af::has, limit));
            }
            for (CostFilter af : fields.costs) {
                cards.addAll(getSourceCardsImpl(af::check, limit));
            }
            for (String af : fields.flags) {
                CardFlag cf = CardFlag.get(af);
                if (cf != null) {
                    cards.addAll(getSourceCardsImpl(cf::has, limit));
                }
            }
            for (String af : fields.loadouts) {
                PCLLoadout cf = PCLLoadout.get(af);
                if (cf != null) {
                    cards.addAll(getSourceCardsImpl(cf::isCardFromLoadout, limit));
                }
            }
            return cards;
        }
        else {
            return fields.or ? GameUtilities.getCardsFromAllColorCombatPool(getSourceFilter(), limit): GameUtilities.getCardsFromStandardCombatPool(getSourceFilter(), limit);
        }
    }

    protected Collection<AbstractCard> getSourceCardsImpl(FuncT1<Boolean, AbstractCard> cardCond, int limit) {
        if (fields.or) {
            return GameUtilities.getCardsFromAllColorCombatPool(cardCond, limit);
        }
        Collection<AbstractCard> res = GameUtilities.getCardsFromFullCombatPool(cardCond, limit);
        if (!res.isEmpty()) {
            return res;
        }
        return GameUtilities.getCardsFromAllColorCombatPool(cardCond, limit);
    }

    protected FuncT1<Boolean, AbstractCard> getSourceFilter() {
        return isMetascaling() ? fields.getBaseCardFilter() : c -> fields.getBaseCardFilter().invoke(c) && GameUtilities.isObtainableInCombat(c);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawOrAllString(requestor), getCopiesOfString(requestor));
        return (fields.random) && fields.targetsSpecificCards() ? TEXT.subjects_randomly(base) : base;
    }

    protected boolean isOutOf() {
        return extra > amount;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
        fields.registerRequired(editor);
        fields.registerNotBoolean(editor, StringUtils.capitalize(TEXT.subjects_thisCard()), null);
        fields.registerRBoolean(editor, StringUtils.capitalize(TEXT.cedit_or), null);
        fields.registerFBoolean(editor, StringUtils.capitalize(TEXT.cedit_required), null);
        fields.registerOrBoolean(editor, TEXT.cedit_cardPoolRestrict, TEXT.cetut_cardPoolRestrict);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> callback) {
        CardGroup choice = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        choice.group = getBaseCards(info);
        // When not doing an X out of Y choice, amount may produce more than the advertised amount if we are generating multiple card IDs
        int itemsToGet = isOutOf() ? refreshAmount(info) : choice.group.size();

        order.selectFromPile(getName(), itemsToGet, choice)
                .setOptions((!isOutOf() ? PCLCardSelection.Random : fields.origin), !fields.forced)
                .addCallback(cards -> {
                    for (AbstractCard c : cards) {
                        performAction(info, order, c);
                    }
                    info.setData(cards);
                    callback.invoke(info);
                    if (this.childEffect != null) {
                        this.childEffect.use(info, order);
                    }
                });
    }

    public abstract EUITooltip getActionTooltip();

    public abstract void performAction(PCLUseInfo info, PCLActions order, AbstractCard c);


}
