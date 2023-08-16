package pinacolada.dungeon;

import basemod.abstracts.AbstractCardModifier;
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
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.ui.EUICardDraggable;

public abstract class PCLPlayerMeter extends EUICardDraggable<PCLCard> {
    public final String ID;
    protected EUIButton infoIcon;
    protected PCLUseInfo info;
    protected int currentScore;
    protected int highestScore;

    public PCLPlayerMeter(String id, STSConfigItem<Vector2> config, float iconSize) {
        super(config, new DraggableHitbox(screenW(0.0366f), screenH(0.425f), iconSize, iconSize, true), iconSize);
        this.ID = id;
        infoIcon = new EUIButton(EUIRM.images.info.texture(), new RelativeHitbox(hb, scale(40f), scale(40f), scale(25), scale(-40f)))
                .setTooltip(getInfoTitle(), getInfoMainDescrption() + EUIUtils.DOUBLE_SPLIT_LINE + PGR.core.strings.tutorial_learnMore)
                .setOnClick(() -> EUI.ftueScreen.openScreen(new EUITutorial(getInfoPages())))
                .setColor(EUIColors.white(0.5f));
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLPlayerMeter> type) {
        return resources.createID(type.getSimpleName());
    }

    public static boolean isSwapIntended(PCLCard incoming, PCLCard other) {
        return incoming.type == PCLEnum.CardType.SUMMON && other != null && other != incoming;
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
        return false;
    }

    public void disableAffinity(PCLAffinity affinity) {
    }

    public void flash(int target) {
    }

    public void flashAffinity(PCLAffinity affinity) {
    }

    /* Creates a NEW info object. To be used when executing infos in effects to ensure that data is not interfered with during the action execution process */
    public PCLUseInfo generateInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        return new PCLUseInfo(card, source, target);
    }

    public PCLAffinity get(int target) {
        return PCLAffinity.General;
    }

    public PCLAffinity getCurrentAffinity() {
        return get(0);
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    /* Updates a CACHED info object. To be used in updating calls to avoid memory churn */
    public PCLUseInfo getInfo(AbstractCard card, AbstractCreature source, AbstractCreature target) {
        if (info == null) {
            info = generateInfo(card, source, target);
        }
        else {
            info.set(card, source, target);
        }
        return info;
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

    public float modifyBlock(float block, PCLCard source, PCLCard card, AbstractCreature target) {
        return block;
    }

    public float modifyDamage(float damage, PCLCard source, PCLCard card, AbstractCreature target) {
        return damage;
    }

    public float modifyOrbOutput(float initial, AbstractCreature target, AbstractOrb orb) {
        return initial;
    }

    public void onCardCreated(AbstractCard card, boolean startOfBattle) {

    }

    public AbstractCardModifier onCardModified(AbstractCard card, AbstractCardModifier modifier) {
        return modifier;
    }

    public void onCardPlayed(PCLCard card, PCLUseInfo info, boolean fromSummon) {
    }

    public void onEndOfTurn() {
    }

    public void onStartOfTurn() {
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        infoIcon.renderImpl(sb);
    }

    public PCLAffinity set(PCLAffinity affinity, int target) {
        return get(0);
    }

    public int size() {
        return 0;
    }

    @Override
    public void updateImpl(PCLCard card, PCLCard originalCard, AbstractCreature target, AbstractCreature originalTarget, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget) {
        infoIcon.setColor(EUIColors.white(isHovered() ? 1f : 0.5f));
        infoIcon.tryUpdate();
    }

    public abstract String getInfoMainDescrption();

    public abstract EUITutorialPage[] getInfoPages();

    public abstract String getInfoTitle();
}
