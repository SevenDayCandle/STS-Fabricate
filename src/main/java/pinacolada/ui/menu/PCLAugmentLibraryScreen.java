package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUI;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.markers.CustomPoolModule;
import extendedui.ui.AbstractMenuScreen;
import pinacolada.augments.PCLAugmentData;
import pinacolada.patches.screens.GridCardSelectScreenPatches;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.ui.PCLGenericItemGrid;

import java.util.ArrayList;

public class PCLAugmentLibraryScreen extends AbstractMenuScreen {
    public static final ArrayList<CustomPoolModule<PCLAugmentRenderable>> globalModules = new ArrayList<>();
    public final MenuCancelButton cancelButton;
    public PCLGenericItemGrid<PCLAugmentRenderable> grid;

    public PCLAugmentLibraryScreen() {
        grid = new PCLGenericItemGrid<>(PCLAugmentRenderable.BASE_SCALE, PCLAugmentRenderable.BASE_SCALE * 1.5f);
        grid.setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);
        cancelButton = new MenuCancelButton();
    }

    private static ArrayList<PCLAugmentRenderable> createAll() {
        ArrayList<PCLAugmentRenderable> ret = new ArrayList<>();
        for (PCLAugmentData data : PCLAugmentData.getAvailable()) {
            if (data.branchFactor > 0) {
                createAugmentsImpl(ret, data, 0, 0);
            }
            else {
                for (int i = 0; i < data.maxForms; i++) {
                    for (int j = 0; j <= Math.max(0, data.maxUpgradeLevel); j++) {
                        ret.add(data.createRenderable(i, j));
                    }
                }
            }
        }
        return ret;
    }

    private static void createAugmentsImpl(ArrayList<PCLAugmentRenderable> ret, PCLAugmentData data, int form, int upgrade) {
        for (int i = form; i < Math.min(form + data.branchFactor, data.maxForms); i++) {
            ret.add(data.createRenderable(i, upgrade));
        }
        int minFormNext = GridCardSelectScreenPatches.getFormMin(form, data.branchFactor, upgrade);
        if (minFormNext < data.maxForms) {
            createAugmentsImpl(ret, data, minFormNext, upgrade + 1);
        }
    }

    @Override
    public void open() {
        super.open();
        this.cancelButton.show(CardLibraryScreen.TEXT[0]);

        grid.clear();
        grid.add(createAll());

        PGR.augmentFilters.initializeForSort(grid.group, __ -> {
            for (CustomPoolModule<PCLAugmentRenderable> module : globalModules) {
                module.open(PGR.augmentFilters.group.group, AbstractCard.CardColor.COLORLESS, true, null);
            }
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, AbstractCard.CardColor.COLORLESS);

        for (CustomPoolModule<PCLAugmentRenderable> module : globalModules) {
            module.open(grid.group.group, AbstractCard.CardColor.COLORLESS, true, null);
        }

        PGR.augmentFilters.sort();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        grid.tryRender(sb);
        cancelButton.render(sb);
        EUI.sortHeader.renderImpl(sb);
        if (!PGR.augmentFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
            EUIExporter.exportButton.tryRender(sb);
        }
        for (CustomPoolModule<AbstractBlight> module : EUI.globalCustomBlightLibraryModules) {
            module.render(sb);
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
            EUI.sortHeader.updateImpl();
            EUI.openFiltersButton.tryUpdate();
            EUIExporter.exportButton.tryUpdate();
            for (CustomPoolModule<AbstractBlight> module : EUI.globalCustomBlightLibraryModules) {
                module.update();
            }
        }
        EUIExporter.exportDropdown.tryUpdate();
    }
}
