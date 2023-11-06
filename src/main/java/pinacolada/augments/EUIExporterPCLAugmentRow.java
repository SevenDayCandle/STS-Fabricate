package pinacolada.augments;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterRow;
import pinacolada.ui.PCLAugmentRenderable;

import java.io.File;
import java.util.ArrayList;

public class EUIExporterPCLAugmentRow extends EUIExporterRow {
    public static final EUIExporter.Exportable<PCLAugmentRenderable> augmentExportable = new EUIExporter.Exportable<>(EUIExporterPCLAugmentRow::exportAugment);

    public String Category;
    public int Tier;
    public String Description;

    public EUIExporterPCLAugmentRow(PCLAugmentRenderable augment) {
        this(augment.item);
    }

    public EUIExporterPCLAugmentRow(PCLAugment augment) {
        super(augment.save.ID, augment, AbstractCard.CardColor.COLORLESS, augment.getName());
        Category = augment.data.category.getName();
        Tier = augment.data.getTier(0);
        Description = augment.getFullText();
    }

    public static void exportAugment(Iterable<? extends PCLAugmentRenderable> cards, EUIExporter.ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(type.type), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportAugment(cards, type, file.getAbsolutePath());
        }
    }

    private static void exportAugment(Iterable<? extends PCLAugmentRenderable> cards, EUIExporter.ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporterPCLAugmentRow::new);
        type.exportRows(rows, path);
    }
}
