package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.interfaces.markers.CustomPoolModule;
import extendedui.ui.AbstractMenuScreen;
import pinacolada.augments.PCLAugmentData;
import pinacolada.augments.PCLAugmentRenderable;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLAugmentGrid;

import java.util.ArrayList;

public class PCLAugmentLibraryScreen extends AbstractMenuScreen {
    public static final ArrayList<CustomPoolModule<PCLAugmentRenderable>> globalModules = new ArrayList<>();
    public PCLAugmentGrid grid;
    public final MenuCancelButton cancelButton;

    public PCLAugmentLibraryScreen() {
        grid = (PCLAugmentGrid) new PCLAugmentGrid()
                .setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);
        cancelButton = new MenuCancelButton();
    }

    @Override
    public void open() {
        super.open();
        this.cancelButton.show(CardLibraryScreen.TEXT[0]);

        grid.clear();
        grid.add(EUIUtils.map(PCLAugmentData.getAvailable(), PCLAugmentRenderable::new));

        PGR.augmentFilters.initializeForCustomHeader(grid.group, __ -> {
            for (CustomPoolModule<PCLAugmentRenderable> module : globalModules) {
                module.open(PGR.augmentHeader.group.group, AbstractCard.CardColor.COLORLESS, null);
            }
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, AbstractCard.CardColor.COLORLESS, false, true);

        for (CustomPoolModule<PCLAugmentRenderable> module : globalModules) {
            module.open(grid.group.group, AbstractCard.CardColor.COLORLESS, null);
        }
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        boolean shouldDoStandardUpdate = !PGR.augmentFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
        if (shouldDoStandardUpdate) {
            grid.tryUpdate();
            cancelButton.update();
            if (this.cancelButton.hb.clicked) {
                this.cancelButton.hb.clicked = false;
                this.cancelButton.hide();
                close();
            }
            PGR.augmentHeader.updateImpl();
            EUI.openBlightFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            for (CustomPoolModule<AbstractBlight>module : EUI.globalCustomBlightLibraryModules) {
                module.update();
            }
        }
        EUIExporter.exportDropdown.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        grid.tryRender(sb);
        cancelButton.render(sb);
        PGR.augmentHeader.renderImpl(sb);
        if (!PGR.augmentFilters.isActive) {
            EUI.openBlightFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        for (CustomPoolModule<AbstractBlight>module : EUI.globalCustomBlightLibraryModules) {
            module.render(sb);
        }
    }
}