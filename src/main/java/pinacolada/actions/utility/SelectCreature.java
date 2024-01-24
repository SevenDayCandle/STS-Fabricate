package pinacolada.actions.utility;

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
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class SelectCreature extends PCLAction<AbstractCreature> {
    private final Vector2[] points = new Vector2[20];
    private final Vector2 controlPoint = new Vector2();
    private final Vector2 origin = new Vector2();
    private float arrowScaleTimer;
    private ActionT1<AbstractCreature> onHovering;
    private AbstractCreature previous;
    private PCLCardTarget targeting;
    private boolean actForSummon;
    private boolean autoSelect;
    private boolean cancellable;
    private boolean skipConfirmation;

    public SelectCreature(AbstractCreature target) {
        this(AbstractDungeon.player, target);
    }

    public SelectCreature(AbstractCreature source, AbstractCreature target) {
        super(ActionType.SPECIAL);

        this.card = null;
        this.cancellable = true;

        initialize(source, target, 1);
    }

    public SelectCreature(PCLCardTarget targeting, String sourceName) {
        this(targeting, sourceName, AbstractDungeon.player);
    }

    public SelectCreature(PCLCardTarget targeting, String sourceName, AbstractCreature source) {
        super(ActionType.SPECIAL);

        this.card = null;
        this.targeting = targeting;
        this.cancellable = true;

        initialize(source, null, 1, sourceName);
    }

    public SelectCreature(AbstractCard card) {
        this(card, GameUtilities.getCardOwner(card));
    }

    public SelectCreature(AbstractCard card, AbstractCreature source) {
        super(ActionType.SPECIAL);

        this.card = card;
        this.cancellable = true;

        PCLCard c = EUIUtils.safeCast(card, PCLCard.class);
        if (c != null && c.pclTarget != null) {
            targeting = c.pclTarget;
        }
        else {
            targeting = PCLCardTarget.forVanilla(card.target);
        }

        initialize(source, null, 1, card.name);
    }

    public SelectCreature actForSummon(boolean allowSummonSlot) {
        this.actForSummon = allowSummonSlot;
        targeting = PCLCardTarget.SingleAlly;

        return this;
    }

    public SelectCreature autoSelectSingleTarget(boolean autoSelectSingleTarget) {
        this.autoSelect = autoSelectSingleTarget;

        return this;
    }

    @Override
    protected void completeImpl() {
        GameCursor.hidden = false;
        if (actForSummon) {
            PCLCardAlly.emptyAnimation.unhighlight();
        }
        super.completeImpl();
    }

    protected void drawCurve(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control) {
        float radius = 7f * Settings.scale;

        for (int i = 0; i < points.length - 1; ++i) {
            points[i] = Bezier.quadratic(points[i], (float) i / 20f, start, control, end, new Vector2());
            radius += 0.4F * Settings.scale;
            float angle;
            Vector2 tmp;
            if (i != 0) {
                tmp = new Vector2(points[i - 1].x - points[i].x, points[i - 1].y - points[i].y);
                angle = tmp.nor().angle() + 90f;
            }
            else {
                tmp = new Vector2(control.x - points[i].x, control.y - points[i].y);
                angle = tmp.nor().angle() + 270f;
            }

            sb.draw(ImageMaster.TARGET_UI_CIRCLE, points[i].x - 64f, points[i].y - 64f, 64f, 64f, 128f, 128f, radius / 18f, radius / 18f, angle, 0, 0, 128, 128, false, false);
        }
    }

    @Override
    protected void firstUpdate() {
        if (target != null) {
            complete(target);
            return;
        }

        final ArrayList<AbstractMonster> enemies = GameUtilities.getEnemies(true);
        final ArrayList<PCLCardAlly> summons = GameUtilities.getSummons(actForSummon ? null : true);
        if (enemies.isEmpty() && targeting == PCLCardTarget.Single) {
            complete(null);
            return;
        }
        else if (summons.isEmpty() && targeting == PCLCardTarget.SingleAlly) {
            complete(null);
            return;
        }

        if (autoSelect) {
            if ((targeting == PCLCardTarget.Single || targeting == PCLCardTarget.SelfSingle) && enemies.size() == 1) {
                target = enemies.get(0);
                if (card != null) {
                    card.calculateCardDamage((AbstractMonster) target);
                }
            }
            else if ((targeting == PCLCardTarget.SingleAlly || targeting == PCLCardTarget.SelfSingleAlly) && summons.size() == 1) {
                target = summons.get(0);
                if (card != null) {
                    card.calculateCardDamage((AbstractMonster) target);
                }
            }
            else if (targeting == PCLCardTarget.Self) {
                target = player;
                if (card != null) {
                    card.applyPowers();
                }
            }

            if (target != null) {
                complete(target);
                return;
            }
        }

        if (skipConfirmation) {
            switch (targeting) {
                case Self:
                case SelfAllEnemy:
                case Team:
                    complete(source);
                    return;
                case SelfSingle:
                case RandomEnemy:
                    complete(GameUtilities.getRandomEnemy(true));
                    return;
                case SelfSingleAlly:
                case RandomAlly:
                    complete(GameUtilities.getRandomSummon(actForSummon ? null : true));
                    return;
                case AllEnemy:
                case All:
                case AllAlly:
                case None:
                    complete(null);
                    return;
            }
        }

        for (int i = 0; i < this.points.length; ++i) {
            this.points[i] = new Vector2();
        }

        super.firstUpdate();
    }

    public SelectCreature isCancellable(boolean cancellable) {
        this.cancellable = cancellable;

        return this;
    }

    protected void render(SpriteBatch sb) {
        switch (targeting) {
            case AllAllyEnemy:
                for (AbstractCreature c : GameUtilities.getSummons(true)) {
                    c.renderReticle(sb);
                }
            case RandomEnemy:
            case AllEnemy:
            case SelfAllEnemy:
                for (AbstractCreature c : GameUtilities.getEnemies(true)) {
                    c.renderReticle(sb);
                }
                break;
            case Team:
            case AllAlly:
                for (AbstractCreature c : GameUtilities.getSummons(true)) {
                    c.renderReticle(sb);
                }
                break;
            case All:
                for (AbstractCreature c : GameUtilities.getAllCharacters(true)) {
                    c.renderReticle(sb);
                }
                break;
            case None:
                break;
            default:
                renderArrow(sb);
                if (target != null) {
                    target.renderReticle(sb);
                }
                break;
        }

        final String message = updateMessage();
        if (!message.isEmpty()) {
            FontHelper.renderDeckViewTip(sb, message, Settings.scale * 96f, Settings.CREAM_COLOR);
        }
    }

    protected void renderArrow(SpriteBatch sb) {
        float x = (float) InputHelper.mX;
        float y = (float) InputHelper.mY;

        if (card != null) {
            origin.x = card.current_x;
            origin.y = card.current_y;

            controlPoint.x = card.current_x - ((x - card.current_x) / 4f);
            controlPoint.y = card.current_y + ((y - card.current_y - 40f * Settings.scale) / 2f);
        }
        else {
            origin.x = player.dialogX;
            origin.y = player.dialogY - 40f * Settings.scale;

            controlPoint.x = player.animX - (x - player.animX) / 4f;
            controlPoint.y = player.animY + (y - player.animY - 40f * Settings.scale) / 2f;
        }

        float arrowScale;
        if (target == null) {
            arrowScale = Settings.scale;
            arrowScaleTimer = 0f;
            sb.setColor(new Color(1f, 1f, 1f, 1f));
        }
        else {
            arrowScaleTimer += Gdx.graphics.getDeltaTime();
            if (arrowScaleTimer > 1f) {
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

    public SelectCreature setMessage(String message) {
        this.message = message;

        return this;
    }

    public SelectCreature setMessage(String format, Object... args) {
        this.message = EUIUtils.format(format, args);

        return this;
    }

    public SelectCreature setOnHovering(ActionT1<AbstractCreature> onHovering) {
        this.onHovering = onHovering;

        return this;
    }

    public SelectCreature skipConfirmation(boolean skipConfirmation) {
        this.skipConfirmation = skipConfirmation;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        GameCursor.hidden = true;

        if (InputHelper.justClickedRight && cancellable) {
            if (card != null) {
                card.applyPowers();
            }

            completeImpl();
            return;
        }

        switch (targeting) {
            case Self:
            case SelfPlayer:
                updateTarget(true, false, false, true);
                break;
            case Single:
                updateTarget(false, true, true, false);
                break;
            case SingleAlly:
                updateTarget(false, false, true, false);
                break;
            case SelfSingleAlly:
                updateTarget(true, false, true, true);
                break;
            case SelfSingle:
            case Any:
                updateTarget(true, true, true, true);
                break;
        }

        if (InputHelper.justClickedLeft || InputHelper.justReleasedClickLeft) {
            InputHelper.justClickedLeft = false;
            InputHelper.justReleasedClickLeft = false;
            switch (targeting) {
                case RandomAlly:
                    complete(target = GameUtilities.getRandomSummon(true));
                    return;
                case RandomEnemy:
                    complete(target = GameUtilities.getRandomEnemy(true));
                    return;

                case AllEnemy:
                case AllAlly:
                case All:
                case Team:
                case None:
                    complete(null);
                    return;

                case Self:
                case Single:
                case SelfSingle:
                case SelfSingleAlly:
                case SingleAlly:
                case Any:
                    if (target != null) {
                        complete(target);
                        return;
                    }
            }
        }

        EUI.addPostRender(this::render);
    }

    protected void updateTarget(boolean targetPlayer, boolean targetEnemy, boolean targetAlly, boolean canTargetSelf) {
        if (actForSummon) {
            PCLCardAlly.emptyAnimation.highlight();
        }

        if (target != null) {
            previous = target;
            target = null;
        }

        if (targetPlayer && (player.hb.hovered && !player.isDying) && (canTargetSelf || player != source)) {
            target = player;
        }
        else {
            if (targetEnemy) {
                for (AbstractMonster m : GameUtilities.getEnemies(true)) {
                    if (m.hb.hovered && !m.isDying && (canTargetSelf || m != source)) {
                        target = m;
                        break;
                    }
                }
            }
            if (targetAlly && target == null) {
                for (AbstractMonster m : GameUtilities.getSummons(actForSummon ? null : true)) {
                    m.hb.update();
                    if (m.hb.hovered && (actForSummon || !m.isDying) && (canTargetSelf || m != source)) {
                        target = m;
                        break;
                    }
                }
            }
        }

        if ((card != null) && (target != null) && (target != previous)) {
            if (target instanceof AbstractMonster) {
                card.calculateCardDamage((AbstractMonster) target);
            }
            else {
                card.applyPowers();
            }
        }

        if (onHovering != null) {
            onHovering.invoke(target);
        }
    }
}
