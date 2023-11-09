package pinacolada.augments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum PCLAugmentCategory implements CountingPanelItem, TooltipProvider {
    General,
    Summon,
    Played,
    Hindrance,
    Special;

    private EUITooltip tip;

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    public String getDesc() {
        switch (this) {
            case Summon:
                return PGR.core.strings.augment_summonDesc;
            case Played:
                return PGR.core.strings.augment_playedDesc;
            case Hindrance:
                return PGR.core.strings.augment_hindranceDesc;
            case General:
                return PGR.core.strings.augment_generalDesc;
        }
        return PGR.core.strings.augment_specialDesc;
    }

    @Override
    public Texture getIcon() {
        switch (this) {
            case Summon:
                return PCLCoreImages.CardUI.augmentSummon.texture();
            case Played:
                return PCLCoreImages.CardUI.augmentPlayed.texture();
            case Hindrance:
                return PCLCoreImages.CardUI.augmentHindrance.texture();
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
                return PGR.core.strings.augment_played;
            case Hindrance:
                return PGR.core.strings.augment_hindrance;
            case General:
                return PGR.core.strings.augment_general;
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
    public List<? extends EUITooltip> getTips() {
        return Collections.singletonList(getTooltip());
    }

    @Override
    public EUITooltip getTooltip() {
        if (tip == null) {
            tip = new EUITooltip(getName(), getDesc());
        }
        return tip;
    }

    public boolean isTypeValid(AbstractCard.CardType type) {
        switch (this) {
            case Summon:
                return type == PCLEnum.CardType.SUMMON;
            case Played:
                switch (type) {
                    case ATTACK:
                    case SKILL:
                    case POWER:
                        return true;
                    default:
                        return false;
                }
            case Hindrance:
                switch (type) {
                    case CURSE:
                    case STATUS:
                        return true;
                    default:
                        return false;
                }
        }
        return true;
    }
}
