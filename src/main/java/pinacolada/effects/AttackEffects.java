package pinacolada.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.utilities.EUIColors;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.vfx.GenericRenderEffect;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class AttackEffects
{
    private static final HashMap<AttackEffect, AttackEffectData> map = new HashMap<>();
    private static final ArrayList<AttackEffect> melee = new ArrayList<>();
    private static final ArrayList<AttackEffect> magic = new ArrayList<>();
    private static final ArrayList<AttackEffect> other = new ArrayList<>();
    private static final PCLCoreImages.Effects IMAGES = PGR.core.images.effects;

    public static final AttackEffect BLUNT_HEAVY = AttackEffect.BLUNT_HEAVY;
    public static final AttackEffect BLUNT_LIGHT = AttackEffect.BLUNT_LIGHT;
    public static final AttackEffect FIRE = AttackEffect.FIRE;
    public static final AttackEffect LIGHTNING = AttackEffect.LIGHTNING;
    public static final AttackEffect NONE = AttackEffect.NONE;
    public static final AttackEffect POISON = AttackEffect.POISON;
    public static final AttackEffect SHIELD = AttackEffect.SHIELD;
    public static final AttackEffect SLASH_DIAGONAL = AttackEffect.SLASH_DIAGONAL;
    public static final AttackEffect SLASH_HEAVY = AttackEffect.SLASH_HEAVY;
    public static final AttackEffect SLASH_HORIZONTAL = AttackEffect.SLASH_HORIZONTAL;
    public static final AttackEffect SLASH_VERTICAL = AttackEffect.SLASH_VERTICAL;
    public static final AttackEffect SMASH = AttackEffect.SMASH;
    // Custom:
    public static final AttackEffect BITE = PCLEnum.AttackEffect.BITE;
    public static final AttackEffect BURN = PCLEnum.AttackEffect.BURN;
    public static final AttackEffect CLAW = PCLEnum.AttackEffect.CLAW;
    public static final AttackEffect DAGGER = PCLEnum.AttackEffect.DAGGER;
    public static final AttackEffect DARK = PCLEnum.AttackEffect.DARK;
    public static final AttackEffect DARKNESS = PCLEnum.AttackEffect.DARKNESS;
    public static final AttackEffect EARTH = PCLEnum.AttackEffect.EARTH;
    public static final AttackEffect ELECTRIC = PCLEnum.AttackEffect.ELECTRIC;
    public static final AttackEffect GUNSHOT = PCLEnum.AttackEffect.GUNSHOT;
    public static final AttackEffect ICE = PCLEnum.AttackEffect.ICE;
    public static final AttackEffect PSYCHOKINESIS = PCLEnum.AttackEffect.PSYCHOKINESIS;
    public static final AttackEffect PUNCH = PCLEnum.AttackEffect.PUNCH;
    public static final AttackEffect SMALL_EXPLOSION = PCLEnum.AttackEffect.SMALL_EXPLOSION;
    public static final AttackEffect SPARK = PCLEnum.AttackEffect.SPARK;
    public static final AttackEffect WATER = PCLEnum.AttackEffect.WATER;
    public static final AttackEffect WAVE = PCLEnum.AttackEffect.WAVE;
    public static final AttackEffect WIND = PCLEnum.AttackEffect.WIND;

    protected static AttackEffectData add(ArrayList<AttackEffect> category, AttackEffect effect)
    {
        return add(category, effect, null);
    }

    protected static AttackEffectData add(ArrayList<AttackEffect> category, AttackEffect effect, TextureRegion texture)
    {
        AttackEffectData data = new AttackEffectData();
        data.texture = texture;
        category.add(effect);
        map.put(effect, data);
        return data;
    }

    public static float getDamageDelay(AttackEffect attackEffect)
    {
        return attackEffect == NONE ? 0 : map.get(attackEffect).damageDelay;
    }

    public static Color getDamageTint(AttackEffect effect)
    {
        return effect == NONE ? null : map.get(effect).damageTint;
    }

    public static String getSoundKey(AttackEffect effect)
    {
        return effect == NONE ? null : map.get(effect).getSound();
    }

    public static TextureRegion getTextureRegion(AttackEffect effect)
    {
        return effect == NONE ? null : map.get(effect).getTexture();
    }

    public static PCLEffect getVFX(AttackEffect effect, AbstractCreature source, float t_cX, float t_cY)
    {
        if (effect != null && effect != NONE)
        {
            final AttackEffectData data = map.get(effect);
            if (data.createVFX2 != null)
            {
                return data.createVFX2.invoke(source, t_cX, t_cY);
            }
            if (data.createVFX != null)
            {
                return data.createVFX.invoke(t_cX, t_cY);
            }

            final TextureRegion region = data.getTexture();
            if (region != null)
            {
                return new GenericRenderEffect(region, t_cX, t_cY)
                        .setRotation(effect == BLUNT_HEAVY ? MathUtils.random(0, 360) : MathUtils.random(-12f, 12f));
            }
        }

        return new GenericRenderEffect((TextureRegion) null, t_cX, t_cY);
    }

    public static void initialize()
    {
        add(melee, BLUNT_LIGHT, ImageMaster.ATK_BLUNT_LIGHT)
                .setSFX(SFX.BLUNT_FAST);

        add(melee, BLUNT_HEAVY, ImageMaster.ATK_BLUNT_HEAVY)
                .setSFX(SFX.BLUNT_HEAVY);

        add(melee, SLASH_DIAGONAL, ImageMaster.ATK_SLASH_D)
                .setSFX(SFX.ATTACK_FAST);

        add(melee, SLASH_HEAVY, ImageMaster.ATK_SLASH_HEAVY)
                .setSFX(SFX.ATTACK_HEAVY);

        add(melee, SLASH_HORIZONTAL, ImageMaster.ATK_SLASH_H)
                .setSFX(SFX.ATTACK_FAST);

        add(melee, SLASH_VERTICAL, ImageMaster.ATK_SLASH_V)
                .setSFX(SFX.ATTACK_FAST);

        add(melee, SMASH)
                .setVFX(VFX::whack)
                .setSFX(SFX.BLUNT_FAST);

        add(magic, FIRE, ImageMaster.ATK_FIRE)
                .setSFX(SFX.ATTACK_FIRE)
                .setDamageTint(Color.RED);

        add(magic, SMALL_EXPLOSION)
                .setVFX(VFX::smallExplosion)
                .setSFX(SFX.ATTACK_FLAME_BARRIER)
                .setDamageTint(Color.SCARLET);

        add(magic, POISON, ImageMaster.ATK_POISON)
                .setSFX(SFX.ATTACK_POISON, SFX.ATTACK_POISON2)
                .setDamageTint(Color.CHARTREUSE);

        add(magic, ICE)
                .setVFX(VFX::snowballImpact)
                .setSFX(SFX.ORB_FROST_CHANNEL)
                .setDamageTint(Color.SKY);

        add(magic, DARKNESS)
                .setVFX(VFX::darkness)
                .setSFX(SFX.PCL_DARKNESS)
                .setDamageTint(Color.VIOLET);

        add(magic, PSYCHOKINESIS)
                .setVFX(VFX::psychokinesis)
                .setSFX(SFX.PCL_PSI)
                .setDamageTint(Color.PINK);

        add(other, SHIELD, ImageMaster.ATK_SHIELD)
                .setVFX(VFX::shield)
                .setSFX(SFX.BLOCK_GAIN_1, SFX.BLOCK_GAIN_2, SFX.BLOCK_GAIN_3);

        add(magic, SPARK)
                .setVFX(VFX::sparkImpact)
                .setSFX(SFX.ORB_LIGHTNING_CHANNEL)
                .setDamageTint(EUIColors.lerp(Color.YELLOW, Color.WHITE, 0.3f));

        add(magic, ELECTRIC)
                .setVFX(VFX::electric)
                .setSFX(SFX.ORB_LIGHTNING_CHANNEL)
                .setDamageTint(EUIColors.lerp(Color.YELLOW, Color.WHITE, 0.3f));

        add(magic, LIGHTNING)
                .setVFX(VFX::lightning)
                .setSFX(SFX.ORB_LIGHTNING_EVOKE)
                .setDamageTint(EUIColors.lerp(Color.YELLOW, Color.WHITE, 0.3f));

        add(other, GUNSHOT)
                .setVFX(VFX::gunshot);

        add(melee, DAGGER, ImageMaster.ATK_SLASH_H)
                .setSFX(SFX.ATTACK_DAGGER_1, SFX.ATTACK_DAGGER_2);

        add(melee, PUNCH)
                .setVFX(VFX::strongPunch)
                .setSFX(SFX.RAGE)
                .setDamageDelay(0.6f);

        add(melee, CLAW)
                .setVFX((cx, cy) -> VFX.claw(cx, cy, Color.VIOLET, Color.WHITE))
                .setSFX(SFX.ATTACK_DAGGER_5, SFX.ATTACK_DAGGER_6);

        add(melee, BITE)
                .setVFX((cx, cy) -> VFX.bite(cx, cy, Color.WHITE))
                .setSFX(SFX.EVENT_VAMP_BITE);

        add(magic, EARTH)
                .setVFX((cx, cy) -> VFX.rockBurst(cx, cy).setDuration(1.5f, true))
                .setSFX(SFX.PCL_ORB_EARTH_CHANNEL)
                .setDamageTint(Color.BROWN);

        add(magic, BURN)
                .setVFX(VFX::fireBurstParticle)
                .setSFX(SFX.ATTACK_FIRE)
                .setDamageTint(Color.RED);

        add(magic, DARK)
                .setVFX(VFX::dark)
                .setSFX(SFX.ORB_DARK_CHANNEL)
                .setDamageTint(Color.VIOLET);

        add(magic, WATER)
                .setVFX(VFX::water)
                .setSFX(SFX.PCL_ORB_WATER_EVOKE)
                .setDamageTint(Color.BLUE);

        add(magic, WAVE)
                .setVFX(VFX::circularWave)
                .setSFX(SFX.PCL_PING)
                .setDamageTint(Color.LIME);

        add(magic, WIND)
                .setVFX(VFX::tornado)
                .setSFX(SFX.POWER_FLIGHT)
                .setDamageTint(Color.LIME);
    }

    public static List<AttackEffect> keys()
    {
        return map.keySet().stream().sorted((a, b) -> StringUtils.compare(a.name(), b.name())).collect(Collectors.toList());
    }

    public static void playSound(AttackEffect effect, float pitchMin, float pitchMax)
    {
        SFX.play(getSoundKey(effect), pitchMin, pitchMax);
    }

    public static AttackEffect randomMagic()
    {
        return EUIUtils.random(magic);
    }

    public static AttackEffect randomMelee()
    {
        return EUIUtils.random(melee);
    }

    protected static class AttackEffectData
    {
        private String[] sounds;
        private FuncT2<PCLEffect, Float, Float> createVFX;
        private FuncT3<PCLEffect, AbstractCreature, Float, Float> createVFX2;
        private TextureRegion texture;
        private float damageDelay;
        private Color damageTint;

        protected String getSound()
        {
            return sounds.length == 0 ? null : sounds.length == 1 ? sounds[0] : EUIUtils.random(sounds);
        }

        protected TextureRegion getTexture()
        {
            return texture;
        }

        protected AttackEffectData setDamageDelay(float delay)
        {
            this.damageDelay = delay;

            return this;
        }

        protected AttackEffectData setDamageTint(Color color)
        {
            this.damageTint = color.cpy();

            return this;
        }

        protected AttackEffectData setSFX(String... sounds)
        {
            this.sounds = sounds;

            return this;
        }

        protected AttackEffectData setTexture(TextureRegion texture)
        {
            this.texture = texture;

            return this;
        }

        protected AttackEffectData setVFX(FuncT2<PCLEffect, Float, Float> createVFX)
        {
            this.createVFX = createVFX;

            return this;
        }

        protected AttackEffectData setVFX2(FuncT3<PCLEffect, AbstractCreature, Float, Float> createVFX2)
        {
            this.createVFX2 = createVFX2;

            return this;
        }
    }
}
