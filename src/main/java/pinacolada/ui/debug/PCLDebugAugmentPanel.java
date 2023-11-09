package pinacolada.ui.debug;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.debug.*;
import imgui.ImGuiTextFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugmentData;
import pinacolada.augments.PCLCustomAugmentSlot;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PCLDebugAugmentPanel {
    protected static final String ALL = "Any";
    protected static final String BASE_GAME = "Base";
    protected ArrayList<PCLAugmentData> sortedAugments = new ArrayList<>();
    protected ArrayList<String> sortedModIDs = new ArrayList<>();
    protected ImGuiTextFilter filter = new ImGuiTextFilter();
    protected DEUIFilteredSuffixListBox<PCLAugmentData> augmentList = new DEUIFilteredSuffixListBox<PCLAugmentData>("##all augments",
            sortedAugments, p -> p.ID, p -> p.strings.NAME, this::passes);
    protected DEUITabItem augments = new DEUITabItem("Augments");
    protected DEUIIntInput upgradeCount = new DEUIIntInput("Upgrades", 0, 0, Integer.MAX_VALUE);
    protected DEUIIntInput formCount = new DEUIIntInput("Form", 0, 0, Integer.MAX_VALUE);
    protected DEUIIntInput augmentCount = new DEUIIntInput("Count", 1, 1, Integer.MAX_VALUE);
    protected DEUIButton obtain = new DEUIButton("Obtain");
    protected DEUICombo<String> modList = new DEUICombo<String>("##modid", sortedModIDs, p -> p);


    public PCLDebugAugmentPanel() {
        regenerate();
    }

    private static String getModID(PCLAugmentData c) {
        return c.resources.ID;
    }

    private void obtain() {
        PCLAugmentData chosen = augmentList.get();
        if (chosen != null) {
            for (int j = 0; j < augmentCount.get(); ++j) {
                PGR.dungeon.addAugment(chosen.create(formCount.get(), upgradeCount.get()).save);
            }
        }
    }

    private boolean passes(PCLAugmentData augment) {
        String mod = modList.get();
        return (filter.passFilter(augment.ID) || filter.passFilter(augment.strings.NAME)) && (PCLDebugCardPanel.ALL.equals(mod) || getModID(augment).equals(mod));
    }

    public void refresh() {
        sortedAugments.clear();
        sortedModIDs.clear();
        regenerate();
    }

    protected void regenerate() {
        sortedAugments.addAll(PCLAugmentData.getAllData());
        sortedAugments.addAll(EUIUtils.map(PCLCustomAugmentSlot.getAugments(), slot -> slot.getBuilder(0)));
        sortedAugments.sort((a, b) -> StringUtils.compare(a.ID, b.ID));
        sortedModIDs.add(PCLDebugCardPanel.ALL);
        sortedModIDs.addAll(sortedAugments.stream()
                .map(PCLDebugAugmentPanel::getModID)
                .distinct()
                .collect(Collectors.toList()));
    }

    public void render() {
        augments.render(() -> {
            DEUIUtils.withWidth(90, () -> modList.renderInline());
            DEUIUtils.withFullWidth(() ->
            {
                filter.draw("##");
                augmentList.render();
            });
            DEUIUtils.withWidth(90, () ->
                    {
                        upgradeCount.renderInline();
                        formCount.renderInline();
                        augmentCount.render();
                    });
            DEUIUtils.disabledIf(AbstractDungeon.player == null || augmentList.get() == null, () ->
                    obtain.render(this::obtain));
        });
    }
}
