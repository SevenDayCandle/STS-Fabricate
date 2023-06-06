package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.ui.EUIBase;
import extendedui.ui.cardFilter.CustomCardLibraryScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.HashMap;

public class PCLLibraryModule extends EUIBase implements CustomCardPoolModule {
    private static final HashMap<ColorlessGroup, CardGroup> ColorlessGroupMapping = new HashMap<>();
    private static final HashMap<ColorlessGroup, CardGroup> CurseGroupMapping = new HashMap<>();
    public static ColorlessGroup group = ColorlessGroup.Default;
    protected CustomCardLibraryScreen screen;
    public EUIButton groupButton;
    public EUIContextMenu<ColorlessGroup> groupMenu;

    public PCLLibraryModule(CustomCardLibraryScreen screen) {
        this.screen = screen;
        groupButton = new EUIButton(ImageMaster.COLOR_TAB_BAR, new EUIHitbox(Settings.WIDTH * 0.18f, Settings.HEIGHT * 0.9f, scale(210), scale(80)))
                .setColor(Color.DARK_GRAY)
                .setLabel(EUIFontHelper.cardDescriptionFontNormal, 1f, group.getTitle())
                .setOnClick(() -> togglePool(group.next()))
                .setOnRightClick(() -> groupMenu.positionToOpen());
        groupMenu = (EUIContextMenu<ColorlessGroup>) new EUIContextMenu<>(new EUIHitbox(0, 0, scale(240), scale(48)).setIsPopupCompatible(true), ColorlessGroup::getTitle)
                .setPosition(screen.quickSearch.hb.cX + screen.quickSearch.hb.width * 0.5f, screen.quickSearch.hb.cY)
                .setOnChange(costs -> {
                    togglePool(costs.size() > 0 ? costs.get(0) : ColorlessGroup.Default);
                })
                .setCanAutosizeButton(true)
                .setItems(ColorlessGroup.values());
    }

    protected CardGroup getGroup(ColorlessGroup val) {
        HashMap<ColorlessGroup, CardGroup> mapping = getMapping(CustomCardLibraryScreen.currentColor);
        if (mapping == null) {
            return null;
        }
        CardGroup g = mapping.get(val);
        if (g == null) {
            g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(CustomCardLibraryScreen.currentColor).group) {
                if (val.isMatch(c)) {
                    g.addToBottom(c);
                }
            }
            mapping.put(val, g);
        }
        return g;
    }

    protected HashMap<ColorlessGroup, CardGroup> getMapping(AbstractCard.CardColor color) {
        switch (color) {
            case COLORLESS:
                return ColorlessGroupMapping;
            case CURSE:
                return CurseGroupMapping;
            default:
                return null;
        }
    }

    @Override
    public void open(ArrayList<AbstractCard> arrayList, AbstractCard.CardColor color, Object payload) {
        // Must refresh the description to have the proper character-specific icons show up
        for (AbstractCard c : arrayList) {
            c.initializeDescription();
        }

        // Only trigger the custom module if:
        // 1. Just switching into the colorless/curse pool (to initialize the module)
        // 2. When clicking on the button
        if (payload == null) {
            groupButton.setActive(getMapping(CustomCardLibraryScreen.currentColor) != null);
            togglePool(group);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        groupButton.tryRender(sb);
        groupMenu.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        groupMenu.tryUpdate();
        groupButton.tryUpdate();
    }

    protected void togglePool(ColorlessGroup val) {
        group = val;
        CardGroup cards = getGroup(val);
        if (cards != null) {
            screen.setActiveColor(CustomCardLibraryScreen.currentColor, cards, CustomCardLibraryScreen.currentColor);
        }
        groupButton.setText(group.getTitle());
    }

    public enum ColorlessGroup {
        Default(null),
        PCL(PCLCard.class);

        public final Class<? extends AbstractCard> cardClass;

        ColorlessGroup(Class<? extends AbstractCard> cardClass) {
            this.cardClass = cardClass;
        }

        public String getTitle() {
            if (this == ColorlessGroup.PCL) {
                return PGR.core.strings.misc_fabricate;
            }
            return EUIRM.strings.uiBasegame;
        }

        public boolean isMatch(AbstractCard c) {
            if (cardClass == null) {
                return !(c instanceof PCLCard);
            }
            return cardClass.isInstance(c);
        }

        public ColorlessGroup next() {
            ColorlessGroup[] values = ColorlessGroup.values();
            return values[(this.ordinal() + 1) % values.length];
        }
    }
}
