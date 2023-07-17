package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

public abstract class PMove_GenerateCard extends PCallbackMove<PField_CardCategory> {
    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, int amount, int extra) {
        super(data, PCLCardTarget.None, amount, extra);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, int amount, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, PCLCardTarget.None, amount, extra);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, PCLCardGroupHelper... h) {
        super(data, target, amount);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, int extra, PCLCardGroupHelper... h) {
        super(data, target, amount, extra);
        fields.setCardGroup(h);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, String... cardData) {
        super(data, target, amount);
        fields.setCardIDs(cardData);
    }

    public PMove_GenerateCard(PSkillData<PField_CardCategory> data, PCLCardTarget target, int amount, int extra, String... cardData) {
        super(data, target, amount, extra);
        fields.setCardIDs(cardData);
    }

    protected boolean generateSpecificCards() {
        return !fields.cardIDs.isEmpty();
    }

    protected String getActionTitle() {
        return getActionTooltip().title;
    }

    @Override
    public String getAmountRawOrAllString() {
        return isOutOf() ? TEXT.subjects_xOfY(getAmountRawString(), getExtraRawString()) : getAmountRawString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(getActionTitle(), TEXT.subjects_x);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
        fields.registerFBoolean(editor, StringUtils.capitalize(TEXT.subjects_thisCard), null);
    }

    @Override
    public String getSubText() {
        String base = EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawOrAllString(), getCopiesOfString());
        return fields.origin != PCLCardSelection.Manual && generateSpecificCards() ? TEXT.subjects_randomly(base) : base;
    }

    protected ArrayList<AbstractCard> getBaseCards(PCLUseInfo info) {
        final int limit = Math.max(extra, amount);
        // When sourcing cards from the parent skill, make exact copies of the cards
        // Skip ephemeral cards because this can cause infinite loops
        if (useParent && info != null) {
            List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
            if (cards != null) {
                ArrayList<AbstractCard> created = new ArrayList<>();
                for (AbstractCard card : cards) {
                    if (!card.purgeOnUse) {
                        for (int i = 0; i < limit; i++) {
                            created.add(card.makeStatEquivalentCopy());
                        }
                    }
                }
                return created;
            }
        }
        // For these actions, also treat the "forced" parameter as a self-target to allow users to create effects that create copies of the calling card in a specific pile
        else if (fields.forced && sourceCard != null) {
            ArrayList<AbstractCard> created = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                created.add(sourceCard.makeStatEquivalentCopy());
            }
            return created;
        }
        // Otherwise, we prioritize making card ID copies first if they exist, then color-specific cards if colors exist, then any cards
        else {
            if (generateSpecificCards()) {
                ArrayList<AbstractCard> created = new ArrayList<>();
                // When creating specific cards in an X of Y effect, only create up to Y cards.
                if (isOutOf()) {
                    for (String cd : fields.cardIDs) {
                        AbstractCard c = PField_CardCategory.getCard(cd);
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
                // Otherwise, create X copies of each card
                else {
                    for (String cd : fields.cardIDs) {
                        // getCard already makes a copy
                        AbstractCard c = PField_CardCategory.getCard(cd);
                        if (c != null) {
                            for (int i = 0; i < limit; i++) {
                                created.add(c.makeCopy());
                            }
                        }
                    }
                }

                return created;
            }
            return EUIUtils.map(getSourceCards(limit),
                    AbstractCard::makeCopy);
        }

        return new ArrayList<>();
    }

    protected String getCopiesOfString() {
        return useParent ? TEXT.subjects_copiesOf(getInheritedThemString())
                : (fields.forced && sourceCard != null) ? TEXT.subjects_copiesOf(TEXT.subjects_thisCard)
                : fields.cardIDs.size() >= 4 ? fields.getShortCardString()
                : isOutOf() || fields.origin != PCLCardSelection.Manual ? fields.getFullCardOrString(getExtraRawString()) : fields.getFullCardAndString(getAmountRawString());
    }

    protected Iterable<AbstractCard> getSourceCards(int limit) {
        if (EUIUtils.any(fields.colors, f -> f != AbstractCard.CardColor.COLORLESS && f != GameUtilities.getActingColor())
                || EUIUtils.any(fields.types, f -> f == AbstractCard.CardType.STATUS)
                || EUIUtils.any(fields.rarities, f -> f != AbstractCard.CardRarity.COMMON && f != AbstractCard.CardRarity.UNCOMMON && f != AbstractCard.CardRarity.RARE && f != AbstractCard.CardRarity.CURSE)) {
            return GameUtilities.getCardsFromAllColorCombatPool(fields.getFullCardFilter(), limit);
        }
        else if (!fields.rarities.isEmpty() || !fields.types.isEmpty()) {
            return GameUtilities.getCardsFromFullCombatPool(fields.getFullCardFilter(), limit);
        }
        else {
            return GameUtilities.getCardsFromStandardCombatPool(fields.getFullCardFilter(), limit);
        }
    }

    protected boolean isOutOf() {
        return extra > amount;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> callback) {
        CardGroup choice = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        choice.group = getBaseCards(info);
        // When not doing an X out of Y choice, amount may produce more than the advertised amount if we are generating multiple card IDs
        int itemsToGet = isOutOf() ? amount : choice.group.size();

        order.selectFromPile(getName(), itemsToGet, choice)
                .setOptions((!isOutOf() ? PCLCardSelection.Random : fields.origin), !fields.not)
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
