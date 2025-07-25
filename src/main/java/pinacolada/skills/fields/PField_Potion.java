package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Potion extends PField_Random {
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<AbstractPotion.PotionRarity> rarities = new ArrayList<>();
    public ArrayList<AbstractPotion.PotionSize> sizes = new ArrayList<>();
    public ArrayList<String> potionIDs = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Potion
                && potionIDs.equals(((PField_Potion) other).potionIDs)
                && colors.equals(((PField_Potion) other).colors)
                && rarities.equals(((PField_Potion) other).rarities)
                && sizes.equals(((PField_Potion) other).sizes)
                && ((PField_Potion) other).random == random;
    }

    public FuncT1<Boolean, AbstractPotion> getFullPotionFilter() {
        return !potionIDs.isEmpty() ? c -> EUIUtils.any(potionIDs, id -> id.equals(c.ID)) :
                (c -> (colors.isEmpty() || colors.contains(EUIGameUtils.getPotionColor(c.ID)))
                        && (rarities.isEmpty() || rarities.contains(c.rarity))
                        && (sizes.isEmpty() || sizes.contains(c.size))
                        && !(c instanceof PotionSlot)
                );
    }

    public String getFullPotionString(Object requestor) {
        return getFullPotionStringForValue(skill.getAmountRawString(requestor));
    }

    public String getFullPotionStringForValue(Object value) {
        return !potionIDs.isEmpty() ? getPotionIDOrString() : random ? PSkill.TEXT.subjects_randomX(getPotionOrStringForValue(value)) : getPotionOrStringForValue(value);
    }

    public String getFullPotionStringSingular() {
        return !potionIDs.isEmpty() ? getPotionIDOrString() : getPotionXString(PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
    }

    public String getPotionAndStringForValue(Object requestor) {
        return getPotionAndString(skill.getAmountRawString(requestor));
    }

    public String getPotionAndString(Object value) {
        return getPotionXString(PCLCoreStrings::joinWithAnd, (s) -> EUIUtils.format(s, value));
    }

    public String getPotionIDAndString() {
        return getPotionIDAndString(potionIDs);
    }

    public String getPotionIDOrString() {
        return getPotionIDOrString(potionIDs);
    }

    public String getPotionOrString(Object requestor) {
        return getPotionOrStringForValue(skill.getAmountRawString(requestor));
    }

    public String getPotionOrStringForValue(Object value) {
        return getPotionXString(PCLCoreStrings::joinWithOr, (s) -> EUIUtils.format(s, value));
    }

    public final String getPotionXString(FuncT1<String, ArrayList<String>> joinFunc, FuncT1<String, String> pluralFunc) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!colors.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(colors, EUIGameUtils::getColorName)));
        }
        if (!rarities.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, EUIGameUtils::textForPotionRarity)));
        }
        if (!sizes.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(sizes, EUIGameUtils::textForPotionSize)));
        }
        stringsToJoin.add(pluralFunc.invoke(PSkill.TEXT.subjects_potionN));

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public boolean isFilterEmpty() {
        return potionIDs.isEmpty() && colors.isEmpty() && rarities.isEmpty() && sizes.isEmpty();
    }

    @Override
    public PField_Potion makeCopy() {
        return (PField_Potion) new PField_Potion()
                .setPotionID(potionIDs)
                .setColor(colors)
                .setRarity(rarities)
                .setSize(sizes)
                .setRandom(random);
    }

    public PField_Potion setColor(Collection<AbstractCard.CardColor> types) {
        this.colors.clear();
        this.colors.addAll(types);
        return this;
    }

    public PField_Potion setColor(AbstractCard.CardColor... types) {
        return setColor(Arrays.asList(types));
    }

    public PField_Potion setPotionID(Collection<String> orbs) {
        this.potionIDs.clear();
        this.potionIDs.addAll(orbs);
        return this;
    }

    public PField_Potion setPotionID(String... orbs) {
        return setPotionID(Arrays.asList(orbs));
    }

    public PField_Potion setRarity(Collection<AbstractPotion.PotionRarity> types) {
        this.rarities.clear();
        this.rarities.addAll(types);
        return this;
    }

    public PField_Potion setRarity(AbstractPotion.PotionRarity... types) {
        return setRarity(Arrays.asList(types));
    }

    public PField_Potion setSize(AbstractPotion.PotionSize... types) {
        return setSize(Arrays.asList(types));
    }

    public PField_Potion setSize(Collection<AbstractPotion.PotionSize> types) {
        this.sizes.clear();
        this.sizes.addAll(types);
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerColor(colors);
        editor.registerPotionRarity(rarities);
        editor.registerPotionSize(sizes);
        editor.registerPotion(potionIDs);
    }
}
