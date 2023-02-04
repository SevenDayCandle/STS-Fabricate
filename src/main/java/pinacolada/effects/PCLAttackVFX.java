package pinacolada.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT3;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.vfx.GenericRenderEffect;
import pinacolada.resources.PCLEnum;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

// TODO Refactor
public class PCLAttackVFX
{
    private static final HashMap<AttackEffect, PCLAttackVFX> ALL = new HashMap<>();

    // Custom:
    public static final PCLAttackVFX BITE = new PCLAttackVFX(PCLEnum.AttackEffect.BITE, null, (t, cx, cy) -> VFX.bite(cx, cy, Color.WHITE), SFX.EVENT_VAMP_BITE);
    public static final PCLAttackVFX BLUNT_HEAVY = new PCLAttackVFX(AttackEffect.BLUNT_HEAVY, ImageMaster.ATK_BLUNT_HEAVY, SFX.BLUNT_FAST);
    public static final PCLAttackVFX BLUNT_LIGHT = new PCLAttackVFX(AttackEffect.BLUNT_LIGHT, ImageMaster.ATK_BLUNT_LIGHT, SFX.BLUNT_HEAVY);
    public static final PCLAttackVFX BURN = new PCLAttackVFX(PCLEnum.AttackEffect.BURN, null, Color.RED, (t, cx, cy) -> VFX.fireBurstParticle(cx, cy), SFX.ATTACK_FIRE);
    public static final PCLAttackVFX CLAW = new PCLAttackVFX(PCLEnum.AttackEffect.CLAW, null, (t, cx, cy) -> VFX.claw(cx, cy, Color.VIOLET, Color.WHITE), SFX.ATTACK_DAGGER_5, SFX.ATTACK_DAGGER_6);
    public static final PCLAttackVFX DAGGER = new PCLAttackVFX(PCLEnum.AttackEffect.DAGGER, ImageMaster.ATK_SLASH_H, SFX.ATTACK_DAGGER_1, SFX.ATTACK_DAGGER_2);
    public static final PCLAttackVFX DARKNESS = new PCLAttackVFX(PCLEnum.AttackEffect.DARKNESS, null, Color.VIOLET, (t, cx, cy) -> VFX.darkness(cx, cy), SFX.PCL_DARKNESS);
    public static final PCLAttackVFX EARTH = new PCLAttackVFX(PCLEnum.AttackEffect.EARTH, null, Color.BROWN, (t, cx, cy) -> VFX.rockBurst(cx, cy).setDuration(1.5f, true), SFX.PCL_ORB_EARTH_CHANNEL);
    public static final PCLAttackVFX ELECTRIC = new PCLAttackVFX(PCLEnum.AttackEffect.ELECTRIC, null, Color.YELLOW, (t, cx, cy) -> VFX.electric(cx, cy), SFX.ORB_LIGHTNING_CHANNEL);
    public static final PCLAttackVFX FIRE = new PCLAttackVFX(AttackEffect.FIRE, ImageMaster.ATK_FIRE, Color.RED, SFX.ATTACK_FIRE);
    public static final PCLAttackVFX GHOST = new PCLAttackVFX(PCLEnum.AttackEffect.GHOST, null, Color.VIOLET, (t, cx, cy) -> VFX.ghost(cx, cy), SFX.ORB_DARK_CHANNEL);

    public static final PCLAttackVFX GUNSHOT = new PCLAttackVFX(PCLEnum.AttackEffect.GUNSHOT, null, (t, cx, cy) -> VFX.gunshot(cx, cy));
    public static final PCLAttackVFX ICE = new PCLAttackVFX(PCLEnum.AttackEffect.ICE, null, Color.SKY, (t, cx, cy) -> VFX.snowballImpact(cx, cy), SFX.ORB_FROST_CHANNEL);
    public static final PCLAttackVFX LIGHTNING = new PCLAttackVFX(AttackEffect.LIGHTNING, null, Color.YELLOW, (t, cx, cy) -> VFX.lightning(cx, cy), SFX.ORB_LIGHTNING_EVOKE);
    public static final PCLAttackVFX POISON = new PCLAttackVFX(AttackEffect.POISON, ImageMaster.ATK_POISON, Color.CHARTREUSE, SFX.ATTACK_POISON, SFX.ATTACK_POISON2);
    public static final PCLAttackVFX PSYCHOKINESIS = new PCLAttackVFX(PCLEnum.AttackEffect.PSYCHOKINESIS, null, Color.MAGENTA, (t, cx, cy) -> VFX.psychokinesis(cx, cy), SFX.PCL_PSI);
    public static final PCLAttackVFX PUNCH = new PCLAttackVFX(PCLEnum.AttackEffect.PUNCH, null, null, 0.6f, (t, cx, cy) -> VFX.strongPunch(cx, cy), SFX.RAGE);
    public static final PCLAttackVFX SHIELD = new PCLAttackVFX(AttackEffect.SHIELD, ImageMaster.ATK_SHIELD, (t, cx, cy) -> VFX.shield(cx, cy));
    public static final PCLAttackVFX SLASH_DIAGONAL = new PCLAttackVFX(AttackEffect.SLASH_DIAGONAL, ImageMaster.ATK_SLASH_D, SFX.ATTACK_FAST);
    public static final PCLAttackVFX SLASH_HEAVY = new PCLAttackVFX(AttackEffect.SLASH_HEAVY, ImageMaster.ATK_SLASH_HEAVY, SFX.ATTACK_HEAVY);
    public static final PCLAttackVFX SLASH_HORIZONTAL = new PCLAttackVFX(AttackEffect.SLASH_HORIZONTAL, ImageMaster.ATK_SLASH_H, SFX.ATTACK_FAST);
    public static final PCLAttackVFX SLASH_VERTICAL = new PCLAttackVFX(AttackEffect.SLASH_VERTICAL, ImageMaster.ATK_SLASH_V, SFX.ATTACK_FAST);
    public static final PCLAttackVFX SMALL_EXPLOSION = new PCLAttackVFX(PCLEnum.AttackEffect.SMALL_EXPLOSION, null, Color.SCARLET, (t, cx, cy) -> VFX.smallExplosion(cx, cy), SFX.ATTACK_FLAME_BARRIER);
    public static final PCLAttackVFX SMASH = new PCLAttackVFX(AttackEffect.SMASH, null, (t, cx, cy) -> VFX.whack(cx, cy), SFX.BLUNT_FAST);
    public static final PCLAttackVFX SPARK = new PCLAttackVFX(PCLEnum.AttackEffect.SPARK, null, Color.YELLOW, (t, cx, cy) -> VFX.sparkImpact(cx, cy), SFX.ORB_LIGHTNING_CHANNEL);
    public static final PCLAttackVFX WATER = new PCLAttackVFX(PCLEnum.AttackEffect.WATER, null, Color.BLUE, (t, cx, cy) -> VFX.water(cx, cy), SFX.PCL_ORB_WATER_EVOKE);
    public static final PCLAttackVFX WIND = new PCLAttackVFX(PCLEnum.AttackEffect.WIND, null, Color.FOREST, (t, cx, cy) -> VFX.tornado(cx, cy), SFX.POWER_FLIGHT);

    public final AttackEffect key;
    public final TextureRegion texture;
    public final float damageDelay;
    public final Color damageTint;
    protected final String[] sounds;
    protected final FuncT3<PCLEffect, AbstractCreature, Float, Float> createVFX;

    public PCLAttackVFX(AttackEffect key)
    {
        this(key, null);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, String... sfx)
    {
        this(key, texture, null, 0, null, sfx);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, Color damageTint, String... sfx)
    {
        this(key, texture, damageTint, 0, null, sfx);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, float damageDelay, String... sfx)
    {
        this(key, texture, null, damageDelay, null, sfx);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, FuncT3<PCLEffect, AbstractCreature, Float, Float> createVFX, String... sfx)
    {
        this(key, texture, null, 0, createVFX, sfx);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, Color damageTint, FuncT3<PCLEffect, AbstractCreature, Float, Float> createVFX, String... sfx)
    {
        this(key, texture, damageTint, 0, createVFX, sfx);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, Color damageTint, float damageDelay, FuncT3<PCLEffect, AbstractCreature, Float, Float> createVFX, String... sfx)
    {
        ALL.put(key, this);
        this.key = key;
        this.texture = texture;
        this.damageTint = damageTint;
        this.damageDelay = damageDelay;
        this.createVFX = createVFX;
        this.sounds = sfx;
    }

    public static PCLAttackVFX get(AttackEffect key)
    {
        return ALL.get(key);
    }

    public static List<AttackEffect> keys()
    {
        return ALL.keySet().stream().sorted((a, b) -> StringUtils.compare(a.name(), b.name())).collect(Collectors.toList());
    }

    public String getSound()
    {
        return sounds.length == 0 ? null : sounds.length == 1 ? sounds[0] : EUIUtils.random(sounds);
    }

    public PCLEffect getVFX(AbstractCreature source, float t_cX, float t_cY)
    {
        if (createVFX != null)
        {
            return createVFX.invoke(source, t_cX, t_cY);
        }

        if (texture != null)
        {
            return new GenericRenderEffect(texture, t_cX, t_cY)
                    .setRotation(key == AttackEffect.BLUNT_HEAVY ? MathUtils.random(0, 360) : MathUtils.random(-12f, 12f));
        }

        return new GenericRenderEffect((TextureRegion) null, t_cX, t_cY);
    }

    public void playSound(float pitchMin, float pitchMax)
    {
        SFX.play(getSound(), pitchMin, pitchMax);
    }
}
