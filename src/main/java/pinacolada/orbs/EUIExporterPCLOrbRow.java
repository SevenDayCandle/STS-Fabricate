package pinacolada.orbs;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.exporter.EUIExporterRow;
import pinacolada.powers.PCLPowerData;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.PCLPowerRenderable;

import java.io.File;
import java.util.ArrayList;

public class EUIExporterPCLOrbRow extends EUIExporterRow {
    public static final EUIExporter.Exportable<PCLOrbRenderable> orbExportable = new EUIExporter.Exportable<>(EUIExporterPCLOrbRow::exportPower);

    public String Timing;
    public int Base_Evoke;
    public int Base_Passive;
    public boolean Is_Evoke_Focus_Based;
    public boolean Is_Passive_Focus_Based;
    public String Description;

    public EUIExporterPCLOrbRow(PCLOrbRenderable augment) {
        this(augment.item);
    }

    public EUIExporterPCLOrbRow(PCLOrbData power) {
        super(power.ID, power, AbstractCard.CardColor.COLORLESS, power.getName());
        Timing = power.timing.getTitle();
        Base_Evoke = power.getBaseEvoke(0);
        Base_Passive = power.getBasePassive(0);
        Is_Evoke_Focus_Based = power.applyFocusToEvoke;
        Is_Passive_Focus_Based = power.applyFocusToPassive;
        Description = power.getText();
    }

    public static void exportPower(Iterable<? extends PCLOrbRenderable> cards, EUIExporter.ExportType type) {
        File file = EUIUtils.saveFile(EUIUtils.getFileFilter(type.type), EUIConfiguration.lastExportPath);
        if (file != null) {
            exportPower(cards, type, file.getAbsolutePath());
        }
    }

    private static void exportPower(Iterable<? extends PCLOrbRenderable> cards, EUIExporter.ExportType type, String path) {
        ArrayList<? extends EUIExporterRow> rows = EUIUtils.map(cards, EUIExporterPCLOrbRow::new);
        type.exportRows(rows, path);
    }
}
