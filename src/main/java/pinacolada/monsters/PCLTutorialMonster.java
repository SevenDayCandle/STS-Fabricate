package pinacolada.monsters;

import basemod.BaseMod;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TourProvider;
import extendedui.ui.tooltips.EUITourTooltip;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.vfx.FadingParticleEffect;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class PCLTutorialMonster extends PCLCreature implements TourProvider {
    public static ArrayList<TutorialInfo> tutorials = new ArrayList<>();

    public ArrayList<FuncT0<EUITourTooltip>> steps = new ArrayList<>();
    public int current;
    protected EUITourTooltip waitingTip;

    public static void register(STSConfigItem<Boolean> config, PCLCreatureData data, FuncT1<Boolean, AbstractPlayer> shouldShow) {
        tutorials.add(new TutorialInfo(config, data, shouldShow));
        BaseMod.addMonster(data.ID, data::create);
    }

    public static boolean tryStart() {
        for (PCLTutorialMonster.TutorialInfo tutorialInfo : PCLTutorialMonster.tutorials) {
            if (tutorialInfo.shouldShow.invoke(AbstractDungeon.player) && EUIConfiguration.triggerOnFirstView(tutorialInfo.config)) {
                tutorialInfo.data.forceEncounter();
                return true;
            }
        }
        return false;
    }

    public PCLTutorialMonster(PCLCreatureData data) {
        super(data);
    }

    public PCLTutorialMonster(PCLCreatureData data, float offsetX, float offsetY) {
        super(data, offsetX, offsetY);
    }

    @Override
    protected void getMove(int i) {
        this.setMove("", (byte) -1, Intent.NONE);
    }

    @Override
    public void usePreBattleAction() {
        performActions(false);
    }

    @Override
    public void performActions(boolean manual) {
        if (waitingTip != null && !EUITourTooltip.hasTutorial(waitingTip)) {
            waitingTip = null;
            current += 1;
        }
        if (current < steps.size()) {
            EUITourTooltip.clearTutorialQueue();
            waitingTip = steps.get(current).invoke();
            waitingTip.setWaitOnProvider(this);
            EUITourTooltip.queueTutorial(waitingTip);
        }
        else {
            die();
        }
    }

    public PCLTutorialMonster addSteps(FuncT0<EUITourTooltip>... steps) {
        this.steps.addAll(Arrays.asList(steps));
        return this;
    }

    public void onComplete() {
        PCLSFX.play(PCLSFX.TINGSHA);
        PCLEffects.Queue.add(new FadingParticleEffect(EUIRM.images.plus.texture(), Settings.WIDTH * 0.75f, Settings.HEIGHT * 0.5f)
                .setTargetPosition(Settings.WIDTH * 0.75f, Settings.HEIGHT * 0.6f)
                .setDuration(1.5f, true)
        );
    }

    public void talk(String message) {
        PCLEffects.Queue.talk(this, message);
    }

    public void clearLists() {
        AbstractDungeon.player.drawPile.group = new ArrayList<>();
        AbstractDungeon.player.hand.group = new ArrayList<>();
        AbstractDungeon.player.discardPile.group = new ArrayList<>();
        AbstractDungeon.player.limbo.group = new ArrayList<>();
    }

    public static class TutorialInfo {
        public final STSConfigItem<Boolean> config;
        public final PCLCreatureData data;
        public final FuncT1<Boolean, AbstractPlayer> shouldShow;

        public TutorialInfo(STSConfigItem<Boolean> config, PCLCreatureData data, FuncT1<Boolean, AbstractPlayer> shouldShow) {
            this.config = config;
            this.data = data;
            this.shouldShow = shouldShow;
        }
    }
}
