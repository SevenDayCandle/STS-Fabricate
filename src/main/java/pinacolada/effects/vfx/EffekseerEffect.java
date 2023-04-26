package pinacolada.effects.vfx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import extendedui.STSEffekseerManager;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.SFX;

public class EffekseerEffect extends PCLEffect {
    protected Vector2 position;
    protected Vector3 rotation;
    protected String sfxKey;
    protected EffekseerEFK vfxKey;
    protected float pitchMin = 1f;
    protected float pitchMax = 1f;
    protected float soundDelay = 0.5f;
    protected float volume = 1f;
    protected Integer handle;
    protected Vector3 scale;
    protected int forceEnd = -20;
    protected boolean hasPlayed;


    public EffekseerEffect(EffekseerEFK key, float x, float y) {
        super(0.5f, false);
        this.vfxKey = key;
        this.position = new Vector2(x, y);
    }

    public EffekseerEffect setForceEnd(int forceEnd) {
        this.forceEnd = forceEnd;
        return this;
    }

    public EffekseerEffect setPosition(float x, float y) {
        this.position = new Vector2(x, y);
        return this;
    }

    public EffekseerEffect setRotation(Vector3 rotation) {
        this.rotation = rotation;
        return this;
    }

    public EffekseerEffect setRotation(float x, float y, float z) {
        this.rotation = new Vector3(x, y, z);
        return this;
    }

    public EffekseerEffect setRotationDeg(float x, float y, float z) {
        this.rotation = new Vector3(x * MathUtils.degRad, y * MathUtils.degRad, z * MathUtils.degRad);
        return this;
    }

    public EffekseerEffect setScale(float scale) {
        this.scale = new Vector3(scale, scale, scale);
        return this;
    }

    public void dispose() {
        super.dispose();
        STSEffekseerManager.stop(handle);
    }

    @Override
    protected void firstUpdate() {
        handle = STSEffekseerManager.play(vfxKey.path, position, rotation, scale, color);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        this.duration -= deltaTime;
        this.soundDelay -= deltaTime;
        if (!hasPlayed && this.soundDelay < 0 && sfxKey != null) {
            SFX.play(sfxKey, pitchMin, pitchMax, volume);
            hasPlayed = true;
        }
        if (handle == null || !STSEffekseerManager.exists(handle)) {
            complete();
        }
        else if (duration < forceEnd) {
            STSEffekseerManager.stop(handle);
            complete();
        }
    }

    public EffekseerEffect setScale(Vector3 scale) {
        this.scale = scale;
        return this;
    }

    public EffekseerEffect setSoundKey(String sfxKey) {
        return setSoundKey(sfxKey, 1f, 1f, 1f, duration);
    }

    public EffekseerEffect setSoundKey(String sfxKey, float pitchMin, float pitchMax, float volume, float delay) {
        this.sfxKey = sfxKey;
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;
        this.volume = volume;
        this.soundDelay = delay;
        return this;
    }

    public EffekseerEffect setSoundKey(String sfxKey, float delay) {
        return setSoundKey(sfxKey, 1f, 1f, 1f, delay);
    }
}
