package pinacolada.augments;

import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterRow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class EUIExporterPCLAugmentRow extends EUIExporterRow {
    public static final EUIExporter.Exportable<PCLAugmentRenderable> augmentExportable = new EUIExporter.Exportable<>(EUIExporterPCLAugmentRow::exportAugmentCsv, EUIExporterPCLAugmentRow::exportAugmentJson);

    public String category;
    public String subCategory;
    public int tier;
    public String description;

    public EUIExporterPCLAugmentRow(PCLAugmentRenderable augment) {
        this(augment.augment);
    }

    public EUIExporterPCLAugmentRow(PCLAugment augment) {
        super(augment.ID, EUIGameUtils.getModID(augment), "", augment.getName());
        category = String.valueOf(augment.data.category);
        subCategory = augment.data.categorySub.suffix;
        tier = augment.data.tier;
        description = augment.getFullText();
    }

    public static void exportAugmentCsv(PCLAugmentRenderable c) {
        exportAugmentCsv(Collections.singleton(c));
    }

    public static void exportAugmentCsv(Iterable<? extends PCLAugmentRenderable> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EUIExporter.EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportAugmentCsv(cards, file.getAbsolutePath());
        }
    }

    private static void exportAugmentCsv(Iterable<? extends PCLAugmentRenderable> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporterPCLAugmentRow::new);
        EUIExporter.exportImplCsv(rows, path);
    }

    public static void exportAugmentJson(PCLAugmentRenderable c) {
        exportAugmentJson(Collections.singleton(c));
    }

    public static void exportAugmentJson(Iterable<? extends PCLAugmentRenderable> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EUIExporter.EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportAugmentJson(cards, file.getAbsolutePath());
        }
    }

    public static void exportAugmentJson(Iterable<? extends PCLAugmentRenderable> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporterPCLAugmentRow::new);
        EUIExporter.exportImplJson(rows, path);
    }
}
