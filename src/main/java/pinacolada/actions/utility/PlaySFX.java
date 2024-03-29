package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.actions.PCLAction;
import pinacolada.effects.PCLSFX;

// Copied and modified from STS-AnimatorMod
public class PlaySFX extends PCLAction<PlaySFX> {
    private final float pitchMin;
    private final float pitchMax;
    private final float volume;
    public final String key;

    public PlaySFX(String key) {
        this(key, 1, 1, 1);
    }

    public PlaySFX(String key, float pitchMin, float pitchMax, float volume) {
        super(AbstractGameAction.ActionType.WAIT, 0.05f);

        this.key = key;
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;
        this.volume = volume;
        this.isRealtime = true;
    }

    @Override
    protected void firstUpdate() {
        play();
    }

    public void play() {
        float seconds = PCLSFX.play(key, pitchMin, pitchMax, volume);
        if (callbacks.size() > 0) {
            duration = seconds;
            startDuration = duration + 0.001f;
        }
        else {
            completeImpl();
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (tickDuration(deltaTime)) {
            complete(this);
        }
    }
}
