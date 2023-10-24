package pinacolada.cards.base.fields;

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
import pinacolada.cards.base.PCLCard;

import java.io.IOException;
import java.util.*;

@JsonAdapter(CardFlag.CardFlagAdapter.class)
public class CardFlag implements TooltipProvider {
    private static final Map<String, CardFlag> ALL = new HashMap<>();

    public static final CardFlag Defend = register(AbstractCard.CardTags.STARTER_DEFEND);
    public static final CardFlag Strike = register(AbstractCard.CardTags.STRIKE);

    public final String ID;
    public final AbstractCard.CardTags gameFlag;
    public final AbstractCard.CardColor[] colors;
    public String name;
    private EUITooltip tip;

    public CardFlag(String id, AbstractCard.CardColor... colors) {
        this(id, null, colors);
    }

    public CardFlag(AbstractCard.CardTags gameFlag, AbstractCard.CardColor... colors) {
        this(gameFlag.toString(), gameFlag, colors);
    }

    public CardFlag(String id, AbstractCard.CardTags gameFlag, AbstractCard.CardColor... colors) {
        this.ID = id;
        this.gameFlag = gameFlag;
        this.colors = colors;
    }

    public static CardFlag get(AbstractCard.CardTags tag) {
        return get(EUIUtils.capitalize(tag.toString()));
    }

    public static CardFlag get(String id) {
        CardFlag flag = ALL.get(id);
        if (flag == null) {
            PCLCustomFlagInfo info = PCLCustomFlagInfo.get(id);
            if (info != null) {
                flag = info.flag;
            }
        }
        return flag;
    }

    public static ArrayList<CardFlag> getAll() {
        ArrayList<CardFlag> base = new ArrayList<>(ALL.values());
        for (PCLCustomFlagInfo custom : PCLCustomFlagInfo.getFlags(null)) {
            if (custom.flag != null) {
                base.add(custom.flag);
            }
        }
        return base;
    }

    public static ArrayList<CardFlag> getAll(AbstractCard.CardColor targetColor) {
        if (targetColor == null) {
            return getAll();
        }
        ArrayList<CardFlag> base = EUIUtils.filter(ALL.values(), f -> f.colors == null || f.colors.length == 0 || EUIUtils.any(f.colors, co -> co == targetColor));
        for (PCLCustomFlagInfo custom : PCLCustomFlagInfo.getFlags(targetColor)) {
            if (custom.flag != null) {
                base.add(custom.flag);
            }
        }
        if (targetColor != AbstractCard.CardColor.COLORLESS) {
            for (PCLCustomFlagInfo custom : PCLCustomFlagInfo.getFlags(AbstractCard.CardColor.COLORLESS)) {
                if (custom.flag != null) {
                    base.add(custom.flag);
                }
            }
        }
        return base;
    }

    public static List<CardFlag> getFromCard(AbstractCard card) {
        if (card instanceof PCLCard) {
            return ((PCLCard) card).cardData.flags;
        }
        return EUIUtils.mapAsNonnull(card.tags, CardFlag::get);
    }

    public static void postInitialize() {
        Defend.name = CardCrawlGame.languagePack.getCardStrings(Defend_Red.ID).NAME;
        Strike.name = CardCrawlGame.languagePack.getCardStrings(Strike_Red.ID).NAME;
    }

    public static CardFlag register(String id, AbstractCard.CardColor... colors) {
        return registerFlag(new CardFlag(id, null, colors));
    }

    public static CardFlag register(AbstractCard.CardTags gameFlag, AbstractCard.CardColor... colors) {
        return registerFlag(new CardFlag(gameFlag, colors));
    }

    protected static CardFlag registerFlag(CardFlag flag) {
        ALL.putIfAbsent(flag.ID, flag);
        return flag;
    }

    public String getName() {
        return name != null ? name : ID;
    }

    public EUITooltip getTip() {
        if (tip == null) {
            tip = new EUITooltip(getName());
        }
        return tip;
    }

    @Override
    public List<EUITooltip> getTips() {
        return Collections.singletonList(getTip());
    }

    public boolean has(AbstractCard card) {
        if (card instanceof PCLCard) {
            List<CardFlag> flags = ((PCLCard) card).cardData.flags;
            return flags != null && EUIUtils.any(flags, f -> f == this);
        }
        return EUIUtils.any(card.tags, t -> t == this.gameFlag);
    }

    public boolean isCompatible(AbstractCard.CardColor color) {
        return colors == null || colors.length == 0 || EUIUtils.any(colors, t -> t == color);
    }

    public CardFlag setName(String name) {
        this.name = name;
        if (tip != null) {
            tip.title = name;
        }
        return this;
    }

    public static class CardFlagAdapter extends TypeAdapter<CardFlag> {
        @Override
        public CardFlag read(JsonReader in) throws IOException {
            return get(in.nextString());
        }

        @Override
        public void write(JsonWriter writer, CardFlag value) throws IOException {
            writer.value(value.ID);
        }
    }
}
