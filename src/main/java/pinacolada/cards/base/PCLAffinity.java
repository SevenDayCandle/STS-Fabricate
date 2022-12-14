package pinacolada.cards.base;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.TextureCache;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PCLAffinity implements TooltipProvider, Comparable<PCLAffinity>
{
    Red(0, "Red", "R"),
    Green(1, "Green",  "G"),
    Blue(2, "Blue",  "B"),
    Orange(3, "Orange",  "O"),
    Light(4, "Light",  "L"),
    Dark(5, "Dark",  "D"),
    Silver(6, "Silver",  "S"),
    Star(-1, "Star",  "A"),
    General(-2, "Gen",  "W"),
    Unknown(-3, "NA",  "U");

    public static final int TOTAL_AFFINITIES = 7;
    public static final int MAX_LEVEL = 2;

    private static final PCLAffinity[] BASIC_TYPES = new PCLAffinity[6];
    private static final PCLAffinity[] EXTENDED_TYPES = new PCLAffinity[TOTAL_AFFINITIES];
    private static final PCLAffinity[] ALL_TYPES = new PCLAffinity[8];

    private static final PCLAffinity[] MAIN_TYPES = new PCLAffinity[]{Red, Green, Blue, Orange};
    private static final PCLAffinity[] ETERNAL_TYPES = new PCLAffinity[]{Light, Dark, Silver};

    static
    {
        ALL_TYPES[0] = EXTENDED_TYPES[0] = BASIC_TYPES[0] = Red;
        ALL_TYPES[1] = EXTENDED_TYPES[1] = BASIC_TYPES[1] = Green;
        ALL_TYPES[2] = EXTENDED_TYPES[2] = BASIC_TYPES[2] = Blue;
        ALL_TYPES[3] = EXTENDED_TYPES[3] = BASIC_TYPES[3] = Orange;
        ALL_TYPES[4] = EXTENDED_TYPES[4] = BASIC_TYPES[4] = Light;
        ALL_TYPES[5] = EXTENDED_TYPES[5] = BASIC_TYPES[5] = Dark;
        ALL_TYPES[6] = EXTENDED_TYPES[6] = Silver;
        ALL_TYPES[7] = Star;
    }

    public final int id;
    public final String name;
    public final String symbol;

    PCLAffinity(int id, String name, String powerSymbol)
    {
        this.id = id;
        this.name = name;
        this.symbol = powerSymbol;
    }

    public static PCLAffinity[] all()
    {
        return ALL_TYPES;
    }

    public static PCLAffinity[] basic()
    {
        return BASIC_TYPES;
    }

    public static PCLAffinity[] extended()
    {
        return EXTENDED_TYPES;
    }

    public static PCLAffinity fromTooltip(EUITooltip tooltip) {   //@Formatter: Off
        if (tooltip.is(PGR.core.tooltips.affinityRed)) {
            return PCLAffinity.Red;
        }
        if (tooltip.is(PGR.core.tooltips.affinityGreen)) {
            return PCLAffinity.Green;
        }
        if (tooltip.is(PGR.core.tooltips.affinityBlue)) {
            return PCLAffinity.Blue;
        }
        if (tooltip.is(PGR.core.tooltips.affinityOrange)) {
            return PCLAffinity.Orange;
        }
        if (tooltip.is(PGR.core.tooltips.affinityLight)) {
            return PCLAffinity.Light;
        }
        if (tooltip.is(PGR.core.tooltips.affinityDark)) {
            return PCLAffinity.Dark;
        }
        if (tooltip.is(PGR.core.tooltips.affinitySilver)) {
            return PCLAffinity.Silver;
        }
        if (tooltip.is(PGR.core.tooltips.multicolor)) {
            return PCLAffinity.Star;
        }
        if (tooltip.is(PGR.core.tooltips.affinityGeneral)) {
            return PCLAffinity.General;
        }
        if (tooltip.is(PGR.core.tooltips.affinityUnknown)) {
            return PCLAffinity.Unknown;
        }
        return null;
    }   //@Formatter: On

    public static PCLAffinity[] getAvailableAffinities(AbstractCard.CardColor pc)
    {
        if (pc == AbstractCard.CardColor.COLORLESS || pc == AbstractCard.CardColor.CURSE)
        {
            return extended();
        }
        if (pc == PGR.Enums.Cards.THE_CONJURER || pc == PGR.Enums.Cards.THE_DECIDER)
        {
            return MAIN_TYPES;
        }
        if (pc == PGR.Enums.Cards.THE_ETERNAL)
        {
            return ETERNAL_TYPES;
        }
        return new PCLAffinity[]{};
    }

    public static PCLAffinity[] getAvailableAffinities()
    {
        return getAvailableAffinities(GameUtilities.getActingColor());
    }

    public static List<PCLAffinity> getAvailableAffinitiesAsList(AbstractCard.CardColor pc)
    {
        return Arrays.asList(getAvailableAffinities(pc));
    }

    public static List<PCLAffinity> getAvailableAffinitiesAsList()
    {
        return Arrays.asList(getAvailableAffinities());
    }

    public String getAffinitySymbol()
    {
        return EUIUtils.format("A-{0}", symbol);
    }

    public Color getAlternateColor(float lerp)
    {
        return Color.WHITE.cpy().lerp(getAlternateColor(), lerp);
    }

    public Color getAlternateColor()
    {
        switch (this)
        {
            case Red:
                return new Color(0.8f, 0.5f, 0.5f, 1f);

            case Green:
                return new Color(0.45f, 0.7f, 0.55f, 1f);

            case Blue:
                return new Color(0.45f, 0.55f, 0.7f, 1f);

            case Orange:
                return new Color(0.7f, 0.6f, 0.5f, 1f);

            case Light:
                return new Color(0.8f, 0.8f, 0.3f, 1f);

            case Dark:
                return new Color(0.55f, 0.1f, 0.85f, 1);//0.7f, 0.55f, 0.7f, 1f);

            case Silver:
                return new Color(0.5f, 0.5f, 0.5f, 1f);

            case Star:
            default:
                return new Color(0.95f, 0.95f, 0.95f, 1f);
        }
    }

    public Texture getBackground(int level)
    {
        return (level ) > 1 ? PGR.core.images.core.borderBG.texture() : null;
    }

    public Texture getBorder(int level)
    {
        AbstractCard.CardColor color = GameUtilities.getActingColor();
        if (color == PGR.Enums.Cards.THE_CONJURER)
        {
            return PGR.core.images.core.borderSpecial2.texture();
        }
        return (level > 1 ? PGR.core.images.core.borderWeak : PGR.core.images.core.borderNormal).texture();
    }

    public TextureCache getDefaultIcon()
    {
        switch (this)
        {
            case Red:
                return PGR.core.images.affinities.red;
            case Green:
                return PGR.core.images.affinities.green;
            case Blue:
                return PGR.core.images.affinities.blue;
            case Orange:
                return PGR.core.images.affinities.orange;
            case Light:
                return PGR.core.images.affinities.light;
            case Dark:
                return PGR.core.images.affinities.dark;
            case Silver:
                return PGR.core.images.affinities.silver;
            case Star:
                return PGR.core.images.star;
            case General:
                return PGR.core.images.general;
        }
        return PGR.core.images.unknown;
    }

    public Texture getForeground(int level)
    {
        return /*this == Star ? null : */(level > 1 ? PGR.core.images.core.borderFG.texture() : null);
    }

    public String getFormattedAffinitySymbol()
    {
        return EUIUtils.format("[{0}]", getAffinitySymbol());
    }

    public String getFormattedPowerSymbol()
    {
        return EUIUtils.format("[{0}]", getPowerSymbol());
    }

    public String getFormattedSymbol(AbstractCard.CardColor pc)
    {
        return getTooltip().getTitleOrIcon();
    }

    public Texture getIcon()
    {
        return getTextureCache().texture();
    }

    public EUITooltip getLevelTooltip()
    {
        return getLevelTooltip(GameUtilities.getActingColor());
    }

    public EUITooltip getLevelTooltip(AbstractCard.CardColor pc)
    {
        PCLResources resources = PGR.getResources(pc);
        if (pc != null)
        {
            return resources.tooltips.getLevelTooltip(this);
        }
        return PGR.core.tooltips.level;
    }

    public TextureRegion getPowerIcon()
    {
        return getLevelTooltip().icon;
    }

    public String getPowerSymbol()
    {
        return EUIUtils.format("P-{0}", symbol);
    }

    public TextureCache getTextureCache()
    {
        AbstractCard.CardColor color = GameUtilities.getActingColor();
        PCLResources resources = PGR.getResources(color);
        switch (this)
        {
            case Red:
                return resources.images.affinities.red;
            case Green:
                return resources.images.affinities.green;
            case Blue:
                return resources.images.affinities.blue;
            case Orange:
                return resources.images.affinities.orange;
            case Light:
                return resources.images.affinities.light;
            case Dark:
                return resources.images.affinities.dark;
            case Silver:
                return resources.images.affinities.silver;
        }
        return getDefaultIcon();
    }

    public TextureRegion getTextureRegion()
    {
        return EUIRenderHelpers.getCroppedRegion(getIcon(), 10);
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return Collections.singletonList(getTooltip());
    }

    public EUITooltip getTooltip()
    {
        switch (this)
        {
            case Red:
                return PGR.core.tooltips.affinityRed;
            case Green:
                return PGR.core.tooltips.affinityGreen;
            case Blue:
                return PGR.core.tooltips.affinityBlue;
            case Orange:
                return PGR.core.tooltips.affinityOrange;
            case Light:
                return PGR.core.tooltips.affinityLight;
            case Dark:
                return PGR.core.tooltips.affinityDark;
            case Silver:
                return PGR.core.tooltips.affinitySilver;
            case Star:
                return PGR.core.tooltips.multicolor;
            case General:
                return PGR.core.tooltips.affinityGeneral;
            case Unknown:
                return PGR.core.tooltips.affinityUnknown;
            default:
                throw new EnumConstantNotPresentException(PCLAffinity.class, this.name());
        }
    }

    public int getPolarity()
    {
        switch (this)
        {
            case Light:
                return 1;
            case Dark:
                return -1;
        }
        return 0;
    }
}