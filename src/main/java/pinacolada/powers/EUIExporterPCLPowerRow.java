package pinacolada.powers;

import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterRow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class EUIExporterPCLPowerRow extends EUIExporterRow {
    public static final EUIExporter.Exportable<PCLPowerRenderable> powerExportable = new EUIExporter.Exportable<>(EUIExporterPCLPowerRow::exportPowerCsv, EUIExporterPCLPowerRow::exportPowerJson);

    public String endTurnBehavior;
    public int minAmount;
    public int maxAmount;
    public int priority;
    public boolean isCommon;
    public boolean isMetascaling;
    public boolean isPostActionPower;
    public String description;

    public EUIExporterPCLPowerRow(PCLPowerRenderable augment) {
        this(augment.power);
    }

    public EUIExporterPCLPowerRow(PCLPowerData power) {
        super(power.ID, EUIGameUtils.getModID(power), "", power.getName());
        endTurnBehavior = String.valueOf(power.endTurnBehavior);
        minAmount = power.minAmount;
        maxAmount = power.maxAmount;
        priority = power.priority;
        isCommon = power.isCommon;
        isMetascaling = power.isMetascaling;
        isPostActionPower = power.isPostActionPower;
        description = power.getText();
    }

    public static void exportPowerCsv(PCLPowerRenderable c) {
        exportPowerCsv(Collections.singleton(c));
    }

    public static void exportPowerCsv(Iterable<? extends PCLPowerRenderable> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EUIExporter.EXT_CSV), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPowerCsv(cards, file.getAbsolutePath());
        }
    }

    private static void exportPowerCsv(Iterable<? extends PCLPowerRenderable> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporterPCLPowerRow::new);
        EUIExporter.exportImplCsv(rows, path);
    }

    public static void exportPowerJson(PCLPowerRenderable c) {
        exportPowerJson(Collections.singleton(c));
    }

    public static void exportPowerJson(Iterable<? extends PCLPowerRenderable> cards) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(EUIExporter.EXT_JSON), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPowerJson(cards, file.getAbsolutePath());
        }
    }

    public static void exportPowerJson(Iterable<? extends PCLPowerRenderable> cards, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporterPCLPowerRow::new);
        EUIExporter.exportImplJson(rows, path);
    }
}
