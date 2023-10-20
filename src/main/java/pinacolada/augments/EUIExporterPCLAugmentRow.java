package pinacolada.augments;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterRow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class EUIExporterPCLAugmentRow extends EUIExporterRow {
    public static final EUIExporter.Exportable<PCLAugmentRenderable> augmentExportable = new EUIExporter.Exportable<>(EUIExporterPCLAugmentRow::exportAugment);

    public String category;
    public String subCategory;
    public int tier;
    public String description;

    public EUIExporterPCLAugmentRow(PCLAugmentRenderable augment) {
        this(augment.augment);
    }

    public EUIExporterPCLAugmentRow(PCLAugment augment) {
        super(augment.ID, augment, AbstractCard.CardColor.COLORLESS, augment.getName());
        category = augment.data.category.getName();
        subCategory = augment.data.categorySub.getName();
        tier = augment.data.tier;
        description = augment.getFullText();
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
