package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class PField implements Serializable
{
    public static final PCLCoreStrings TEXT = PGR.core.strings;
    public transient PSkill<?> skill;

    public abstract PField makeCopy();

    public boolean equals(PField other)
    {
        return other != null && this.getClass().equals(other.getClass());
    }

    // Enables selectors for modifying this objects fields to appear in the card editor
    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {

    }

    public PField setSkill(PSkill<?> skill)
    {
        this.skill = skill;
        return this;
    }

    public static String getAffinityAndString(ArrayList<PCLAffinity> affinities)
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.mapAsNonnull(affinities, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getAffinityAndOrString(ArrayList<PCLAffinity> affinities, boolean or)
    {
        return or ? getAffinityOrString(affinities) : getAffinityAndString(affinities);
    }

    public static String getAffinityLevelAndString(ArrayList<PCLAffinity> affinities)
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.mapAsNonnull(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public static String getAffinityLevelOrString(ArrayList<PCLAffinity> affinities)
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.mapAsNonnull(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public static String getAffinityLevelAndOrString(ArrayList<PCLAffinity> affinities, boolean or)
    {
        return or ? getAffinityLevelOrString(affinities) : getAffinityLevelAndString(affinities);
    }

    public static String getAffinityOrString(ArrayList<PCLAffinity> affinities)
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.mapAsNonnull(affinities, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getAffinityPowerAndString(ArrayList<PCLAffinity> affinities)
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.mapAsNonnull(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public static String getAffinityPowerOrString(ArrayList<PCLAffinity> affinities)
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.mapAsNonnull(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public static String getAffinityPowerString(ArrayList<PCLAffinity> affinities)
    {
        return EUIUtils.joinStrings(" ", EUIUtils.mapAsNonnull(affinities, a -> a.getLevelTooltip().getTitleOrIcon()));
    }

    public static String getAffinityString(ArrayList<PCLAffinity> affinities)
    {
        return EUIUtils.joinStrings(" ", EUIUtils.mapAsNonnull(affinities, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getCardNameForID(String cardID)
    {
        if (cardID != null)
        {
            // NOT using CardLibrary.getCard as the replacement patching on that method may cause text glitches or infinite loops in this method
            AbstractCard c = CardLibrary.cards.get(cardID);
            if (c != null)
            {
                return c.name;
            }

            // Try to load data on cards not in the library
            PCLCardData data = PCLCardData.getStaticData(cardID);
            if (data != null)
            {
                return data.strings.NAME;
            }

            // Try to load data from slots. Do not actually create cards here to avoid infinite loops
            PCLCustomCardSlot slot = PCLCustomCardSlot.get(cardID);
            if (slot != null)
            {
                HashMap<Settings.GameLanguage, CardStrings> languageMap = PCLDynamicData.parseLanguageStrings(slot.languageStrings);
                CardStrings language = languageMap != null ? PCLDynamicData.getStringsForLanguage(languageMap) : null;
                if (language != null)
                {
                    return language.NAME;
                }
            }

            return cardID;
        }
        return "";
    }

    public static String getCardIDAndString(ArrayList<String> cardIDs)
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.map(cardIDs, g -> "{" + getCardNameForID(g) + "}"));
    }

    public static String getCardIDOrString(ArrayList<String> cardIDs)
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(cardIDs, g -> "{" + getCardNameForID(g) + "}"));
    }

    public static String getGroupString(List<PCLCardGroupHelper> groups)
    {
        return groups.size() >= 3 ? PGR.core.strings.subjects_anyPile : PCLCoreStrings.joinWithOr(EUIUtils.mapAsNonnull(groups, g -> g.name));
    }

    public static String getGeneralAffinityAndString(ArrayList<PCLAffinity> affinities)
    {
        return affinities.isEmpty() ? getGeneralAffinityString() : getAffinityAndString(affinities);
    }

    public static String getGeneralAffinityOrString(ArrayList<PCLAffinity> affinities)
    {
        return affinities.isEmpty() ? getGeneralAffinityString() : getAffinityOrString(affinities);
    }

    public static String getGeneralAffinityString()
    {
        return PGR.core.tooltips.affinityGeneral.getTitleOrIcon();
    }

    public static String getGroupString(ArrayList<PCLCardGroupHelper> groupTypes, PCLCardSelection origin)
    {
        String base = getGroupString(groupTypes);
        return origin == PCLCardSelection.Top ? TEXT.subjects_topOf(base) : origin == PCLCardSelection.Bottom ? TEXT.subjects_bottomOf(base) : base;
    }

    public static String getOrbAndString(ArrayList<PCLOrbHelper> orbs, Object value)
    {
        return orbs.isEmpty() ? PCLCoreStrings.plural(PGR.core.tooltips.orb, value) : PCLCoreStrings.joinWithAnd(EUIUtils.mapAsNonnull(orbs, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getOrbOrString(ArrayList<PCLOrbHelper> orbs, Object value)
    {
        return orbs.isEmpty() ? PCLCoreStrings.plural(PGR.core.tooltips.orb, value) : PCLCoreStrings.joinWithOr(EUIUtils.mapAsNonnull(orbs, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getOrbString(ArrayList<PCLOrbHelper> orbs)
    {
        return EUIUtils.joinStrings(" ", EUIUtils.mapAsNonnull(orbs, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getPowerAndString(ArrayList<PCLPowerHelper> powers)
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.mapAsNonnull(powers, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getPowerOrString(ArrayList<PCLPowerHelper> powers)
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.mapAsNonnull(powers, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getPowerString(ArrayList<PCLPowerHelper> powers)
    {
        return EUIUtils.joinStrings(" ", EUIUtils.mapAsNonnull(powers, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getRelicNameForID(String relicID)
    {
        if (relicID != null)
        {
            AbstractRelic c = RelicLibrary.getRelic(relicID);
            if (c != null)
            {
                return c.name;
            }
        }
        return "";
    }

    public static String getRelicIDAndString(ArrayList<String> relicIDs)
    {
        return PCLCoreStrings.joinWithAnd(EUIUtils.map(relicIDs, g -> "{" + getRelicNameForID(g) + "}"));
    }

    public static String getRelicIDOrString(ArrayList<String> relicIDs)
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(relicIDs, g -> "{" + getRelicNameForID(g) + "}"));
    }

    public static String getStanceString(ArrayList<PCLStanceHelper> stances)
    {
        return PCLCoreStrings.joinWithOr(EUIUtils.map(stances, stance -> "{" + (stance.affinity != null ? stance.tooltip.title.replace(stance.affinity.getPowerSymbol(), stance.affinity.getFormattedPowerSymbol()) : stance.tooltip.title) + "}"));
    }

    public static String getTagAndString(ArrayList<PCLCardTag> tags)
    {
        return tags.isEmpty() ? TEXT.cedit_tags : PCLCoreStrings.joinWithAnd(EUIUtils.map(tags, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getTagAndOrString(ArrayList<PCLCardTag> tags, boolean or)
    {
        return or ? getTagOrString(tags) : getTagAndString(tags);
    }

    public static String getTagOrString(ArrayList<PCLCardTag> tags)
    {
        return tags.isEmpty() ? TEXT.cedit_tags : PCLCoreStrings.joinWithOr(EUIUtils.map(tags, a -> a.getTooltip().getTitleOrIcon()));
    }

    public static String getTagString(ArrayList<PCLCardTag> tags)
    {
        return tags.isEmpty() ? TEXT.cedit_tags : (EUIUtils.joinStrings(" ", EUIUtils.map(tags, a -> a.getTooltip().getTitleOrIcon())));
    }
}
