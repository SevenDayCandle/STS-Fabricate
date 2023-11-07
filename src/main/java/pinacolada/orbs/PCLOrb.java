package pinacolada.orbs;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIColors;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.vfx.OrbEvokeParticle;
import pinacolada.effects.vfx.OrbFlareNotActuallyNeedingOrbEffect;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public abstract class PCLOrb extends AbstractOrb implements KeywordProvider {
    public static final int IMAGE_SIZE = 96;
    protected float rotationSpeed;
    protected OrbStrings orbStrings;
    public final ArrayList<EUIKeywordTooltip> tooltips = new ArrayList<>();
    public final PCLOrbData data;
    public DelayTiming timing;
    public EUIKeywordTooltip mainTip;

    public PCLOrb(PCLOrbData data) {
        this.data = data;
        this.ID = data.ID;
        setup();
        setupProperties();
        setupImages();
        setupDescription();
    }

    public static String createFullID(Class<? extends PCLOrb> type) {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLOrb> type) {
        return resources.createID(type.getSimpleName());
    }

    public static int getFocus() {
        return GameUtilities.getPowerAmount(AbstractDungeon.player, FocusPower.POWER_ID);
    }

    protected static PCLOrbData register(Class<? extends AbstractOrb> type) {
        return register(type, PGR.core);
    }

    protected static PCLOrbData register(Class<? extends AbstractOrb> type, PCLResources<?, ?, ?, ?> resources) {
        return registerPowerData(new PCLOrbData(type, resources));
    }

    protected static <T extends PCLOrbData> T registerPowerData(T cardData) {
        return PCLOrbData.registerPCLData(cardData);
    }

    @Override
    public void applyFocus() {
        int focus = getFocus();
        if (data.applyFocusToPassive) {
            this.passiveAmount = Math.max(0, this.basePassiveAmount + focus);
        }
        if (data.applyFocusToEvoke) {
            this.evokeAmount = Math.max(0, this.baseEvokeAmount + focus);
        }
        CombatManager.onOrbApplyFocus(this);
    }

    protected String formatDescription(int index, Object... args) {
        if (orbStrings.DESCRIPTION == null || orbStrings.DESCRIPTION.length <= index) {
            EUIUtils.logError(this, "orbStrings.Description does not exist, " + this.name);
            return "";
        }
        return EUIUtils.format(orbStrings.DESCRIPTION[index], args);
    }

    public int getBaseEvokeAmount() {
        return this.baseEvokeAmount;
    }

    public int getBasePassiveAmount() {
        return this.basePassiveAmount;
    }

    protected Color getColor1() {
        return data.flareColor1;
    }

    protected Color getColor2() {
        return data.flareColor2;
    }

    protected OrbFlareNotActuallyNeedingOrbEffect getOrbFlareEffect() {
        return new OrbFlareNotActuallyNeedingOrbEffect(this.cX, this.cY).setColors(getColor1(), getColor2());
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tooltips;
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return mainTip;
    }

    public String getUpdatedDescription() {
        return formatDescription(0);
    }

    public void loadImage(String path) {
        Texture t = EUIRM.getTexture(path, true, false);
        if (t == null) {
            path = PCLCoreImages.CardAffinity.unknown.path();
            t = EUIRM.getTexture(path, true, false);
        }
        this.img = t;
    }

    @Override
    public AbstractOrb makeCopy() {
        try {
            return getClass().getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void onChannel() {
    }

    public void onClick() {

    }

    @Override
    public void onEvoke() {
        PCLActions.bottom.playVFX(getOrbFlareEffect(), Settings.FAST_MODE ? 0 : (0.6F / (float) AbstractDungeon.player.orbs.size()));
    }

    public void passive() {
        PCLActions.bottom.playVFX(getOrbFlareEffect(), Settings.FAST_MODE ? 0 : (0.6F / (float) AbstractDungeon.player.orbs.size()));
        CombatManager.onOrbPassiveEffect(this);
    }

    public void setBaseEvokeAmount(int amount, boolean relative) {
        this.baseEvokeAmount = relative ? this.baseEvokeAmount + amount : amount;
        applyFocus();
        this.updateDescription();
    }

    public void setBasePassiveAmount(int amount, boolean relative) {
        this.basePassiveAmount = relative ? this.basePassiveAmount + amount : amount;
        applyFocus();
        this.updateDescription();
    }

    public void setup() {

    }

    protected void setupDescription() {
        this.name = orbStrings.NAME;
        String desc = getUpdatedDescription();
        mainTip = new EUIKeywordTooltip(name, desc);
        mainTip.icon = img != null ? new TextureRegion(img) : null;
        tooltips.add(mainTip);
        // Should not contain the tooltip associated with this orb
        if (data.tooltip != null) {
            tooltips.add(data.tooltip);
            EUITooltip.scanForTips(desc, tooltips);
            tooltips.remove(data.tooltip);
        }
        else {
            EUITooltip.scanForTips(desc, tooltips);
        }
        // Base game descriptions don't support special characters. Just gonna reuse PCLPower's here
        this.description = PCLPower.sanitizePowerDescription(desc);
    }

    protected void setupImages() {
        loadImage(data.imagePath);
    }

    public void setupProperties() {
        this.baseEvokeAmount = this.evokeAmount = data.baseEvokeValue;
        this.basePassiveAmount = this.passiveAmount = data.basePassiveValue;
        this.timing = data.timing;
        this.orbStrings = data.strings;
        this.showEvokeValue = data.showEvokeValue;
        this.rotationSpeed = data.rotationSpeed;
    }

    @Override
    public void triggerEvokeAnimation() {
        for (int i = 0; i < 4; i++) {
            PCLEffects.Queue.add(new OrbEvokeParticle(this.cX, this.cY, EUIColors.lerpNew(getColor1(), getColor2(), MathUtils.random(0, 0.5f))));
        }
    }

    @Override
    public void update() {
        hb.update();
        if (hb.hovered) {
            EUITooltip.queueTooltips(tooltips, InputHelper.mX + hb.width, InputHelper.mY + (hb.height * 0.5f));
        }
        this.fontScale = MathHelper.scaleLerpSnap(this.fontScale, 0.7F);
        this.angle += EUI.delta() * rotationSpeed;

        if (InputHelper.justClickedLeft) {
            hb.clickStarted = true;
        }
        else if (hb.clicked) {
            hb.clicked = false;
            onClick();
        }
    }

    @Override
    public void updateDescription() {
        this.applyFocus();
        this.description = getUpdatedDescription();
        mainTip.setDescription(this.description);
    }
}
