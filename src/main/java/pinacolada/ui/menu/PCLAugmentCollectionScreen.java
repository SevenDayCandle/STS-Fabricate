package pinacolada.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.ui.screens.EUIPoolScreen;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.card.ChooseCardForAugmentEffect;
import pinacolada.ui.cardView.PCLAugmentList;
import pinacolada.utilities.GameUtilities;

import java.util.HashMap;
import java.util.Map;

public class PCLAugmentCollectionScreen extends EUIPoolScreen {

    @SpireEnum
    public static AbstractDungeon.CurrentScreen AUGMENT_SCREEN;

    protected PCLAugmentList panel;
    protected PCLEffect curEffect;
    protected FuncT0<HashMap<PCLAugmentData, Integer>> getEntries;
    protected ActionT2<PCLAugmentData, Integer> addItem;
    protected boolean canSelect;

    public PCLAugmentCollectionScreen() {
        panel = new PCLAugmentList(this::doAction).enableCancel(false);
    }

    // To prevent the user from accessing the augment screen from the pop-up view while the augment selection is open
    public boolean allowOpenDeck() {
        return curEffect != null;
    }

    public boolean allowOpenMap() {
        return curEffect != null;
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return AUGMENT_SCREEN;
    }

    public void doAction(PCLAugment augment) {
        if (canSelect && augment != null) {
            curEffect = new ChooseCardForAugmentEffect(augment)
                    .addCallback(__ ->
                            {
                                refreshAugments();
                            }
                    );
        }
        else {
            close();
        }
    }

    public void openScreen(FuncT0<HashMap<PCLAugmentData, Integer>> getEntries, int rows, boolean canSelect) {
        super.reopen();
        this.getEntries = getEntries;
        this.canSelect = canSelect;
        panel = new PCLAugmentList(this::doAction, rows).enableCancel(false);
        addItem = canSelect ? (a, b) -> panel.addPanelItem(a, b) : panel::addListItem;
        refreshAugments();
    }

    @Override
    public void openingSettings() {
        AbstractDungeon.previousScreen = curScreen();
    }

    public void refreshAugments() {
        panel.clear();
        HashMap<PCLAugmentData, Integer> entries = getEntries != null ? getEntries.invoke() : new HashMap<>();
        for (Map.Entry<PCLAugmentData, Integer> params : entries.entrySet()) {
            PCLAugmentData data = params.getKey();
            int amount = params.getValue();
            if (data != null && amount > 0) {
                addItem.invoke(data, amount);
            }
        }
        EUI.countingPanel.openManual(GameUtilities.augmentStats(entries), __ -> {
        }, false);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (curEffect != null) {
            curEffect.render(sb);
        }
        else {
            panel.renderImpl(sb);
        }
        EUI.countingPanel.tryRender(sb);
    }

    @Override
    public void update() {
        if (curEffect != null) {
            curEffect.update();
            if (curEffect.isDone) {
                curEffect = null;
                refreshAugments();
            }
        }
        else {
            panel.updateImpl();
        }
        EUI.countingPanel.tryUpdate();

    }


}
