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
    General,
    Summon,
    Played,
    Power,
    Special;

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public int getRank(AbstractCard c) {
        ArrayList<PCLAugment> augments = GameUtilities.getAugments(c);
        return augments != null ? EUIUtils.count(augments, a -> a.data.category == this) : 0;
    }

    @Override
    public Texture getIcon() {
        switch (this) {
            case Summon:
                return PCLCoreImages.CardUI.augmentSummon.texture();
            case Played:
                return PCLCoreImages.CardUI.augmentPlayed.texture();
            case Power:
                return PCLCoreImages.CardUI.augmentPower.texture();
            case Special:
                return PCLCoreImages.CardUI.augmentSpecial.texture();
        }
        return PCLCoreImages.CardUI.augmentBase.texture();
    }

    public String getName() {
        switch (this) {
            case Summon:
                return PGR.core.tooltips.summon.title;
            case Played:
                return PGR.core.tooltips.attack.title + "/" + PGR.core.tooltips.skill.title;
            case Power:
                return PGR.core.tooltips.power.title;
            case General:
                return PGR.core.strings.ctype_general;
            case Special:
                return RunHistoryScreen.TEXT[15];
        }
        return AbstractCard.TEXT[5];
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
