package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Augment extends PField_Random {
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<PCLAugmentCategory> categories = new ArrayList<>();
    public ArrayList<String> augmentIDs = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Augment
                && augmentIDs.equals(((PField_Augment) other).augmentIDs)
                && colors.equals(((PField_Augment) other).colors)
                && categories.equals(((PField_Augment) other).categories)
                && ((PField_Augment) other).random == random;
    }

    public FuncT1<Boolean, PCLAugment> getFullAugmentFilter() {
        return !augmentIDs.isEmpty() ? c -> EUIUtils.any(augmentIDs, id -> id.equals(c.data.ID)) :
                (c -> (colors.isEmpty() || colors.contains(c.data.cardColor))
                        && (categories.isEmpty() || categories.contains(c.data.category)
                ));
    }

    public String getFullAugmentString() {
        return getFullAugmentString(skill.getAmountRawString());
    }

    public String getFullAugmentString(Object value) {
        return !augmentIDs.isEmpty() ? getAugmentIDOrString() : random ? PSkill.TEXT.subjects_randomX(getAugmentOrString(value)) : getAugmentOrString(value);
    }

    public String getFullAugmentStringSingular() {
        return !augmentIDs.isEmpty() ? getAugmentIDOrString() : getAugmentXString(PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
    }

    public String getAugmentAndString() {
        return getAugmentAndString(skill.getAmountRawString());
    }

    public String getAugmentAndString(Object value) {
        return getAugmentXString(PCLCoreStrings::joinWithAnd, (s) -> EUIUtils.format(s, value));
    }

    public String getAugmentIDAndString() {
        return getAugmentIDAndString(augmentIDs);
    }

    public String getAugmentIDOrString() {
        return getAugmentIDOrString(augmentIDs);
    }

    public String getAugmentOrString() {
        return getAugmentOrString(skill.getAmountRawString());
    }

    public String getAugmentOrString(Object value) {
        return getAugmentXString(PCLCoreStrings::joinWithOr, (s) -> EUIUtils.format(s, value));
    }

    public final String getAugmentXString(FuncT1<String, ArrayList<String>> joinFunc, FuncT1<String, String> pluralFunc) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!colors.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(colors, EUIGameUtils::getColorName)));
        }
        if (!categories.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(categories, PCLAugmentCategory::getName)));
        }
        stringsToJoin.add(pluralFunc.invoke(PGR.core.tooltips.augment.plural()));

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    @Override
    public PField_Augment makeCopy() {
        return (PField_Augment) new PField_Augment()
                .setAugmentID(augmentIDs)
                .setColor(colors)
                .setCategory(categories)
                .setRandom(random);
    }

    public PField_Augment setColor(Collection<AbstractCard.CardColor> types) {
        this.colors.clear();
        this.colors.addAll(types);
        return this;
    }

    public PField_Augment setColor(AbstractCard.CardColor... types) {
        return setColor(Arrays.asList(types));
    }

    public PField_Augment setAugmentID(Collection<String> orbs) {
        this.augmentIDs.clear();
        this.augmentIDs.addAll(orbs);
        return this;
    }

    public PField_Augment setAugmentID(String... orbs) {
        return setAugmentID(Arrays.asList(orbs));
    }

    public PField_Augment setCategory(Collection<PCLAugmentCategory> types) {
        this.categories.clear();
        this.categories.addAll(types);
        return this;
    }

    public PField_Augment setCategory(PCLAugmentCategory... types) {
        return setCategory(Arrays.asList(types));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerColor(colors);
        editor.registerAugmentCategory(categories);
        editor.registerAugment(augmentIDs);
    }
}
