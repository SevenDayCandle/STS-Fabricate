package pinacolada.cards.base.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.PCLCardTag;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static extendedui.EUIUtils.array;
import static extendedui.EUIUtils.safeIndex;

public class PCLCardTagInfo implements Serializable, TooltipProvider {
    public final PCLCardTag tag;
    public Integer[] value;
    public Integer[] upgrades;

    public PCLCardTagInfo(PCLCardTag tag, Integer value) {
        this(tag, array(value), null);
    }

    public PCLCardTagInfo(PCLCardTag tag, Integer[] values, Integer[] upgrades) {
        this.tag = tag;
        value = values;
        this.upgrades = upgrades;
    }

    public PCLCardTagInfo(PCLCardTag tag, Integer value, Integer upgrade) {
        this(tag, array(value), array(upgrade));
    }

    public PCLCardTagInfo(PCLCardTag tag, Integer value, Integer[] upgrades) {
        this(tag, array(value), upgrades);
    }

    public Integer get(int form) {
        return safeIndex(value, form);
    }

    public List<EUIKeywordTooltip> getTips() {
        return tag.getTips();
    }

    public Integer getUpgrade(int form) {
        return EUIUtils.isNullOrEmpty(upgrades) ? null : safeIndex(upgrades, form);
    }

    public void invoke(PCLCard card) {
        invoke(card, card.getForm());
    }

    public void invoke(AbstractCard card, int form) {
        Integer value = card.upgraded ? getUpgrade(form) : get(form);
        if (value != null) {
            tag.set(card, value);
        }
    }

    public void invoke(AbstractCard card) {
        invoke(card, 0);
    }

    public void set(int form, int v) {
        if (form >= value.length) {
            value = Arrays.copyOf(value, form);
        }
        value[form] = v;
    }

    public void setUpgrade(int form, int v) {
        if (upgrades == null) {
            upgrades = new Integer[form];
        }
        else if (form >= upgrades.length) {
            value = Arrays.copyOf(upgrades, form);
        }
        upgrades[form] = v;
    }
}
