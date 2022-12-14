package pinacolada.actions.special;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.effects.SFX;

public class PlaySFX extends PCLActionWithCallback<PlaySFX>
{
    public final String key;

    private float pitchMin;
    private float pitchMax;
    private float volume;

    public PlaySFX(String key)
    {
        this(key, 1, 1, 1);
    }

    public PlaySFX(String key, float pitchMin, float pitchMax, float volume)
    {
        super(AbstractGameAction.ActionType.WAIT, 0.05f);

        this.key = key;
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;
        this.volume = volume;
        this.isRealtime = true;
    }

    @Override
    protected void firstUpdate()
    {
        play();
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(this);
        }
    }

    public void play()
    {
        float seconds = SFX.play(key, pitchMin, pitchMax, volume);
        if (callbacks.size() > 0)
        {
            duration = seconds;
            startDuration = duration + 0.001f;
        }
        else
        {
            complete();
        }
    }
}
