package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT5;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.actions.utility.CardFilterAction;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PField_CardGeneric extends PField_Random {
    public ArrayList<PCLCardGroupHelper> groupTypes = new ArrayList<>();
    public ArrayList<PCLCardGroupHelper> baseGroupTypes = groupTypes;
    public PCLCardSelection origin = PCLCardSelection.Manual;
    public PCLCardSelection destination = PCLCardSelection.Manual;
    public boolean forced;

    public PField_CardGeneric() {
        super();
    }

    public PField_CardGeneric(PField_CardGeneric other) {
        super(other);
        setCardGroup(other.groupTypes);
        setOrigin(other.origin);
        setDestination(other.destination);
        setNot(other.not);
        setForced(other.forced);
    }

    public SelectFromPile createAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> action, PCLUseInfo info, int subchoices) {
        return createAction(action, info, subchoices, true);
    }

    /**
     * Generates a generic SelectFromPile action on the groups specified by this effect.
     * If the skill's BASE amount is 0 or less, we will go for ALL cards in hand (skills that had their amounts set to 0 by mods still act on 0 cards)
     */
    public SelectFromPile createAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> action, PCLUseInfo info, int subchoices, boolean allowSelf) {
        CardGroup[] g = getCardGroup(info, allowSelf);
        int choiceSize = skill.useParent && g.length > 0 ? g[0].size() : skill.baseAmount <= 0 ? Integer.MAX_VALUE : skill.amount;


        // Set automatic selection when self targeting, or if the action is forced and we must select every available card
        if ((!skill.useParent && groupTypes.isEmpty() && allowSelf) || (forced && (skill.useParent || skill.baseAmount <= 0))) {
            return action.invoke(skill.getName(), skill.target.getTarget(info, skill.scope), choiceSize, PCLCardSelection.Random, g)
                    .setDestination(destination);
        }
        else if (subchoices > 0 && subchoices <= choiceSize) {
            return action.invoke(skill.getName(), skill.target.getTarget(info, skill.scope), subchoices, PCLCardSelection.Manual, g)
                    .setMaxChoices(choiceSize, origin == PCLCardSelection.Manual ? PCLCardSelection.Random : origin) // "Manual" will cause the max choices selection to do nothing
                    .setDestination(destination);
        }

        return action.invoke(skill.getName(), skill.target.getTarget(info, skill.scope), choiceSize, origin, g)
                .setDestination(destination)
                .showEffect(origin != PCLCardSelection.Manual, false);
    }

    public SelectFromPile createFilteredAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> action, PCLUseInfo info, int subchoices) {
        return createAction(action, info, subchoices);
    }

    public SelectFromPile createFilteredAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> action, PCLUseInfo info, int subchoices, boolean allowSelf) {
        return createAction(action, info, subchoices, allowSelf);
    }

    @Override
    public boolean equals(PField other) {
        return super.equals(other)
                && groupTypes.equals(((PField_CardGeneric) other).groupTypes)
                && origin.equals(((PField_CardGeneric) other).origin)
                && destination.equals(((PField_CardGeneric) other).destination)
                && not == ((PField_CardGeneric) other).not
                && forced == ((PField_CardGeneric) other).forced;
    }

    public final CardGroup[] getCardGroup(PCLUseInfo info) {
        return getCardGroup(info, true);
    }

    public final CardGroup[] getCardGroup(PCLUseInfo info, boolean allowSelf) {
        if (skill.useParent) {
            CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
            if (cards != null) {
                for (AbstractCard c : cards) {
                    g.addToBottom(c);
                }
            }
            return new CardGroup[]{g};
        }
        else if (groupTypes.isEmpty()) {
            if (skill.sourceCard != null && allowSelf) {
                CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                g.addToBottom(skill.sourceCard);
                return new CardGroup[]{g};
            }
            else {
                return new CardGroup[]{PCLCardGroupHelper.Hand.getCardGroup()};
            }
        }
        else {
            return EUIUtils.map(groupTypes, PCLCardGroupHelper::getCardGroup).toArray(new CardGroup[]{});
        }
    }

    public String getDestinationString(String base) {
        return getOriginWrappedString(base, destination);
    }

    public String getFullCardString() {
        return getShortCardString();
    }

    public String getFullCardString(Object parse) {
        return getFullCardString();
    }

    public String getFullCardStringSingular() {
        return getFullCardString();
    }

    public CardFilterAction getGenericPileAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> action, PCLUseInfo info, PCLActions order, int subchoices) {
        if (!skill.useParent && groupTypes.isEmpty() && skill.sourceCard != null) {
            return PCLActions.last.add(createFilteredAction(action, info, subchoices));
        }
        else {
            return order.add(createFilteredAction(action, info, subchoices));
        }
    }

    public String getGroupString() {
        return getGroupString(groupTypes, origin);
    }

    public String getShortCardString() {
        return isRandom() ? PSkill.TEXT.subjects_randomX(skill.pluralCard()) : skill.pluralCard();
    }

    public boolean hasGroups() {
        return !EUIUtils.isNullOrEmpty(groupTypes);
    }

    public boolean isRandom() {
        return origin == PCLCardSelection.Random;
    }

    @Override
    public PField_CardGeneric makeCopy() {
        return new PField_CardGeneric(this);
    }

    public void registerFBoolean(PCLCustomEffectEditingPane editor, String name, String desc) {
        editor.registerBoolean(name, desc, v -> forced = v, forced);
    }

    public void registerRequired(PCLCustomEffectEditingPane editor) {
        editor.registerBoolean(PGR.core.strings.cedit_required, PGR.core.strings.cetut_required1, v -> forced = v, forced);
    }

    public PField_CardGeneric resetTemporaryGroups() {
        this.groupTypes = baseGroupTypes;
        return this;
    }

    public PField_CardGeneric setCardGroup(Collection<PCLCardGroupHelper> gt) {
        this.groupTypes.clear();
        this.groupTypes.addAll(gt);
        this.baseGroupTypes = this.groupTypes;
        return this;
    }

    public PField_CardGeneric setCardGroup(PCLCardGroupHelper... gt) {
        return setCardGroup(Arrays.asList(gt));
    }

    public PField_CardGeneric setDestination(PCLCardSelection destination) {
        this.destination = destination;
        return this;
    }

    public PField_CardGeneric setForced(boolean value) {
        this.forced = value;
        return this;
    }

    public PField_CardGeneric setNot(boolean value) {
        this.not = value;
        return this;
    }

    public PField_CardGeneric setOrigin(PCLCardSelection origin) {
        this.origin = origin;
        return this;
    }

    public PField_CardGeneric setTemporaryGroups(ArrayList<PCLCardGroupHelper> cardGroups) {
        this.groupTypes = cardGroups;
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerOrigin(origin, origins -> setOrigin(origins.size() > 0 ? origins.get(0) : PCLCardSelection.Manual));
        editor.registerDestination(destination, destinations -> setDestination(destinations.size() > 0 ? destinations.get(0) : PCLCardSelection.Manual));
        editor.registerPile(groupTypes);
    }

    public boolean shouldHideGroupNames() {
        return groupTypes.size() == 1
                && (groupTypes.get(0) == PCLCardGroupHelper.Hand || skill.getEligibleOrigins().size() <= 1);
    }
}
