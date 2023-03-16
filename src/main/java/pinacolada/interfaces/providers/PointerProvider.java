package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.text.EUISmartText;
import pinacolada.interfaces.markers.PMultiBase;
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
import static pinacolada.skills.PSkill.CHAR_OFFSET;

public interface PointerProvider
{
    Skills getSkills();
    String getID();
    String getName();

    default PSkill<?> addUseMove(PSkill<?> effect)
    {
        PSkill<?> added = effect.setSource(this, PSkill.PCLCardValueSource.XValue);
        getEffects().add(added);
        return added;
    }

    default ArrayList<PSkill<?>> getEffects()
    {
        return getSkills().onUseEffects;
    }

    default PSkill<?> addUseMove(PSkill<?> primary, PSkill<?>... effects)
    {
        PSkill<?> added = PSkill.chain(primary, effects).setSource(this, PSkill.PCLCardValueSource.XValue);
        getEffects().add(added);
        return added;
    }

    default void clearSkills()
    {
        getSkills().clear();
    }

    default PSkill<?> getEffect(int index)
    {
        return index < getEffects().size() ? getEffects().get(index) : null;
    }

    default String getEffectStrings()
    {
        ArrayList<PSkill<?>> tempEffects = EUIUtils.filter(getFullEffects(), ef -> ef != null && !(ef instanceof PTrait));
        return EUIUtils.joinStrings(EUIUtils.DOUBLE_SPLIT_LINE, EUIUtils.mapAsNonnull(tempEffects, PSkill::getText));
    }

    // GetEffects plus any additional temporary effects not attached to Skills
    default ArrayList<PSkill<?>> getFullEffects()
    {
        return getEffects();
    }

    default PCardPrimary_DealDamage getCardDamage()
    {
        return null;
    }

    default PCardPrimary_GainBlock getCardBlock()
    {
        return null;
    }

    // An integer mapping to individual PSkills from anywhere in the Skills tree
    default UniqueList<PSkill<?>> getPointers()
    {
        return getSkills().effectTextMapping;
    }

    default ArrayList<PTrigger> getPowerEffects()
    {
        return getSkills().powerEffects;
    }

    default AbstractCreature getSourceCreature()
    {
        return AbstractDungeon.player;
    }

    // List of every individual PSkill present, subeffect or not
    default ArrayList<PSkill<?>> getFullSubEffects()
    {
        ArrayList<PSkill<?>> fullList = new ArrayList<>();
        for (PSkill<?> skill : getFullEffects())
        {
            PSkill<?> current = skill;
            while (current != null)
            {
                fullList.add(current);
                if (current instanceof PMultiBase<?>)
                {
                    fullList.addAll(((PMultiBase<?>) current).getSubEffects());
                }
                current = current.getChild();
            }
        }
        return fullList;
    }

    // Get a particular PSkill on the card using a pointer
    default PSkill<?> getEffectAt(Character c)
    {
        return getPointers().get(c - CHAR_OFFSET);
    }

    default String makeExportString(String baseString)
    {
        if (baseString == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseString.length(); i++)
        {
            char c = baseString.charAt(i);
            if (c == CONDITION_TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 2, CONDITION_TOKEN))
            {
                PSkill<?> move = getEffectAt(baseString.charAt(i + 1));
                if (move != null)
                {
                    sb.append(makeExportString(move.getSubText()));
                }
                i += 2;
            }
            else if (c == BOUND_TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 3, BOUND_TOKEN))
            {
                PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                if (move != null)
                {
                    String s = move.getExportString(baseString.charAt(i + 1));
                    if (!s.isEmpty())
                    {
                        sb.append(s);
                    }
                }
                i += 3;
            }
            else if (c == '$')
            {
                StringBuilder sub = new StringBuilder();
                while (i + 1 < baseString.length())
                {
                    i += 1;
                    c = baseString.charAt(i);
                    sub.append(c);
                    if (c == '$')
                    {
                        break;
                    }
                }
                sb.append(EUISmartText.parseLogicString(sub.toString()));
            }
            else if (!(c == '{' || c == '}' || c == '[' || c == ']'))
            {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    default String makePowerString(String baseString)
    {
        if (baseString == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseString.length(); i++)
        {
            char c = baseString.charAt(i);
            if (c == CONDITION_TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 2, CONDITION_TOKEN))
            {
                PSkill<?> move = getEffectAt(baseString.charAt(i + 1));
                if (move != null)
                {
                    sb.append(makePowerString(move.getSubText()));
                }
                i += 2;
            }
            else if (c == BOUND_TOKEN && EUIRenderHelpers.isCharAt(baseString, i + 3, BOUND_TOKEN))
            {
                PSkill<?> move = getEffectAt(baseString.charAt(i + 2));
                if (move != null)
                {
                    String s = move.getAttributeString(baseString.charAt(i + 1));
                    if (!s.isEmpty())
                    {
                        sb.append("#b").append(s);
                    }
                }
                i += 3;
            }
            else
            {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    default PSkill<?> tryRemove(int index)
    {
        PSkill<?> toRemove = index < getEffects().size() ? getEffects().get(index) : null;
        if (toRemove == null || !toRemove.removable())
        {
            return null;
        }
        getEffects().remove(index);
        return toRemove;
    }

    default int xValue()
    {
        return 1;
    }
}