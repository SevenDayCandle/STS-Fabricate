package pinacolada.augments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CountingPanelItem;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public enum PCLAugmentCategory implements CountingPanelItem {
    General(Color.WHITE),
    Summon(Color.FIREBRICK),
    Played(Color.VIOLET),
    Power(Color.BLUE),
    Special(Color.LIME);

    public final Color color;

    PCLAugmentCategory(Color color) {
        this.color = color;
    }

    public String getName() {
        switch (this) {
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

    @Override
    public int getRank(AbstractCard c) {
        ArrayList<PCLAugment> augments = GameUtilities.getAugments(c);
        return augments != null ? EUIUtils.count(augments, a -> a.data.category == this) : 0;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Texture getIcon() {
        return PCLCoreImages.CardUI.augment.texture();
    }

    public boolean isTypeValid(AbstractCard.CardType type) {
        switch (this) {
            case Summon:
                return type == PCLEnum.CardType.SUMMON;
            case Played:
                switch (type) {
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
}
