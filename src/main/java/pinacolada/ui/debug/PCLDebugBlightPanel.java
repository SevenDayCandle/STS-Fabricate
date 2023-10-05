package pinacolada.ui.debug;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.debug.*;
import imgui.ImGuiTextFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.blights.PCLDynamicBlight;
import pinacolada.blights.PCLBlight;
import pinacolada.patches.library.BlightHelperPatches;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PCLDebugBlightPanel {
    protected ArrayList<AbstractBlight> originalSorted = new ArrayList<>();
    protected ArrayList<AbstractBlight> sorted = originalSorted;
    protected ArrayList<String> sortedModIDs = new ArrayList<>();
    protected ImGuiTextFilter filter = new ImGuiTextFilter();
    protected DEUITabItem blights = new DEUITabItem("Blights");
    protected DEUICombo<String> modList = new DEUICombo<String>("##modid", sortedModIDs, p -> p);
    protected DEUIFilteredSuffixListBox<AbstractBlight> cardList = new DEUIFilteredSuffixListBox<AbstractBlight>("##all blights",
            sorted, p -> p.blightID, blight -> blight.name, this::passes);
    protected DEUIIntInput upgradeCount = new DEUIIntInput("Upgrades", 0, 0, Integer.MAX_VALUE);
    protected DEUIIntInput formCount = new DEUIIntInput("Form", 0, 0, Integer.MAX_VALUE);
    protected DEUIButton obtain = new DEUIButton("Obtain");

    public PCLDebugBlightPanel() {
        regenerate();
    }

    private static String getModID(AbstractBlight c) {
        if (c instanceof PCLDynamicBlight) {
            return PCLDebugCardPanel.CUSTOM;
        }
        int idx = c.blightID.indexOf(':');
        return idx < 0 ? PCLDebugCardPanel.BASE_GAME : c.blightID.substring(0, idx);
    }

    private AbstractBlight getCopy(AbstractBlight chosen) {
        if (chosen instanceof PCLBlight) {
            PCLBlight copy = ((PCLBlight) chosen).makeCopy();
            ((PCLBlight) copy).setForm(formCount.get());
            for (int j = 0; j < upgradeCount.get(); ++j) {
                ((PCLBlight) copy).upgrade();
            }
            return copy;
        }
        else {
            return BlightHelper.getBlight(chosen.blightID);
        }
    }

    private void obtain() {
        AbstractBlight chosen = cardList.get();
        if (chosen != null) {
            GameUtilities.obtainBlightWithoutEffect(getCopy(chosen));
        }
    }

    private boolean passes(AbstractBlight blight) {
        String mod = modList.get();
        return (filter.passFilter(blight.blightID) || filter.passFilter(blight.name)) && (PCLDebugCardPanel.ALL.equals(mod) || getModID(blight).equals(mod));
    }

    public void refresh() {
        originalSorted.clear();
        sortedModIDs.clear();
        regenerate();
    }

    protected void regenerate() {
        originalSorted.addAll(EUIGameUtils.getAllBlights()); // Will contain patched blights
        originalSorted.addAll(EUIUtils.map(PCLCustomBlightSlot.getBlights(null), PCLCustomBlightSlot::make));
        originalSorted.sort((a, b) -> StringUtils.compare(a.blightID, b.blightID));
        sortedModIDs.add(PCLDebugCardPanel.ALL);
        sortedModIDs.addAll(originalSorted.stream()
                .map(PCLDebugBlightPanel::getModID)
                .distinct()
                .collect(Collectors.toList()));
        modList.set(PCLDebugCardPanel.ALL);
    }

    public void render() {
        blights.render(() -> {
            DEUIUtils.withWidth(90, () -> modList.renderInline());
            DEUIUtils.withFullWidth(() ->
            {
                filter.draw("##");
                cardList.render();
            });
            DEUIUtils.withWidth(90, () ->
            {
                upgradeCount.renderInline();
                formCount.render();
            });
            DEUIUtils.disabledIf(AbstractDungeon.player == null || cardList.get() == null, () ->
            {
                obtain.renderInline(this::obtain);
            });
        });
    }
}
