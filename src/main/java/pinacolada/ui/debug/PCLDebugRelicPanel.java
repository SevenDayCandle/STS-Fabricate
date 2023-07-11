package pinacolada.ui.debug;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import extendedui.EUIUtils;
import extendedui.debug.*;
import extendedui.patches.screens.RelicViewScreenPatches;
import imgui.ImGuiTextFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.PCLEffects;
import pinacolada.patches.library.RelicLibraryPatches;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelic;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.relics.PCLRelic;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PCLDebugRelicPanel {
    protected ArrayList<AbstractRelic> originalSorted = new ArrayList<>();
    protected ArrayList<AbstractRelic> sorted = originalSorted;
    protected ArrayList<String> sortedModIDs = new ArrayList<>();
    protected ImGuiTextFilter filter = new ImGuiTextFilter();
    protected DEUITabItem relics = new DEUITabItem("Relics");
    protected DEUICombo<String> modList = new DEUICombo<String>("##modid", sortedModIDs, p -> p);
    protected DEUIFilteredSuffixListBox<AbstractRelic> cardList = new DEUIFilteredSuffixListBox<AbstractRelic>("##all relics",
            sorted, p -> p.relicId, GameUtilities::getRelicName, this::passes);
    protected DEUIIntInput upgradeCount = new DEUIIntInput("Upgrades", 0, 0, Integer.MAX_VALUE);
    protected DEUIIntInput formCount = new DEUIIntInput("Form", 0, 0, Integer.MAX_VALUE);
    protected DEUIButton obtain = new DEUIButton("Obtain");

    public PCLDebugRelicPanel() {
        originalSorted.addAll(RelicViewScreenPatches.getAllReics());
        originalSorted.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(null), PCLCustomRelicSlot::make));
        originalSorted.sort((a, b) -> StringUtils.compare(a.relicId, b.relicId));
        sortedModIDs.add(PCLDebugCardPanel.ALL);
        sortedModIDs.addAll(originalSorted.stream()
                .map(c -> getModID(c.relicId))
                .distinct()
                .collect(Collectors.toList()));
        modList.set(PCLDebugCardPanel.ALL);
    }

    private static String getModID(String cardID) {
        int idx = cardID.indexOf(':');
        return idx < 0 ? PCLDebugCardPanel.BASE_GAME : cardID.substring(0, idx);
    }

    private void obtain() {
        AbstractRelic chosen = cardList.get();
        if (chosen != null) {
            GameUtilities.obtainRelicFromEvent(getCopy(chosen));
        }
    }

    private AbstractRelic getCopy(AbstractRelic chosen) {
        AbstractRelic copy = chosen.makeCopy();
        if (copy instanceof PCLRelic) {
            ((PCLRelic) copy).setForm(formCount.get());
            for (int j = 0; j < upgradeCount.get(); ++j) {
                ((PCLRelic) copy).upgrade();
            }
        }
        return copy;
    }

    private boolean passes(AbstractRelic relic) {
        String mod = modList.get();
        return (filter.passFilter(relic.relicId) || filter.passFilter(GameUtilities.getRelicName(relic))) && (PCLDebugCardPanel.ALL.equals(mod) || getModID(relic.relicId).equals(mod));
    }

    public void refresh() {
        originalSorted.clear();
        originalSorted.addAll(RelicViewScreenPatches.getAllReics());
        originalSorted.addAll(EUIUtils.map(PCLCustomRelicSlot.getRelics(null), PCLCustomRelicSlot::make));
        originalSorted.sort((a, b) -> StringUtils.compare(a.relicId, b.relicId));
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
