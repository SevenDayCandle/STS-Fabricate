package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUIKeywordTooltip;
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

import static pinacolada.cards.base.cardText.ConditionToken.CONDITION_TOKEN;
import static pinacolada.cards.base.cardText.PointerToken.BOUND_TOKEN;
import static pinacolada.skills.PSkill.CAPITAL_CHAR;
import static pinacolada.skills.PSkill.CHAR_OFFSET;

public interface PointerProvider {
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

    default PCardPrimary_GainBlock getCardBlock() {
        return null;
    }

    default PCardPrimary_DealDamage getCardDamage() {
        return null;
    }

    default PSkill<?> getEffect(int index) {
        return index < getEffects().size() ? getEffects().get(index) : null;
    }

    // Get a particular PSkill on the card using a pointer
    default PSkill<?> getEffectAt(Character c) {
        return getPointers().get(c - CHAR_OFFSET);
    }

    default String getEffectPowerTextStrings() {
        ArrayList<PSkill<?>> tempEffects = EUIUtils.filter(getFullEffects(), ef -> ef != null && !(ef instanceof PTrait));
        return EUIUtils.joinStrings(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE, EUIUtils.mapAsNonnull(tempEffects, PSkill::getPowerText));
    }

    default String getEffectStrings() {
        ArrayList<PSkill<?>> tempEffects = EUIUtils.filter(getFullEffects(), ef -> ef != null && !(ef instanceof PTrait));
        return EUIUtils.joinStrings(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE, EUIUtils.mapAsNonnull(tempEffects, PSkill::getText));
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

    default String makeExportString(String baseString) {
        if (baseString == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sub;
        for (int i = 0; i < baseString.length(); i++) {
            char c = baseString.charAt(i);
            switch (c) {
                case CONDITION_TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, CONDITION_TOKEN)) {
                        PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                        boolean capital = baseString.charAt(i + 1) == CAPITAL_CHAR;
                        if (move != null) {
                            sb.append(makeExportString(move.getCapitalSubText(capital)));
                        }
                        i += 3;
                    }
                    break;
                case BOUND_TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, BOUND_TOKEN)) {
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
                        if (c == BOUND_TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 3, BOUND_TOKEN)) {
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
                    EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(key);
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
                case CONDITION_TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, CONDITION_TOKEN)) {
                        PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                        boolean capital = baseString.charAt(i + 1) == CAPITAL_CHAR;
                        if (move != null) {
                            sb.append(makePowerString(move.getCapitalSubText(capital)));
                        }
                        i += 3;
                    }
                    break;
                case BOUND_TOKEN:
                    if (EUIRenderHelpers.isCharAt(baseString, i + 3, BOUND_TOKEN)) {
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
                        if (c == BOUND_TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 3, BOUND_TOKEN)) {
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
                case '[':
                    if (!EUIConfiguration.useEUITooltips.get())
                    {
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
                        EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(key);
                        sb.append(tip != null ? tip.title : key);
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

    default int xValue() {
        return 1;
    }

    String getID();

    String getName();

    Skills getSkills();
}
