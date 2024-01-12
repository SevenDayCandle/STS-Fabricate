package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.screens.CustomCardLibraryScreen;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLColorlessGroupLibraryModule extends EUIBase implements CustomCardPoolModule {
    public static ColorlessGroup group = ColorlessGroup.Default;
    protected CustomCardLibraryScreen screen;
    public EUIButton groupButton;
    public EUIContextMenu<ColorlessGroup> groupMenu;

    public PCLColorlessGroupLibraryModule(CustomCardLibraryScreen screen) {
        this.screen = screen;
        groupButton = new EUIButton(ImageMaster.COLOR_TAB_BAR, new EUIHitbox(Settings.WIDTH * 0.18f, Settings.HEIGHT * 0.905f, scale(210), scale(80)))
                .setColor(Color.DARK_GRAY)
                .setLabel(FontHelper.cardDescFont_N, 1f, group.getTitle())
                .setOnRightClick(() -> groupMenu.positionToOpen());
        groupMenu = (EUIContextMenu<ColorlessGroup>) new EUIContextMenu<>(new EUIHitbox(0, 0, scale(240), scale(48)).setIsPopupCompatible(true), ColorlessGroup::getTitle)
                .setPosition(screen.quickSearch.hb.cX + screen.quickSearch.hb.width * 0.5f, screen.quickSearch.hb.cY)
                .setCanAutosizeButton(true)
                .setItems(ColorlessGroup.values());
    }

    protected ArrayList<? extends AbstractCard> getGroup(ArrayList<? extends AbstractCard> arrayList, ColorlessGroup val) {
        if (val == ColorlessGroup.Default) {
            return arrayList;
        }
        return EUIUtils.map(arrayList, val::processCard);
    }

    @Override
    public void open(ArrayList<? extends AbstractCard> arrayList, AbstractCard.CardColor color, boolean isAll, Object payload) {
        // Only trigger the custom module if:
        // 1. Just switching into the colorless/curse pool (to initialize the module)
        // 2. When clicking on the button
        if (payload == null) {
            groupButton
                    .setOnClick(() -> togglePool(arrayList, group.next(), isAll))
                    .setActive(GameUtilities.isColorlessCardColor(color));
            groupMenu.setOnChange(costs -> {
                togglePool(arrayList, costs.size() > 0 ? costs.get(0) : ColorlessGroup.Default, isAll);
            });
            togglePool(arrayList, group, isAll);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        groupButton.tryRender(sb);
        groupMenu.tryRender(sb);
    }

    protected void togglePool(ArrayList<? extends AbstractCard> arrayList, ColorlessGroup val, boolean isAll) {
        group = val;
        ArrayList<? extends AbstractCard> cards = getGroup(arrayList, val);
        screen.setActiveColor(CustomCardLibraryScreen.getCurrentColor(), cards, isAll, CustomCardLibraryScreen.getCurrentColor());
        groupButton.setText(group.getTitle());
    }

    @Override
    public void updateImpl() {
        groupMenu.tryUpdate();
        groupButton.tryUpdate();
    }

    public enum ColorlessGroup {
        Default,
        PCL;

        public String getTitle() {
            if (this == ColorlessGroup.PCL) {
                return PGR.core.strings.misc_fabricate;
            }
            return EUIRM.strings.ui_basegame;
        }

        public ColorlessGroup next() {
            ColorlessGroup[] values = ColorlessGroup.values();
            return values[(this.ordinal() + 1) % values.length];
        }

        public AbstractCard processCard(AbstractCard c) {
            if (this == PCL) {
                TemplateCardData template = TemplateCardData.getTemplate(c.cardID);
                if (template != null) {
                    return template.create(c.timesUpgraded);
                }
            }
            return c;
        }
    }
}
