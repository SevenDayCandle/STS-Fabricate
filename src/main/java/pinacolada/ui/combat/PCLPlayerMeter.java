package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.controls.EUITutorialPage;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.ui.EUICardDraggable;

public abstract class PCLPlayerMeter extends EUICardDraggable<PCLCard> implements ClickableProvider {
    public static EUITutorialPage AFFINITY_TUTORIAL = new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.affinityGeneral.title), PGR.core.strings.tutorial_affinityTutorial);
    public static EUITutorialPage TAG_TUTORIAL = new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.strings.cedit_tags), PGR.core.strings.tutorial_tagTutorial);
    public static EUITutorialPage SUMMON_TUTORIAL1 = new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 1), PGR.core.strings.tutorial_summonTutorial1);
    public static EUITutorialPage SUMMON_TUTORIAL2 = new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 2), PGR.core.strings.tutorial_summonTutorial2);
    public static EUITutorialPage SUMMON_TUTORIAL3 = new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 3), PGR.core.strings.tutorial_summonTutorial3);
    public static EUITutorialPage SUMMON_TUTORIAL4 = new EUITutorialPage(makeTitle(PGR.core.strings.misc_fabricate, PGR.core.tooltips.summon.title, 4), PGR.core.strings.tutorial_summonTutorial4);
    protected EUIButton infoIcon;
    protected String id;
    protected int currentScore;
    protected int highestScore;
    public PCLPlayerMeter(String id, STSConfigItem<Vector2> config, float iconSize) {
        super(config, new DraggableHitbox(screenW(0.0366f), screenH(0.425f), iconSize, iconSize, true), iconSize);
        this.id = id;
        infoIcon = new EUIButton(EUIRM.images.info.texture(), new RelativeHitbox(hb, scale(40f), scale(40f), scale(20), scale(-50f)))
                .setTooltip(getInfoTitle(), getInfoMainDescrption() + EUIUtils.DOUBLE_SPLIT_LINE + PGR.core.strings.tutorial_learnMore)
                .setOnClick(() -> {
                    EUI.ftueScreen.open(new EUITutorial(getInfoPages()));
                });
    }

    public abstract String getInfoTitle();

    public abstract String getInfoMainDescrption();

    public abstract EUITutorialPage[] getInfoPages();

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLPlayerMeter> type) {
        return resources.createID(type.getSimpleName());
    }

    public static String makeTitle(String category, String addendum) {
        return category + ": " + addendum;
    }

    public static String makeTitle(String category, String addendum, int index) {
        return category + ": " + EUIRM.strings.generic2(addendum, index);
    }

    public void addLevel(PCLAffinity affinity, int amount) {
    }

    public void addSkip(int amount) {
    }

    public boolean canGlow(AbstractCard c) {
        return true;
    }

    public void disableAffinity(PCLAffinity affinity) {
    }

    public void flash(int target) {
    }

    public void flashAffinity(PCLAffinity affinity) {
    }

    public PCLUseInfo generateInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        return new PCLUseInfo(card, source, target);
    }

    public PCLAffinity getCurrentAffinity() {
        return get(0);
    }

    public PCLAffinity get(int target) {
        return PCLAffinity.General;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public String getID() {
        return id;
    }

    public EUITooltip getTooltip() {
        return null;
    }

    public int getLevel(PCLAffinity affinity) {
        return 0;
    }

    public Object getRerollDescription() {
        return null;
    }

    public Object getRerollDescription2() {
        return null;
    }

    public void increaseScore(int amount) {
        currentScore += amount;
        highestScore = Math.max(highestScore, currentScore);
    }

    public void initialize() {
        super.initialize();
        currentScore = 0;
        highestScore = 0;
    }

    public boolean isHovered() {
        return super.isHovered() || infoIcon.hb.hovered;
    }

    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        infoIcon.renderImpl(sb);
    }

    public void updateImpl(PCLCard card, AbstractCreature target, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget) {
        infoIcon.tryUpdate();
    }

    public boolean isPowerActive(PCLAffinity affinity) {
        return false;
    }

    public float modifyBlock(float block, PCLCard source, PCLCard card, AbstractCreature target) {
        return block;
    }

    public float modifyDamage(float damage, PCLCard source, PCLCard card, AbstractCreature target) {
        return damage;
    }

    public float modifyOrbOutput(float initial, AbstractCreature target, AbstractOrb orb) {
        return initial;
    }

    public void onCardPlayed(PCLCard card, PCLUseInfo info, boolean fromSummon) {
    }

    public void onEndOfTurn() {
    }

    public void onStartOfTurn() {
    }

    public PCLAffinity set(PCLAffinity affinity, int target) {
        return get(0);
    }

    public int size() {
        return 0;
    }
}
