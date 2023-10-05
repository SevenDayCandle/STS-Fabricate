package pinacolada.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.combat.RoomTintEffect;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.actions.utility.WaitRealtimeAction;
import pinacolada.effects.card.ShowCardEffect;
import pinacolada.effects.combat.TalkEffect;
import pinacolada.effects.player.RemoveRelicEffect;
import pinacolada.effects.player.SpawnRelicEffect;
import pinacolada.effects.utility.ActionCallbackEffect;
import pinacolada.effects.utility.EffectCallbackEffect;
import pinacolada.effects.vfx.EffekseerEffect;
import pinacolada.effects.vfx.FadingParticleEffect;
import pinacolada.effects.vfx.TrailingParticleEffect;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public final class PCLEffects {
    public final static ArrayList<AbstractGameEffect> UnlistedEffects = new ArrayList<>();
    public final static PCLEffects List = new PCLEffects(EffectType.List);
    public final static PCLEffects Queue = new PCLEffects(EffectType.Queue);
    public final static PCLEffects TopLevelList = new PCLEffects(EffectType.TopLevelList);
    public final static PCLEffects TopLevelQueue = new PCLEffects(EffectType.TopLevelQueue);
    public final static PCLEffects Manual = new PCLEffects(EffectType.Manual);

    private final EffectType effectType;

    private PCLEffects(EffectType effectType) {
        this.effectType = effectType;
    }

    public static boolean isEmpty() {
        for (AbstractGameEffect effect : AbstractDungeon.topLevelEffects) {
            if (effect instanceof PCLEffect) {
                return false;
            }
        }

        return UnlistedEffects.isEmpty();
    }

    public <T extends AbstractGameEffect> T add(T effect) {
        getList().add(effect);

        return effect;
    }

    public BorderFlashEffect borderFlash(Color color) {
        return add(new BorderFlashEffect(color, true));
    }

    public BorderLongFlashEffect borderLongFlash(Color color) {
        return add(new BorderLongFlashEffect(color, true));
    }

    public EffectCallbackEffect callback(AbstractGameEffect effect) {
        return add(new EffectCallbackEffect(effect));
    }

    public EffectCallbackEffect callback(AbstractGameEffect effect, ActionT0 onCompletion) {
        return add(new EffectCallbackEffect(effect, onCompletion));
    }

    public EffectCallbackEffect callback(AbstractGameEffect effect, ActionT1<AbstractGameEffect> onCompletion) {
        return add(new EffectCallbackEffect(effect, onCompletion));
    }

    public EffectCallbackEffect callback(AbstractGameEffect effect, Object state, ActionT2<Object, AbstractGameEffect> onCompletion) {
        return add(new EffectCallbackEffect(effect, state, onCompletion));
    }

    public ActionCallbackEffect callback(ActionT0 onCompletion) {
        return add(new ActionCallbackEffect(new WaitAction(0.01f), onCompletion));
    }

    public ActionCallbackEffect callback(AbstractGameAction action) {
        return add(new ActionCallbackEffect(action));
    }

    public ActionCallbackEffect callback(AbstractGameAction effect, ActionT0 onCompletion) {
        return add(new ActionCallbackEffect(effect, onCompletion));
    }

    public ActionCallbackEffect callback(AbstractGameAction action, ActionT1<AbstractGameAction> onCompletion) {
        return add(new ActionCallbackEffect(action, onCompletion));
    }

    public ActionCallbackEffect callback(AbstractGameAction action, Object state, ActionT2<Object, AbstractGameAction> onCompletion) {
        return add(new ActionCallbackEffect(action, state, onCompletion));
    }

    public int count() {
        return getList().size();
    }

    public ArrayList<AbstractGameEffect> getList() {
        switch (effectType) {
            case List:
                return AbstractDungeon.effectList;

            case Queue:
                return AbstractDungeon.effectsQueue;

            case TopLevelList:
                return AbstractDungeon.topLevelEffects;

            case TopLevelQueue:
                return AbstractDungeon.topLevelEffectsQueue;

            case Manual:
                return UnlistedEffects;
        }

        throw new RuntimeException("Enum value does not exist.");
    }

    public FadingParticleEffect particle(Texture texture, float x, float y) {
        return add(FadingParticleEffect.obtain(texture, x, y));
    }

    public FadingParticleEffect particle(Texture texture, float x, float y, float rot, float scale) {
        return add(FadingParticleEffect.obtain(texture, x, y, rot, scale));
    }

    public EffekseerEffect playEFX(EffekseerEFK key, float x, float y) {
        return add(EffekseerEFK.efk(key, x, y));
    }

    public EffekseerEffect playEFX(EffekseerEFK key) {
        return add(EffekseerEFK.efk(key));
    }

    public RemoveRelicEffect removeRelic(AbstractRelic relic) {
        return add(new RemoveRelicEffect(relic));
    }

    public RoomTintEffect roomTint(Color color, float transparency) {
        return add(new RoomTintEffect(color.cpy(), transparency));
    }

    public RoomTintEffect roomTint(Color color, float transparency, float setDuration, boolean renderBehind) {
        return add(new RoomTintEffect(color.cpy(), transparency, setDuration, renderBehind));
    }

    public ShowCardAndObtainEffect showAndObtain(AbstractCard card) {
        return showAndObtain(card, Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f, true);
    }

    public ShowCardAndObtainEffect showAndObtain(AbstractCard card, float x, float y, boolean converge) {
        return add(new ShowCardAndObtainEffect(card, x, y, converge));
    }

    public ShowCardEffect showCardBriefly(AbstractCard card, float x, float y) {
        return add(new ShowCardEffect(card, x, y));
    }

    public ShowCardEffect showCardBriefly(AbstractCard card, float duration) {
        return add(new ShowCardEffect(card, duration));
    }

    public ShowCardEffect showCardBriefly(AbstractCard card) {
        return add(new ShowCardEffect(card));
    }

    public ShowCardEffect showCopy(AbstractCard card) {
        return showCardBriefly(card.makeStatEquivalentCopy());
    }

    public SpawnRelicEffect spawnRelic(AbstractRelic relic, float x, float y) {
        return add(new SpawnRelicEffect(relic, x, y));
    }

    public TalkEffect talk(AbstractCreature source, String message) {
        return add(new TalkEffect(source, message));
    }

    public TalkEffect talk(AbstractCreature source, String message, float duration) {
        return add(new TalkEffect(source, message, duration));
    }

    public TrailingParticleEffect trail(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y) {
        return add(TrailingParticleEffect.obtainTrail(texture, onTrail, x, y));
    }

    public TrailingParticleEffect trail(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y, float rot, float scale) {
        return add(TrailingParticleEffect.obtainTrail(texture, onTrail, x, y, rot, scale));
    }


    public ActionCallbackEffect waitRealtime(float duration) {
        return add(new ActionCallbackEffect(new WaitRealtimeAction(duration)));
    }

    public enum EffectType {
        List,
        Queue,
        TopLevelList,
        TopLevelQueue,
        Manual
    }
}