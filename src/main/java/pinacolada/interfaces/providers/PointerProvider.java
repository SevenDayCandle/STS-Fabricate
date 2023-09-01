package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.cards.base.cardText.ConditionToken;
import pinacolada.cards.base.cardText.PointerToken;
import pinacolada.cards.base.cardText.SymbolToken;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;
import pinacolada.skills.Skills;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.utilities.UniqueList;

import java.util.ArrayList;

import static pinacolada.skills.PSkill.CAPITAL_CHAR;
import static pinacolada.skills.PSkill.CHAR_OFFSET;

public interface PointerProvider {
    static <T extends PointerProvider & KeywordProvider> void fillPreviewsForKeywordProvider(T provider, RotatingList<EUIPreview> list) {
        for (PSkill<?> effect : provider.getEffects()) {
            if (effect == null) {
                continue;
            }
            effect.makePreviews(list);
        }
        for (PSkill<?> effect : provider.getPowerEffects()) {
            if (effect == null) {
                continue;
            }
            effect.makePreviews(list);
        }
        for (EUIKeywordTooltip tip : provider.getTips()) {
            EUICardPreview preview = tip.createPreview();
            // Check for both the tip card and the template, if it exists
            if (preview != null) {
                TemplateCardData template = TemplateCardData.getTemplate(preview.defaultPreview.cardID);
                if (!EUIUtils.any(list, p -> p.matches(preview.defaultPreview.cardID))
                        && (template == null || !EUIUtils.any(list, p -> p.matches(template.ID)) )) {
                    list.add(preview);
                }
            }

        }

        for (EUIPreview preview : list) {
            if (preview instanceof EUICardPreview) {
                PCLCard defaultPreview = PCLCard.cast(((EUICardPreview) preview).defaultPreview);
                PCLCard upgradedPreview = PCLCard.cast(((EUICardPreview) preview).upgradedPreview);

                if (defaultPreview != null && defaultPreview.affinities != null) {
                    defaultPreview.affinities.updateSortedList();
                }
                if (upgradedPreview != null && upgradedPreview.affinities != null) {
                    upgradedPreview.affinities.updateSortedList();
                }
            }
        }
    }

    default PTrigger addPowerMove(PTrigger effect) {
        PTrigger added = (PTrigger) effect.setSource(this);
        getPowerEffects().add(added);
        return added;
    }

    default PSkill<?> addUseMove(PSkill<?> effect) {
        PSkill<?> added = effect.setSource(this);
        getEffects().add(added);
        return added;
    }

    default PSkill<?> addUseMove(PSkill<?> primary, PSkill<?>... effects) {
        PSkill<?> added = PSkill.chain(primary, effects).setSource(this);
        getEffects().add(added);
        return added;
    }

    default void clearSkills() {
        getSkills().clear();
    }

    default void doAll(ActionT1<PSkill<?>> action) {
        doEffects(action);
        doPowers(action);
    }

    default void doEffects(ActionT1<PSkill<?>> action) {
        for (PSkill<?> be : getFullEffects()) {
            action.invoke(be);
        }
    }

    default void doNonPowerEffects(ActionT1<PSkill<?>> action) {
        for (PSkill<?> be : getFullEffects()) {
            if (!(be instanceof SummonOnlyMove)) {
                action.invoke(be);
            }
        }
    }

    default void doPowers(ActionT1<PSkill<?>> action) {
        for (PSkill<?> be : getPowerEffects()) {
            action.invoke(be);
        }
    }

    default PCardPrimary_GainBlock getCardBlock() {
        return null;
    }

    default PCardPrimary_DealDamage getCardDamage() {
        return null;
    }

    default int getCounterValue() {
        return 1;
    }

    default PSkill<?> getEffect(int index) {
        return index < getEffects().size() ? getEffects().get(index) : null;
    }

    // Get a particular PSkill on the card using a pointer
    default PSkill<?> getEffectAt(Character c) {
        return getPointers().get(c - CHAR_OFFSET);
    }

    default String getEffectPowerTextStrings() {
        return EUIUtils.joinStringsMapNonnull(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE,
                ef -> ef != null && !(ef instanceof PTrait) ? StringUtils.capitalize(ef.getPowerText()) : null,
                getFullEffects());
    }

    default String getEffectStrings() {
        return EUIUtils.joinStringsMapNonnull(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE,
                ef -> ef != null && !(ef instanceof PTrait) ? StringUtils.capitalize(ef.getText()) : null,
                getFullEffects());
    }

    default ArrayList<PSkill<?>> getEffects() {
        return getSkills().onUseEffects;
    }

    // GetEffects plus any additional temporary effects not attached to Skills
    default ArrayList<PSkill<?>> getFullEffects() {
        return getEffects();
    }

    // List of every individual PSkill present, subeffect or not
    default ArrayList<PSkill<?>> getFullSubEffects() {
        ArrayList<PSkill<?>> fullList = new ArrayList<>();
        for (PSkill<?> skill : getFullEffects()) {
            PSkill<?> current = skill;
            while (current != null) {
                fullList.add(current);
                if (current instanceof PMultiBase<?>) {
                    fullList.addAll(((PMultiBase<?>) current).getSubEffects());
                }
                current = current.getChild();
            }
        }
        return fullList;
    }

    // An integer mapping to individual PSkills from anywhere in the Skills tree
    default UniqueList<PSkill<?>> getPointers() {
        return getSkills().effectTextMapping;
    }

    default PTrigger getPowerEffect(int i) {
        ArrayList<PTrigger> effects = getPowerEffects();
        return effects != null && effects.size() > i ? effects.get(i) : null;
    }

    default ArrayList<PTrigger> getPowerEffects() {
        return getSkills().powerEffects;
    }

    default AbstractCreature getSourceCreature() {
        return AbstractDungeon.player;
    }

    default int getXValue() {
        return 1;
    }

    default String makeExportString(String baseString) {
        if (baseString == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sub;
        for (int i = 0; i < baseString.length(); i++) {
            char c = baseString.charAt(i);
            switch (c) {
                case ConditionToken.TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, ConditionToken.TOKEN)) {
                        PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                        boolean capital = baseString.charAt(i + 1) == CAPITAL_CHAR;
                        if (move != null) {
                            sb.append(makeExportString(move.getCapitalSubText(PCLCardTarget.Self, capital)));
                        }
                        i += 3;
                    }
                    break;
                case PointerToken.TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, PointerToken.TOKEN)) {
                        PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                        if (move != null) {
                            String s = move.getExportString(baseString.charAt(i + 1));
                            if (!s.isEmpty()) {
                                sb.append(s);
                            }
                        }
                        i += 3;
                    }
                    break;
                case '$':
                    sub = new StringBuilder();
                    while (i + 1 < baseString.length()) {
                        i += 1;
                        c = baseString.charAt(i);
                        if (c == PointerToken.TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 3, PointerToken.TOKEN)) {
                            PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                            if (move != null) {
                                String s = move.getAttributeString(baseString.charAt(i + 1));
                                if (!s.isEmpty()) {
                                    sub.append(s);
                                }
                            }
                            i += 3;
                        }
                        else {
                            sub.append(c);
                            if (c == '$') {
                                break;
                            }
                        }
                    }
                    sb.append(EUISmartText.parseLogicString(sub.toString()));
                    break;
                case SymbolToken.TOKEN2:
                case '[':
                    sub = new StringBuilder();
                    while (i + 1 < baseString.length()) {
                        i += 1;
                        c = baseString.charAt(i);
                        if (c == ']') {
                            break;
                        }
                        else {
                            sub.append(c);
                        }
                    }
                    String key = sub.toString();
                    EUIKeywordTooltip tip = EUIKeywordTooltip.findByID(key);
                    sb.append(tip != null ? tip.title : key);
                    break;
                case '{':
                case '}':
                case ']':
                    continue;
                default:
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    default String makePowerString(String baseString) {
        if (baseString == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseString.length(); i++) {
            char c = baseString.charAt(i);
            switch (c) {
                case ConditionToken.TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, ConditionToken.TOKEN)) {
                        PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                        boolean capital = baseString.charAt(i + 1) == CAPITAL_CHAR;
                        if (move != null) {
                            sb.append(makePowerString(move.getCapitalSubText(PCLCardTarget.Self, capital)));
                        }
                        i += 3;
                    }
                    break;
                case PointerToken.TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, PointerToken.TOKEN)) {
                        PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                        if (move != null) {
                            String s = move.getAttributeString(baseString.charAt(i + 1));
                            if (!s.isEmpty()) {
                                sb.append("#b").append(s);
                            }
                        }
                        i += 3;
                    }
                    break;
                case '$':
                    StringBuilder sub = new StringBuilder();
                    while (i + 1 < baseString.length()) {
                        i += 1;
                        c = baseString.charAt(i);
                        if (c == PointerToken.TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 3, PointerToken.TOKEN)) {
                            PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                            if (move != null) {
                                String s = move.getAttributeString(baseString.charAt(i + 1));
                                if (!s.isEmpty()) {
                                    sub.append(s);
                                }
                            }
                            i += 3;
                        }
                        else {
                            sub.append(c);
                            if (c == '$') {
                                break;
                            }
                        }
                    }
                    sb.append(EUISmartText.parseLogicString(sub.toString()));
                    break;
                case SymbolToken.TOKEN2:
                case '[':
                    if (!EUIConfiguration.useEUITooltips.get()) {
                        sub = new StringBuilder();
                        while (i + 1 < baseString.length()) {
                            i += 1;
                            c = baseString.charAt(i);
                            if (c == ']') {
                                break;
                            }
                            else {
                                sub.append(c);
                            }
                        }
                        String key = sub.toString();
                        EUIKeywordTooltip tip = EUIKeywordTooltip.findByID(key);
                        // Energy tip is a special case that can be rendered as an icon in the base game
                        sb.append(tip != null ?
                                (EUI.ENERGY_ID.equals(tip.ID) ? EUI.ENERGY_TIP : tip.title)
                                : key);
                    }
                    else {
                        sb.append(c);
                    }
                    break;
                case '{':
                case '}':
                case ']':
                    if (!EUIConfiguration.useEUITooltips.get()) {
                        continue;
                    }
                default:
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    default int timesUpgraded() {
        return 0;
    }

    default PSkill<?> tryRemove(int index) {
        PSkill<?> toRemove = index < getEffects().size() ? getEffects().get(index) : null;
        if (toRemove == null || !toRemove.removable()) {
            return null;
        }
        getEffects().remove(index);
        return toRemove;
    }

    String getID();

    String getName();

    Skills getSkills();
}
