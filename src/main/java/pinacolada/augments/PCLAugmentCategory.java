package pinacolada.augments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import extendedui.interfaces.markers.CountingPanelItem;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;

public enum PCLAugmentCategory implements CountingPanelItem
{
    General(Color.WHITE),
    Summon(Color.FIREBRICK),
    Played(Color.VIOLET),
    Power(Color.BLUE),
    Special(Color.LIME);

    public final Color color;

    PCLAugmentCategory(Color color)
    {
        this.color = color;
    }

    @Override
    public Texture getIcon()
    {
        return PCLCoreImages.CardUI.augment.texture();
    }

    @Override
    public Color getColor()
    {
        return color;
    }

    public boolean isTypeValid(AbstractCard.CardType type)
    {
        switch (this)
        {
            case Summon:
                return type == PCLEnum.CardType.SUMMON;
            case Played:
                switch (type)
                {
                    case ATTACK:
                    case SKILL:
                    case STATUS:
                    case CURSE:
                        return true;
                    default:
                        return false;
                }
            case Power:
                return type == AbstractCard.CardType.POWER;
        }
        return true;
    }

    public String getName()
    {
        switch (this)
        {
            case Summon:
                return PGR.core.tooltips.summon.title;
            case Played:
                return PGR.core.tooltips.play.title;
            case Power:
                return PGR.core.tooltips.power.title;
            case General:
                return PGR.core.strings.ctype_general;
            case Special:
                return RunHistoryScreen.TEXT[15];
        }
        return AbstractCard.TEXT[5];
    }
}
