package pinacolada.cards.base.tags;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@JsonAdapter(CardFlag.CardFlagAdapter.class)
public class CardFlag implements TooltipProvider {
    private static final Map<String, CardFlag> ALL = new HashMap<>();

    public static final CardFlag Defend = new CardFlag(AbstractCard.CardTags.STARTER_DEFEND);
    public static final CardFlag Strike = new CardFlag(AbstractCard.CardTags.STRIKE);

    public final String ID;
    public final AbstractCard.CardTags gameFlag;
    public final AbstractCard.CardColor[] colors;
    public String name;

    public CardFlag(AbstractCard.CardTags gameFlag, AbstractCard.CardColor... colors) {
        this(gameFlag.toString(), gameFlag, colors);
    }

    public CardFlag(String id, AbstractCard.CardTags gameFlag, AbstractCard.CardColor... colors) {
        this.ID = id;
        this.gameFlag = gameFlag;
        this.colors = colors;
        ALL.putIfAbsent(ID, this);
    }

    public static CardFlag get(AbstractCard.CardTags tag) {
        return get(EUIUtils.capitalize(tag.toString()));
    }

    public static CardFlag get(String id) {
        return ALL.get(id);
    }

    public static Collection<CardFlag> getAll() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.getName(), b.getName())).collect(Collectors.toList());
    }

    public static Collection<CardFlag> getAll(AbstractCard.CardColor targetColor) {
        return targetColor == null ? getAll() : EUIUtils.filter(ALL.values(), s -> s.colors == null || s.colors.length == 0 || EUIUtils.any(s.colors, t -> t == targetColor))
                .stream()
                .sorted((a, b) -> StringUtils.compare(a.getName(), b.getName())).collect(Collectors.toList());
    }

    public static List<CardFlag> getFromCard(AbstractCard card) {
        return EUIUtils.mapAsNonnull(card.tags, CardFlag::get);
    }

    public static void postInitialize() {
        Defend.name = CardCrawlGame.languagePack.getCardStrings(Defend_Red.ID).NAME;
        Strike.name = CardCrawlGame.languagePack.getCardStrings(Strike_Red.ID).NAME;
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

    public static class CardFlagAdapter extends TypeAdapter<CardFlag> {
        @Override
        public void write(JsonWriter writer, CardFlag value) throws IOException {
            writer.value(value.ID);
        }

        @Override
        public CardFlag read(JsonReader in) throws IOException {
            return get(in.nextString());
        }
    }
}
