package pinacolada.cards.base.tags;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@JsonAdapter(CardTagItem.CardTagItemAdapter.class)
public class CardTagItem implements TooltipProvider {
    private static final Map<String, CardTagItem> ALL = new HashMap<>();

    public static final CardTagItem Defend = new CardTagItem(AbstractCard.CardTags.STARTER_DEFEND);
    public static final CardTagItem Strike = new CardTagItem(AbstractCard.CardTags.STRIKE);

    public final String ID;
    public final AbstractCard.CardTags tag;
    public final AbstractCard.CardColor[] colors;
    public String name;

    public CardTagItem(AbstractCard.CardTags tag, AbstractCard.CardColor... colors) {
        this(tag.toString(), tag, colors);
    }

    public CardTagItem(String id, AbstractCard.CardTags tag, AbstractCard.CardColor... colors) {
        this.ID = id;
        this.tag = tag;
        this.colors = colors;
        ALL.putIfAbsent(ID, this);
    }

    public static CardTagItem get(AbstractCard.CardTags tag) {
        return get(EUIUtils.capitalize(tag.toString()));
    }

    public static CardTagItem get(String id) {
        return ALL.get(id);
    }

    public static Collection<CardTagItem> getAll() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.getName(), b.getName())).collect(Collectors.toList());
    }

    public static Collection<CardTagItem> getAll(AbstractCard.CardColor targetColor) {
        return targetColor == null ? getAll() : EUIUtils.filter(ALL.values(), s -> s.colors == null || s.colors.length == 0 || EUIUtils.any(s.colors, t -> t == targetColor))
                .stream()
                .sorted((a, b) -> StringUtils.compare(a.getName(), b.getName())).collect(Collectors.toList());
    }

    public static List<CardTagItem> getFromCard(AbstractCard card) {
        return EUIUtils.mapAsNonnull(card.tags, CardTagItem::get);
    }

    public String getName() {
        return name != null ? name : ID;
    }

    public EUITooltip getTip() {
        return new EUITooltip(getName());
    }

    @Override
    public List<EUITooltip> getTips() {
        return Collections.singletonList(getTip());
    }

    public boolean isCompatible(AbstractCard.CardColor color) {
        return colors == null || colors.length == 0 || EUIUtils.any(colors, t -> t == color);
    }

    public static class CardTagItemAdapter extends TypeAdapter<CardTagItem> {
        @Override
        public void write(JsonWriter writer, CardTagItem value) throws IOException {
            writer.value(value.ID);
        }

        @Override
        public CardTagItem read(JsonReader in) throws IOException {
            return get(in.nextString());
        }
    }
}
