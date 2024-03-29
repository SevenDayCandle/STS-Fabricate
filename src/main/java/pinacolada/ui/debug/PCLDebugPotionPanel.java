package pinacolada.ui.debug;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIUtils;
import extendedui.debug.*;
import imgui.ImGuiTextFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLDynamicPotion;
import pinacolada.potions.PCLPotion;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PCLDebugPotionPanel {
    protected ArrayList<AbstractPotion> originalSorted = new ArrayList<>();
    protected ArrayList<AbstractPotion> sorted = originalSorted;
    protected ArrayList<String> sortedModIDs = new ArrayList<>();
    protected ImGuiTextFilter filter = new ImGuiTextFilter();
    protected DEUITabItem relics = new DEUITabItem("Potions");
    protected DEUICombo<String> modList = new DEUICombo<String>("##modid", sortedModIDs, p -> p);
    protected DEUIFilteredSuffixListBox<AbstractPotion> cardList = new DEUIFilteredSuffixListBox<AbstractPotion>("##all potions",
            sorted, p -> p.ID, p -> p.name, this::passes);
    protected DEUIIntInput count = new DEUIIntInput("Count", 1, 1, Integer.MAX_VALUE);
    protected DEUIIntInput upgradeCount = new DEUIIntInput("Upgrades", 0, 0, Integer.MAX_VALUE);
    protected DEUIIntInput formCount = new DEUIIntInput("Form", 0, 0, Integer.MAX_VALUE);
    protected DEUIButton obtain = new DEUIButton("Obtain");

    public PCLDebugPotionPanel() {
        regenerate();
    }

    private static String getModID(AbstractPotion c) {
        if (c instanceof PCLDynamicPotion) {
            return PCLDebugCardPanel.CUSTOM;
        }
        int idx = c.ID.indexOf(':');
        return idx < 0 ? PCLDebugCardPanel.BASE_GAME : c.ID.substring(0, idx);
    }

    private AbstractPotion getCopy(AbstractPotion chosen) {
        AbstractPotion copy = chosen.makeCopy();
        if (copy instanceof PCLPotion) {
            ((PCLPotion) copy).setForm(formCount.get());
            for (int j = 0; j < upgradeCount.get(); ++j) {
                ((PCLPotion) copy).upgrade();
            }
        }
        return copy;
    }

    private void obtain() {
        AbstractPotion chosen = cardList.get();
        if (chosen != null && AbstractDungeon.player != null) {
            for (int i = 0; i < count.get(); i++) {
                AbstractDungeon.player.obtainPotion(getCopy(chosen));
            }
        }
    }

    private boolean passes(AbstractPotion potion) {
        String mod = modList.get();
        return (filter.passFilter(potion.ID) || filter.passFilter(potion.name)) && (PCLDebugCardPanel.ALL.equals(mod) || getModID(potion).equals(mod));
    }

    public void refresh() {
        originalSorted.clear();
        sortedModIDs.clear();
        regenerate();
    }

    protected void regenerate() {
        originalSorted.addAll(GameUtilities.getPotions(null));
        originalSorted.addAll(EUIUtils.map(PCLCustomPotionSlot.getPotions(), PCLCustomPotionSlot::make));
        originalSorted.sort((a, b) -> StringUtils.compare(a.ID, b.ID));
        sortedModIDs.add(PCLDebugCardPanel.ALL);
        sortedModIDs.addAll(originalSorted.stream()
                .map(PCLDebugPotionPanel::getModID)
                .distinct()
                .collect(Collectors.toList()));
        modList.set(PCLDebugCardPanel.ALL);
    }

    public void render() {
        relics.render(() -> {
            DEUIUtils.withWidth(90, () -> modList.renderInline());
            DEUIUtils.withFullWidth(() ->
            {
                filter.draw("##");
                cardList.render();
            });
            DEUIUtils.withWidth(90, () ->
            {
                count.renderInline();
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
