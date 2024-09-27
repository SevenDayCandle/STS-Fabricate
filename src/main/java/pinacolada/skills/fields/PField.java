package pinacolada.skills.fields;

import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLIntentType;
import pinacolada.orbs.PCLOrbData;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class PField implements Serializable {
    static final long serialVersionUID = 1L;
    public static final PCLCoreStrings TEXT = PGR.core.strings;
    public transient PSkill<?> skill;

    public static String getAffinityAndOrString(ArrayList<PCLAffinity> affinities, boolean or) {
        return or ? getAffinityOrString(affinities) : getAffinityAndString(affinities);
    }

    public static String getAffinityAndString(ArrayList<PCLAffinity> affinities) {
        return PCLCoreStrings.joinWithAnd(PField::safeInvokeTip, affinities);
    }

    public static String getAffinityOrString(ArrayList<PCLAffinity> affinities) {
        return PCLCoreStrings.joinWithOr(PField::safeInvokeTip, affinities);
    }

    public static String getAffinityString(ArrayList<PCLAffinity> affinities) {
        return EUIUtils.joinStringsMapNonnull(" ", PField::safeInvokeTip, affinities);
    }

    public static String getAugmentIDAndString(ArrayList<String> augmentIDs) {
        return PCLCoreStrings.joinWithAnd(g -> "{" + PCLAugmentData.getAugmentNameForID(g) + "}", augmentIDs);
    }

    public static String getAugmentIDOrString(ArrayList<String> augmentIDs) {
        return PCLCoreStrings.joinWithOr(g -> "{" + PCLAugmentData.getAugmentNameForID(g) + "}", augmentIDs);
    }

    public static String getBlightIDAndString(ArrayList<String> relicIDs) {
        return PCLCoreStrings.joinWithAnd(g -> "{" + GameUtilities.getBlightNameForID(g) + "}", relicIDs);
    }

    public static String getBlightIDOrString(ArrayList<String> relicIDs) {
        return PCLCoreStrings.joinWithOr(g -> "{" + GameUtilities.getBlightNameForID(g) + "}", relicIDs);
    }

    public static String getCardIDAndString(ArrayList<String> cardIDs, int upgrade, int form) {
        return PCLCoreStrings.joinWithAnd(c -> getCardIDString(c, upgrade, form), cardIDs);
    }

    public static String getCardIDOrString(ArrayList<String> cardIDs, int upgrade, int form) {
        return PCLCoreStrings.joinWithOr(c -> getCardIDString(c, upgrade, form), cardIDs);
    }

    public static String getCardIDString(String cardID, int upgrade, int form) {
        return "{" + GameUtilities.getCardNameForID(cardID, upgrade, form) + "}";
    }

    public static String getGeneralAffinityAndString(ArrayList<PCLAffinity> affinities) {
        return affinities.isEmpty() ? getGeneralAffinityString() : getAffinityAndString(affinities);
    }

    public static String getGeneralAffinityOrString(ArrayList<PCLAffinity> affinities) {
        return affinities.isEmpty() ? getGeneralAffinityString() : getAffinityOrString(affinities);
    }

    public static String getGeneralAffinityString() {
        return PGR.core.tooltips.affinityGeneral.title;
    }

    public static String getGroupString(ArrayList<PCLCardGroupHelper> groupTypes, PCLCardSelection origin) {
        String base = getGroupString(groupTypes);
        return getOriginWrappedString(base, origin);
    }

    public static String getGroupString(List<PCLCardGroupHelper> groups) {
        return groups.size() >= 3 ? PGR.core.strings.subjects_anyPile() : PCLCoreStrings.joinWithOr(g -> g.name, groups);
    }

    public static String getIntentString(ArrayList<PCLIntentType> intents) {
        return PCLCoreStrings.joinWithOr(PCLIntentType::getActionString, intents);
    }

    public static String getOrbAndString(ArrayList<String> orbs, Object value) {
        return orbs.isEmpty() ? PCLCoreStrings.plural(PGR.core.tooltips.orb, value) : PCLCoreStrings.joinWithAnd(PField::safeInvokeTipForOrbID, orbs);
    }

    public static String getOrbOrString(ArrayList<String> orbs, Object value) {
        return orbs.isEmpty() ? PCLCoreStrings.plural(PGR.core.tooltips.orb, value) : PCLCoreStrings.joinWithOr(PField::safeInvokeTipForOrbID, orbs);
    }

    public static String getOrbString(ArrayList<String> orbs) {
        return EUIConfiguration.enableDescriptionIcons.get() ? EUIUtils.joinStringsMapNonnull(" ", PField::safeInvokeTipForOrbID, orbs) : getOrbAndString(orbs, 1);
    }

    public static String getOriginWrappedString(String base, PCLCardSelection origin) {
        return origin == PCLCardSelection.Top ? TEXT.subjects_topOf(base) : origin == PCLCardSelection.Bottom ? TEXT.subjects_bottomOf(base) : base;
    }

    public static String getPotionIDAndString(ArrayList<String> potionIDs) {
        return PCLCoreStrings.joinWithAnd(g -> "{" + GameUtilities.getPotionNameForID(g) + "}", potionIDs);
    }

    public static String getPotionIDOrString(ArrayList<String> potionIDs) {
        return PCLCoreStrings.joinWithOr(g -> "{" + GameUtilities.getPotionNameForID(g) + "}", potionIDs);
    }

    public static String getPowerAndString(ArrayList<String> powers) {
        return PCLCoreStrings.joinWithAnd(PField::safeInvokeTipForPowerID, powers);
    }

    public static String getPowerOrString(ArrayList<String> powers) {
        return PCLCoreStrings.joinWithOr(PField::safeInvokeTipForPowerID, powers);
    }

    public static String getPowerString(ArrayList<String> powers) {
        return EUIConfiguration.enableDescriptionIcons.get() ? EUIUtils.joinStringsMapNonnull(" ", PField::safeInvokeTipForPowerID, powers) : getPowerAndString(powers);
    }

    public static String getPowerTitleAndString(ArrayList<String> powers) {
        return PCLCoreStrings.joinWithAnd(PField::safeInvokeTipTitleForPowerID, powers);
    }

    public static String getRelicIDAndString(ArrayList<String> relicIDs) {
        return PCLCoreStrings.joinWithAnd(g -> "{" + GameUtilities.getRelicNameForID(g) + "}", relicIDs);
    }

    public static String getRelicIDOrString(ArrayList<String> relicIDs) {
        return PCLCoreStrings.joinWithOr(g -> "{" + GameUtilities.getRelicNameForID(g) + "}", relicIDs);
    }

    public static String getTagAndOrString(ArrayList<PCLCardTag> tags, boolean or) {
        return or ? getTagOrString(tags) : getTagAndString(tags);
    }

    // If we are not displaying tags as card tag icons, we should not render them as icons in the description either even if the EUI setting is disabled
    public static String getTagAndString(ArrayList<PCLCardTag> tags) {
        return tags.isEmpty() ? TEXT.cedit_tags : PCLCoreStrings.joinWithAnd(PGR.config.displayCardTagDescription.get() ? PField::safeInvokeTipTitle : PField::safeInvokeTip, tags);
    }

    public static String getTagOrString(ArrayList<PCLCardTag> tags) {
        return tags.isEmpty() ? TEXT.cedit_tags : PCLCoreStrings.joinWithOr(PGR.config.displayCardTagDescription.get() ? PField::safeInvokeTipTitle : PField::safeInvokeTip, tags);
    }

    protected static String safeInvokeTip(TooltipProvider provider) {
        return provider != null ? String.valueOf(provider.getTooltip()) : null;
    }

    protected static String safeInvokeTipForOrbID(String id) {
        PCLOrbData data = PCLOrbData.getStaticDataOrCustom(id);
        return data != null ? safeInvokeTip(data) : id;
    }

    protected static String safeInvokeTipForPowerID(String id) {
        PCLPowerData data = PCLPowerData.getStaticDataOrCustom(id);
        return data != null ? safeInvokeTip(data) : id;
    }

    protected static String safeInvokeTipTitle(TooltipProvider provider) {
        return provider != null ? String.valueOf(provider.getTooltip().title) : null;
    }

    protected static String safeInvokeTipTitleForPowerID(String id) {
        PCLPowerData data = PCLPowerData.getStaticDataOrCustom(id);
        return data != null ? "{" + safeInvokeTipTitle(data) + "}" : id;
    }

    public boolean equals(PField other) {
        return other != null && this.getClass().equals(other.getClass());
    }

    public int getQualiferRange() {
        return 0;
    }

    public String getQualifierText(int i) {
        return "";
    }

    public ArrayList<Integer> getQualifiers(PCLUseInfo info) {
        return new ArrayList<>();
    }

    public int getWorth() {
        return 1;
    }

    public void makePreviews(RotatingList<EUIPreview> previews) {

    }

    public int randomize(Random rng, int value) {
        return getWorth();
    }

    public PField setSkill(PSkill<?> skill) {
        this.skill = skill;
        return this;
    }

    public abstract PField makeCopy();

    // Enables selectors for modifying this objects fields to appear in the card editor
    public abstract void setupEditor(PCLCustomEffectEditingPane editor);
}
