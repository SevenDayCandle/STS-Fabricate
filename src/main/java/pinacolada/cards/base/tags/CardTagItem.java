package pinacolada.cards.base.tags;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CardTagItem implements TooltipProvider
{
    Defend(AbstractCard.CardTags.STARTER_DEFEND),
    Strike(AbstractCard.CardTags.STRIKE);

    public final AbstractCard.CardTags tag;
    public final AbstractCard.CardColor[] colors;

    CardTagItem(AbstractCard.CardTags tag, AbstractCard.CardColor... colors)
    {
        this.tag = tag;
        this.colors = colors;
    }

    public static CardTagItem get(String name)
    {
        return CardTagItem.valueOf(name);
    }

    public static CardTagItem get(AbstractCard.CardTags tag)
    {
        return get(EUIUtils.capitalize(tag.toString()));
    }

    public static List<CardTagItem> getAll()
    {
        CardTagItem[] values = CardTagItem.values();
        Arrays.sort(values, (a, b) -> StringUtils.compare(a.getTip().title, b.getTip().title));
        return Arrays.asList(values);
    }

    public static List<CardTagItem> getCompatible(AbstractCard.CardColor co)
    {
        List<CardTagItem> values = EUIUtils.filter(CardTagItem.values(), v -> v.isCompatible(co));
        values.sort((a, b) -> StringUtils.compare(a.getTip().title, b.getTip().title));
        return values;
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return Collections.singletonList(getTip());
    }

    public EUITooltip getTip()
    {
        switch (this)
        {
            case Defend:
                return new EUITooltip(CardCrawlGame.languagePack.getCardStrings(Defend_Red.ID).NAME);
            case Strike:
                return new EUITooltip(CardCrawlGame.languagePack.getCardStrings(Strike_Red.ID).NAME);
        }
        return new EUITooltip(this.name());
    }

    public boolean has(AbstractCard card)
    {
        return card.hasTag(tag);
    }

    public boolean isCompatible(AbstractCard.CardColor color)
    {
        return colors == null || colors.length == 0 || EUIUtils.any(colors, t -> t == color);
    }

    public void set(AbstractCard card, boolean value)
    {
        GameUtilities.setCardTag(card, tag, value);
    }
}
