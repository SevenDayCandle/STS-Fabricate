package pinacolada.cards.base;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.options.InputSettingsScreen;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PGR;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonAdapter(PCLCardGroupHelper.PCLCardGroupHelperAdapter.class)
public class PCLCardGroupHelper implements Serializable {
    public static final Map<CardGroup.CardGroupType, PCLCardGroupHelper> ALL = new HashMap<>();

    public static final PCLCardGroupHelper DiscardPile = new PCLCardGroupHelper(CardGroup.CardGroupType.DISCARD_PILE, InputSettingsScreen.TEXT[12].toLowerCase());
    public static final PCLCardGroupHelper DrawPile = new PCLCardGroupHelper(CardGroup.CardGroupType.DRAW_PILE, InputSettingsScreen.TEXT[11].toLowerCase());
    public static final PCLCardGroupHelper ExhaustPile = new PCLCardGroupHelper(CardGroup.CardGroupType.EXHAUST_PILE, InputSettingsScreen.TEXT[13].toLowerCase());
    public static final PCLCardGroupHelper Hand = new PCLCardGroupHelper(CardGroup.CardGroupType.HAND, PGR.core.strings.cpile_hand);
    public static final PCLCardGroupHelper MasterDeck = new PCLCardGroupHelper(CardGroup.CardGroupType.MASTER_DECK, PGR.core.strings.cpile_deck);

    public final CardGroup.CardGroupType pile;
    public final String name;

    public PCLCardGroupHelper(CardGroup.CardGroupType pile, String name) {
        this.pile = pile;
        this.name = name;
        ALL.putIfAbsent(this.pile, this);
    }

    public static PCLCardGroupHelper get(String name) {
        return ALL.get(CardGroup.CardGroupType.valueOf(name));
    }

    public static PCLCardGroupHelper get(CardGroup.CardGroupType tag) {
        return ALL.get(tag);
    }

    public static List<PCLCardGroupHelper> getAll() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.name, b.name)).collect(Collectors.toList());
    }

    public static List<PCLCardGroupHelper> getStandard() {
        return Stream.of(DiscardPile, DrawPile, ExhaustPile, Hand).sorted((a, b) -> StringUtils.compare(a.name, b.name)).collect(Collectors.toList());
    }

    public final String getCapitalTitle() {
        return StringUtils.capitalize(name);
    }

    public final ArrayList<AbstractCard> getCards() {
        CardGroup g = getCardGroup();
        return g != null ? g.group : new ArrayList<>();
    }

    public final CardGroup getCardGroup() {
        if (AbstractDungeon.player != null) {
            switch (pile) {
                case HAND:
                    return AbstractDungeon.player.hand;
                case DRAW_PILE:
                    return AbstractDungeon.player.drawPile;
                case DISCARD_PILE:
                    return AbstractDungeon.player.discardPile;
                case EXHAUST_PILE:
                    return AbstractDungeon.player.exhaustPile;
                case MASTER_DECK:
                    return AbstractDungeon.player.masterDeck;
            }
        }
        return null;
    }

    public static class PCLCardGroupHelperAdapter extends TypeAdapter<PCLCardGroupHelper> {
        @Override
        public void write(JsonWriter writer, PCLCardGroupHelper value) throws IOException {
            writer.value(String.valueOf(value.pile));
        }

        @Override
        public PCLCardGroupHelper read(JsonReader in) throws IOException {
            return get(in.nextString());
        }
    }
}
