package pinacolada.cards.base.fields;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static pinacolada.cards.base.fields.PCLAffinity.General;


public class PCLCardAffinities {
    protected PCLCardAffinity[] list = new PCLCardAffinity[PCLAffinity.getCount()];
    public PCLCardAffinity star = null;
    public boolean collapseDuplicates = false;
    public final ArrayList<PCLCardAffinity> sorted = new ArrayList<>();
    public transient AbstractCard card;

    public PCLCardAffinities(AbstractCard card) {
        this.card = card;
        this.updateSortedList();
    }

    public PCLCardAffinities(AbstractCard card, PCLCardAffinities affinities) {
        this.card = card;
        initialize(affinities);
    }

    public PCLCardAffinities(PCLCard card, PCLCardDataAffinityGroup affinities) {
        this.card = card;
        initialize(affinities, card.getForm());
    }

    public PCLCardAffinities add(PCLCardAffinities other, int levelLimit) {
        if (other != null) {
            int star = Math.min(levelLimit, other.getLevel(PCLAffinity.Star));
            if (star > 0) {
                addStar(star);
            }
            else {
                for (PCLCardAffinity item : other.list) {
                    if (item != null) {
                        add(item.type, Math.min(levelLimit, item.level));
                    }
                }
            }
        }

        this.updateSortedList();
        return this;
    }

    public PCLCardAffinities add(PCLCardAffinities other) {
        if (other.star != null) {
            addStar(other.star.level);
        }
        for (PCLCardAffinity item : other.list) {
            if (item != null) {
                add(item.type, item.level);
            }
        }

        this.updateSortedList();
        return this;
    }

    public PCLCardAffinity add(PCLAffinity affinity, int level) {
        if (affinity == PCLAffinity.Star) {
            return addStar(level);
        }

        PCLCardAffinity a = list[affinity.ID];
        if (a != null) {
            a.level = Math.max(0, a.level + level);
            this.updateSortedList();
            return a;
        }

        a = new PCLCardAffinity(affinity, Math.max(0, level));
        list[affinity.ID] = a;

        this.updateSortedList();
        return a;
    }

    public PCLCardAffinity addStar(int level) {
        return setStar((star == null ? 0 : star.level) + level);
    }

    public void applyUpgrades(PCLCardDataAffinityGroup affinities, int form) {
        if (star != null) {
            star.level += affinities.getUpgrade(PCLAffinity.Star, form);
        }

        for (PCLCardAffinity a : list) {
            if (a != null) {
                a.level += affinities.getUpgrade(a.type, form);
            }
        }

        this.updateSortedList();
    }

    public void clear() {
        list = new PCLCardAffinity[PCLAffinity.getCount()];
        star = null;
        this.updateSortedList();
    }

    public void clearLevelsOnly() {
        for (PCLCardAffinity result : list) {
            if (result != null) {
                result.level = 0;
            }
        }
        if (star != null) {
            star.level = 0;
        }

        this.updateSortedList();
    }

    public void displayUpgrades(ArrayList<PCLCardAffinity> prev) {
        if (prev == null) {
            return;
        }

        int i = 0;
        int j = 0;

        while (i < prev.size() && j < sorted.size()) {
            if (sorted.get(j).type != prev.get(i).type) {
                sorted.get(j).renderColor = Settings.GREEN_RELIC_COLOR.cpy();
                j++;
            }
            else {
                i++;
                j++;
            }
        }

        while (j < sorted.size()) {
            sorted.get(j).renderColor = Settings.GREEN_RELIC_COLOR.cpy();
            j++;
        }
    }

    public PCLCardAffinity get(PCLAffinity affinity) {
        return get(affinity, false);
    }

    public PCLCardAffinity get(PCLAffinity affinity, boolean createIfNull) {
        if (affinity == null || affinity == PCLAffinity.General || affinity == PCLAffinity.Unknown) {
            return getHighest();
        }

        if (affinity == PCLAffinity.Star) {
            return (createIfNull && star == null) ? setStar(0) : star;
        }

        if (affinity.ID < 0 || affinity.ID >= PCLAffinity.getCount()) {
            return null;
        }

        PCLCardAffinity a = list[affinity.ID];
        return a == null && createIfNull ? set(affinity, 0) : a;
    }

    public ArrayList<PCLAffinity> getAffinities() {
        return getAffinities(true, false);
    }

    public ArrayList<PCLAffinity> getAffinities(boolean convertStar, boolean availableOnly) {
        HashSet<PCLAffinity> available = new HashSet<>(Arrays.asList(availableOnly ? PCLAffinity.getAvailableAffinities() : PCLAffinity.basic()));
        final ArrayList<PCLAffinity> list = new ArrayList<>();
        if (convertStar && hasStar()) {
            list.addAll(available);
        }
        else {
            for (PCLCardAffinity item : this.list) {
                if (item != null && item.level > 0 && available.contains(item.type)) {
                    list.add(item.type);
                }
            }
            if (hasStar()) {
                list.add(star.type);
            }
        }
        return list;
    }

    protected PCLAffinity[] getAvailableAffinities() {
        return PCLAffinity.getAvailableAffinities(GameUtilities.getActingColor(), !(card instanceof PCLDynamicCard));
    }

    public ArrayList<PCLCardAffinity> getCardAffinities() {
        return getCardAffinities(true);
    }

    public ArrayList<PCLCardAffinity> getCardAffinities(boolean filterLevelZero) {
        final ArrayList<PCLCardAffinity> list = new ArrayList<>();
        for (PCLCardAffinity item : this.list) {
            if (item != null && (!filterLevelZero || item.level > 0)) {
                list.add(item);
            }
        }
        if (star != null && (!filterLevelZero || star.level > 0)) {
            list.add(star);
        }
        return list;
    }

    public int getDirectLevel(PCLAffinity affinity) {
        final PCLCardAffinity a = getDirectly(affinity);
        return (a != null) ? a.level : 0;
    }

    public PCLCardAffinity getDirectly(PCLAffinity affinity) {
        if (affinity == PCLAffinity.Star) {
            return star;
        }
        else if (affinity.ID < 0 || affinity.ID >= PCLAffinity.getCount()) {
            return null;
        }

        return list[affinity.ID];
    }

    public PCLCardAffinity getHighest() {
        final int star = this.star != null ? this.star.level : 0;
        final PCLCardAffinity a = EUIUtils.max(list, af -> af);
        return a != null && a.level >= star ? a : this.star;
    }

    public PCLCardAffinity getHighest(FuncT1<Boolean, PCLCardAffinity> filter) {
        final int star = this.star != null ? this.star.level : 0;
        final PCLCardAffinity a = EUIUtils.max(EUIUtils.filter(list, filter), af -> af);
        return a != null && a.level >= star ? a : this.star;
    }

    public int getLevel(PCLAffinity affinity) {
        return getLevel(affinity, true);
    }

    public int getLevel(PCLAffinity affinity, boolean useStarLevel) {
        int star = (this.star != null ? this.star.level : 0);
        if (affinity == PCLAffinity.Star || (useStarLevel && star > 0)) {
            return star;
        }
        else if (affinity == PCLAffinity.General) {
            final PCLCardAffinity a = EUIUtils.findMax(list, af -> af.level);
            return a == null ? (useStarLevel ? star : 0) : a.level; // Highest level among all affinities
        }
        else {
            final PCLCardAffinity a = get(affinity);
            return (a != null) ? a.level : 0;
        }
    }

    public boolean hasSameAffinities(PCLCardAffinities other) {
        if (other == null) {
            return false;
        }
        if (this.getLevel(PCLAffinity.Star) != other.getLevel(PCLAffinity.Star)) {
            return false;
        }
        for (PCLAffinity af : PCLAffinity.basic()) {
            if (this.getLevel(af) != other.getLevel(af)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasStar() {
        return star != null && star.level > 0;
    }

    public PCLCardAffinities initialize(PCLCardAffinities affinities) {
        if (affinities.star != null) {
            star = new PCLCardAffinity(PCLAffinity.Star, affinities.star.level);
        }
        else {
            star = null;
        }

        list = new PCLCardAffinity[PCLAffinity.getCount()];
        for (PCLCardAffinity a : affinities.list) {
            if (a != null) {
                PCLCardAffinity t = new PCLCardAffinity(a.type, a.level);
                list[t.type.ID] = t;
            }
        }

        this.updateSortedList();
        return this;
    }

    public PCLCardAffinities initialize(PCLCardDataAffinityGroup affinities, int form) {
        if (affinities.star != null) {
            star = new PCLCardAffinity(PCLAffinity.Star, affinities.star.get(form));
        }
        else {
            star = null;
        }

        list = new PCLCardAffinity[PCLAffinity.getCount()];
        for (PCLCardDataAffinity a : affinities.list) {
            if (a != null) {
                PCLCardAffinity t = new PCLCardAffinity(a.type, a.get(form));
                list[t.type.ID] = t;
            }
        }

        this.updateSortedList();
        return this;
    }

    public void render(SpriteBatch sb, AbstractCard card, float x, float y, float size, float step) {
        int max = sorted.size();
        final int half = max / 2;
        if (half >= 2) {
            step *= 0.75f;
        }

        for (int i = 0; i < max; i++) {
            final PCLCardAffinity item = sorted.get(i);

            if (max % 2 == 1) {
                x = (step * (i - half));
            }
            else {
                x = (step * 0.5f) + (step * (i - half));
            }

            item.renderOnCard(sb, card, x, y, size, collapseDuplicates);
        }
    }

    public void renderOnCard(SpriteBatch sb, AbstractCard card, boolean highlight) {
        float size;
        float step;
        float y = AbstractCard.RAW_H;

        if (highlight) {
            size = 64;
            y *= 0.58f;
            step = size * 0.9f;
        }
        else {
            size = 48;//48;
            y *= 0.49f;// -0.51f;
            step = size * 1.2f;
        }

        render(sb, card, 0, y, size, step);

    }

    public PCLCardAffinity set(PCLAffinity affinity, int level) {
        if (level < 0) {
            level = 0;
        }
        if (affinity == PCLAffinity.Star) {
            setStar(level);
            return star;
        }

        if (affinity.ID >= list.length) {
            list = Arrays.copyOf(list, affinity.ID + 1);
        }

        PCLCardAffinity result = list[affinity.ID];
        if (result != null) {
            result.level = level;
            this.updateSortedList();
            return result;
        }

        result = new PCLCardAffinity(affinity, level);
        list[affinity.ID] = result;

        this.updateSortedList();
        return result;
    }

    public PCLCardAffinity setStar(int level) {
        if (star != null) {
            star.level = level;
        }
        else {
            star = new PCLCardAffinity(PCLAffinity.Star, level);
        }

        this.updateSortedList();
        return star;
    }

    protected boolean shouldHideAffinity(PCLAffinity a) {
        return (!PGR.config.showIrrelevantProperties.get() && card != null && !EUIUtils.any(getAvailableAffinities(), b -> b == a));
    }

    public void updateSortedList() {
        sorted.clear();
        for (PCLCardAffinity a : list) {
            if (a == null || a.level <= 0 || shouldHideAffinity(a.type)) {
                continue;
            }

            if (collapseDuplicates) {
                sorted.add(a);
            }
            else {
                for (int i = 0; i < a.level; i++) {
                    sorted.add(a);
                }
            }
        }
        if (star != null && star.level > 0) {
            if (collapseDuplicates) {
                sorted.add(star);
            }
            else {
                for (int i = 0; i < star.level; i++) {
                    sorted.add(star);
                }
            }
        }

        if (sorted.isEmpty() && (PGR.config.showIrrelevantProperties.get() || getAvailableAffinities().length > 0)) {
            sorted.add(new PCLCardAffinity(General, 1));
        }

        sorted.sort(PCLCardAffinity::compareTo);
    }
}