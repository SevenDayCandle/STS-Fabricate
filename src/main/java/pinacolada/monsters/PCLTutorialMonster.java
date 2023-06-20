package pinacolada.monsters;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.TourProvider;
import extendedui.ui.tooltips.EUITourTooltip;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.vfx.FadingParticleEffect;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.resources.pcl.PCLCoreStrings;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class PCLTutorialMonster extends PCLCreature implements TourProvider {
    public static ArrayList<TutorialInfo> tutorials = new ArrayList<>();
    public static STSConfigItem<Boolean> currentConfig;

    public ArrayList<FuncT0<EUITourTooltip>> steps = new ArrayList<>();
    public int current;
    protected EUITourTooltip waitingTip;

    public static void register(STSConfigItem<Boolean> config, PCLCreatureData data, FuncT1<Boolean, AbstractPlayer> shouldShow) {
        tutorials.add(new TutorialInfo(config, data, shouldShow));
        BaseMod.addMonster(data.ID, data::create);
    }

    public static MonsterGroup tryStart() {
        for (PCLTutorialMonster.TutorialInfo tutorialInfo : PCLTutorialMonster.tutorials) {
            if (tutorialInfo.shouldShow.invoke(AbstractDungeon.player) && !tutorialInfo.config.get()) {
                MonsterGroup group = tutorialInfo.data.getEncounter();
                if (group != null) {
                    currentConfig = tutorialInfo.config;
                    return group;
                }
            }
        }
        return null;
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
            waitingTip.setWaitOnProvider(this)
                    .setDescription(waitingTip.description + EUIUtils.DOUBLE_SPLIT_LINE + PCLCoreStrings.colorString("p", PGR.core.strings.tutorial_tutorialReplay));
            EUITourTooltip.queueTutorial(waitingTip);
        }
        else {
            finishTutorial();
        }
    }

    @SafeVarargs
    public final PCLTutorialMonster addSteps(FuncT0<EUITourTooltip>... steps) {
        this.steps.addAll(Arrays.asList(steps));
        return this;
    }

    public void onComplete() {
        PCLSFX.play(PCLSFX.TINGSHA);
        PCLEffects.Queue.add(new FadingParticleEffect(PCLCoreImages.Menu.check.texture(), Settings.WIDTH * 0.75f, Settings.HEIGHT * 0.6f)
                .setTargetPosition(Settings.WIDTH * 0.75f, Settings.HEIGHT * 0.68f)
                .setDuration(1.8f, true)
        );
        if (current < steps.size() - 1) {
            EUITourTooltip.queueTutorial(new EUITourTooltip(this.hb,
                    PGR.core.strings.tutorial_tutorialStepHeader, PGR.core.strings.tutorial_tutorialNextStep).setCanDismiss(true));
        }
        else {
            EUITourTooltip.queueTutorial(new EUITourTooltip(this.hb,
                    PGR.core.strings.tutorial_tutorialCompleteHeader, PGR.core.strings.tutorial_tutorialComplete).setCanDismiss(true));
        }
    }

    public void finishTutorial() {
        EUITourTooltip.clearTutorialQueue();
        die();
        if (currentConfig != null) {
            currentConfig.set(true);
        }
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

    public void addToHand(AbstractCard... cards) {
        AbstractDungeon.player.hand.group.addAll(Arrays.asList(cards));
    }

    public void replaceHandWith(AbstractCard... cards) {
        clearLists();
        addToHand(cards);
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