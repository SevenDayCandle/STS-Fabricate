package pinacolada.stances;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.StanceStrings;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import extendedui.EUI;
import extendedui.EUIUtils;
import pinacolada.effects.stance.StanceAura;
import pinacolada.effects.stance.StanceParticleVertical;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameEffects;

import java.util.Objects;

public abstract class PCLStance extends AbstractStance
{
    public static final String STANCE_ID = PGR.core.createID(PCLStance.class.getSimpleName());

    protected static final int EXIT_GAIN = 4;
    protected static long sfxId = -1L;
    protected final StanceStrings strings;
    public final PCLStanceHelper helper;

    protected PCLStance(PCLStanceHelper helper)
    {
        this.ID = helper.ID;
        this.strings = PGR.getStanceString(helper.ID);
        this.name = strings.NAME;
        this.helper = helper;

        updateDescription();
    }

    protected static Color createColor(float r1, float r2, float g1, float g2, float b1, float b2)
    {
        return new Color(MathUtils.random(r1, r2), MathUtils.random(g1, g2), MathUtils.random(b1, b2), 0);
    }

    public static String createFullID(Class<? extends PCLStance> type)
    {
        return PGR.core.createID(type.getSimpleName());
    }

    protected String formatDescription(Object... args)
    {
        return EUIUtils.format(strings.DESCRIPTION[0], args);
    }

    protected abstract Color getAuraColor();

    protected abstract Color getMainColor();

    protected abstract Color getParticleColor();

    protected void queueAura()
    {
        GameEffects.Queue.add(new StanceAura(getAuraColor()));
    }

    protected void queueParticle()
    {
        GameEffects.Queue.add(new StanceParticleVertical(getParticleColor()));
    }

    protected boolean tryApplyStance(String stanceID)
    {
        String current = CombatStats.getCombatData(PCLStance.class.getSimpleName(), null);
        if (Objects.equals(stanceID, current))
        {
            return false;
        }

        CombatStats.setCombatData(PCLStance.class.getSimpleName(), stanceID);
        return true;
    }

    public void onRefreshStance()
    {

    }

    @Override
    public void updateDescription()
    {
        final StanceStrings ms = PGR.getStanceString(STANCE_ID);
        description = EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, ms.DESCRIPTION);
    }

    @Override
    public void onEnterStance()
    {
        super.onEnterStance();

        if (sfxId != -1L)
        {
            this.stopIdleSfx();
        }

        CardCrawlGame.sound.play("STANCE_ENTER_CALM");
        sfxId = CardCrawlGame.sound.playAndLoop("STANCE_LOOP_CALM");
        GameEffects.Queue.add(new BorderFlashEffect(getMainColor(), true));
    }

    @Override
    public void onExitStance()
    {
        super.onExitStance();

        this.stopIdleSfx();
    }

    @Override
    public void updateAnimation()
    {
        if (!Settings.DISABLE_EFFECTS)
        {
            this.particleTimer -= EUI.delta();
            if (this.particleTimer < 0f)
            {
                this.particleTimer = 0.04f;
                queueParticle();
            }
        }

        this.particleTimer2 -= EUI.delta();
        if (this.particleTimer2 < 0f)
        {
            this.particleTimer2 = MathUtils.random(0.45f, 0.55f);
            queueAura();
        }
    }

    @Override
    public void stopIdleSfx()
    {
        if (sfxId != -1L)
        {
            CardCrawlGame.sound.stop("STANCE_LOOP_CALM", sfxId);
            sfxId = -1L;
        }
    }
}
