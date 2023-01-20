package pinacolada.actions.special;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class SelectCreature extends PCLActionWithCallback<AbstractCreature>
{
    protected ActionT1<AbstractCreature> onHovering;
    protected AbstractCreature previous;
    protected AbstractCreature target;
    protected PCLCardTarget targeting;
    protected boolean autoSelect;
    protected boolean skipConfirmation;
    protected boolean cancellable;

    private final Vector2[] points = new Vector2[20];
    private final Vector2 controlPoint = new Vector2();
    private final Vector2 origin = new Vector2();
    private float arrowScaleTimer;

    public SelectCreature(AbstractCreature target)
    {
        super(ActionType.SPECIAL);

        this.card = null;
        this.target = target;
        this.cancellable = true;

        initialize(1);
    }

    public SelectCreature(PCLCardTarget targeting, String sourceName)
    {
        super(ActionType.SPECIAL);

        this.card = null;
        this.targeting = targeting;
        this.cancellable = true;

        initialize(1, sourceName);
    }

    public SelectCreature(AbstractCard card)
    {
        super(ActionType.SPECIAL);

        this.card = card;
        this.cancellable = true;

        PCLCard c = EUIUtils.safeCast(card, PCLCard.class);
        if (c != null && c.pclTarget != null)
        {
            targeting = c.pclTarget;
        }
        else
        {
            switch (card.target)
            {
                case ENEMY:
                    targeting = PCLCardTarget.Single;
                    break;
                case SELF_AND_ENEMY:
                    targeting = PCLCardTarget.Any;
                case ALL:
                case ALL_ENEMY:
                    targeting = PCLCardTarget.AllEnemy;
                    break;
                case SELF:
                    targeting = PCLCardTarget.Self;
                    break;
                case NONE:
                    targeting = PCLCardTarget.None;
                    break;
            }
        }

        initialize(1, card.name);
    }

    public SelectCreature autoSelectSingleTarget(boolean autoSelectSingleTarget)
    {
        this.autoSelect = autoSelectSingleTarget;

        return this;
    }

    public SelectCreature skipConfirmation(boolean skipConfirmation)
    {
        this.skipConfirmation = skipConfirmation;

        return this;
    }

    public SelectCreature isCancellable(boolean cancellable)
    {
        this.cancellable = cancellable;

        return this;
    }

    public SelectCreature setOnHovering(ActionT1<AbstractCreature> onHovering)
    {
        this.onHovering = onHovering;

        return this;
    }

    public SelectCreature setMessage(String message)
    {
        this.message = message;

        return this;
    }

    public SelectCreature setMessage(String format, Object... args)
    {
        this.message = EUIUtils.format(format, args);

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (target != null)
        {
            complete(target);
            return;
        }

        final ArrayList<AbstractMonster> enemies = GameUtilities.getEnemies(true);
        if (enemies.size() == 0 && targeting == PCLCardTarget.Single)
        {
            complete(null);
            return;
        }

        if (autoSelect)
        {
            if (targeting == PCLCardTarget.Single && enemies.size() == 1)
            {
                target = enemies.get(0);
                if (card != null)
                {
                    card.calculateCardDamage((AbstractMonster) target);
                }
            }
            else if (targeting == PCLCardTarget.Self)
            {
                target = player;
                if (card != null)
                {
                    card.applyPowers();
                }
            }

            if (target != null)
            {
                complete(target);
                return;
            }
        }

        if (skipConfirmation)
        {
            switch (targeting)
            {
                case Self:
                    complete(player);
                    return;

                case RandomEnemy:
                    complete(GameUtilities.getRandomEnemy(true));
                    return;

                case AllEnemy:
                case None:
                    complete(null);
                    return;
            }
        }

        for (int i = 0; i < this.points.length; ++i)
        {
            this.points[i] = new Vector2();
        }

        super.firstUpdate();
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        GameCursor.hidden = true;

        if (InputHelper.justClickedRight && cancellable)
        {
            if (card != null)
            {
                card.applyPowers();
            }

            complete();
            return;
        }

        switch (targeting)
        {
            case Self:
                updateTarget(true, false);
                break;
            case Single:
                updateTarget(false, true);
                break;
            case Any:
                updateTarget(true, true);
                break;
        }

        if (InputHelper.justClickedLeft || InputHelper.justReleasedClickLeft)
        {
            InputHelper.justClickedLeft = false;
            InputHelper.justReleasedClickLeft = false;
            switch (targeting)
            {
                case RandomEnemy:
                    complete(target = GameUtilities.getRandomEnemy(true));
                    return;

                case AllEnemy:
                case None:
                    complete(null);
                    return;

                case Self:
                case Single:
                case Any:
                    if (target != null)
                    {
                        complete(target);
                        return;
                    }
            }
        }

        EUI.addPostRender(this::render);
    }

    @Override
    protected void complete()
    {
        GameCursor.hidden = false;
        super.complete();
    }

    protected void updateTarget(boolean targetPlayer, boolean targetEnemy)
    {
        if (target != null)
        {
            previous = target;
            target = null;
        }

        if (targetPlayer && (player.hb.hovered && !player.isDying))
        {
            target = player;
        }
        else if (targetEnemy)
        {
            for (AbstractMonster m : GameUtilities.getEnemies(true))
            {
                if (m.hb.hovered && !m.isDying)
                {
                    target = m;
                    break;
                }
            }
        }

        if ((card != null) && (target != null) && (target != previous))
        {
            if (target instanceof AbstractMonster)
            {
                card.calculateCardDamage((AbstractMonster) target);
            }
            else
            {
                card.applyPowers();
            }
        }

        if (onHovering != null)
        {
            onHovering.invoke(target);
        }
    }

    protected void render(SpriteBatch sb)
    {
        switch (targeting)
        {
            case Self:
            case Single:
            case Any:
                renderArrow(sb);
                if (target != null)
                {
                    target.renderReticle(sb);
                }
                break;

            case RandomEnemy:
            case AllEnemy:
                for (AbstractCreature c : GameUtilities.getAllCharacters(true))
                {
                    c.renderReticle(sb);
                }
                break;

            case None:
                break;
        }

        final String message = updateMessage();
        if (message.length() > 0)
        {
            FontHelper.renderDeckViewTip(sb, message, Settings.scale * 96f, Settings.CREAM_COLOR);
        }
    }

    protected void renderArrow(SpriteBatch sb)
    {
        float x = (float) InputHelper.mX;
        float y = (float) InputHelper.mY;

        if (card != null)
        {
            origin.x = card.current_x;
            origin.y = card.current_y;

            controlPoint.x = card.current_x - ((x - card.current_x) / 4f);
            controlPoint.y = card.current_y + ((y - card.current_y - 40f * Settings.scale) / 2f);
        }
        else
        {
            origin.x = player.dialogX;
            origin.y = player.dialogY - 40f * Settings.scale;

            controlPoint.x = player.animX - (x - player.animX) / 4f;
            controlPoint.y = player.animY + (y - player.animY - 40f * Settings.scale) / 2f;
        }

        float arrowScale;
        if (target == null)
        {
            arrowScale = Settings.scale;
            arrowScaleTimer = 0f;
            sb.setColor(new Color(1f, 1f, 1f, 1f));
        }
        else
        {
            arrowScaleTimer += Gdx.graphics.getDeltaTime();
            if (arrowScaleTimer > 1f)
            {
                arrowScaleTimer = 1f;
            }

            arrowScale = Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, arrowScaleTimer);
            sb.setColor(new Color(1f, 0.2F, 0.3F, 1f));
        }

        Vector2 tmp = new Vector2(controlPoint.x - x, controlPoint.y - y);
        tmp.nor();
        drawCurve(sb, origin, new Vector2(x, y), controlPoint);
        sb.draw(ImageMaster.TARGET_UI_ARROW, x - 128f, y - 128f, 128f, 128f, 256f, 256f, arrowScale, arrowScale,
                tmp.angle() + 90f, 0, 0, 256, 256, false, false);
    }

    protected void drawCurve(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control)
    {
        float radius = 7f * Settings.scale;

        for (int i = 0; i < points.length - 1; ++i)
        {
            points[i] = Bezier.quadratic(points[i], (float) i / 20f, start, control, end, new Vector2());
            radius += 0.4F * Settings.scale;
            float angle;
            Vector2 tmp;
            if (i != 0)
            {
                tmp = new Vector2(points[i - 1].x - points[i].x, points[i - 1].y - points[i].y);
                angle = tmp.nor().angle() + 90f;
            }
            else
            {
                tmp = new Vector2(control.x - points[i].x, control.y - points[i].y);
                angle = tmp.nor().angle() + 270f;
            }

            sb.draw(ImageMaster.TARGET_UI_CIRCLE, points[i].x - 64f, points[i].y - 64f, 64f, 64f, 128f, 128f, radius / 18f, radius / 18f, angle, 0, 0, 128, 128, false, false);
        }
    }
}
