package pinacolada.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterRow;

import java.io.File;
import java.util.ArrayList;

public class EUIExporterPCLPowerRow extends EUIExporterRow {
    public static final EUIExporter.Exportable<PCLPowerRenderable> powerExportable = new EUIExporter.Exportable<>(EUIExporterPCLPowerRow::exportPower);

    public String End_Turn_Behavior;
    public int Min_Amount;
    public int Max_Amount;
    public int Priority;
    public boolean Is_Common;
    public boolean Is_Metascaling;
    public boolean Is_Post_Action_Power;
    public String Description;

    public EUIExporterPCLPowerRow(PCLPowerRenderable augment) {
        this(augment.power);
    }

    public EUIExporterPCLPowerRow(PCLPowerData power) {
        super(power.ID, power, AbstractCard.CardColor.COLORLESS, power.getName());
        End_Turn_Behavior = power.endTurnBehavior.getText();
        Min_Amount = power.minAmount;
        Max_Amount = power.maxAmount;
        Priority = power.priority;
        Is_Common = power.isCommon;
        Is_Metascaling = power.isMetascaling;
        Is_Post_Action_Power = power.isPostActionPower;
        Description = power.getText();
    }

    public static void exportPower(Iterable<? extends PCLPowerRenderable> cards, EUIExporter.ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(type.type), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPower(cards, type, file.getAbsolutePath());
        }
    }

    private static void exportPower(Iterable<? extends PCLPowerRenderable> cards, EUIExporter.ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporterPCLPowerRow::new);
        type.exportRows(rows, path);
    }
}
