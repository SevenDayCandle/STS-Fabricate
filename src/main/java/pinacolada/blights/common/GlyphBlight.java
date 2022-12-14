package pinacolada.blights.common;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.pcl.glyphs.Glyph;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

public class GlyphBlight extends AbstractGlyphBlight
{
    public static final String ID = createFullID(GlyphBlight.class);
    public static final int MAX_CHOICES = 3;
    public Glyph glyph;

    public GlyphBlight()
    {
        super(ID, PGR.core.config.ascensionGlyph0, PCLAbstractPlayerData.ASCENSION_GLYPH1_UNLOCK, PCLAbstractPlayerData.ASCENSION_GLYPH1_LEVEL_STEP, 0, 1);
    }

    public CardGroup createGlyphGroup()
    {
        final CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        final RandomizedList<AbstractCard> possiblePicks = new RandomizedList<>();
        possiblePicks.addAll(EUIUtils.map(Glyph.getCards(), Glyph::makeCopy));

        for (int i = 0; i < MAX_CHOICES; i++)
        {
            AbstractCard pick = possiblePicks.retrieve(GameUtilities.getRNG());
            for (int j = 0; j < getPotency(); j++)
            {
                pick.upgrade();
            }
            group.group.add(pick);
        }

        return group;
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, GameUtilities.inGame() ? EUIUtils.format(strings.DESCRIPTION[1], getPotency()) : "");
    }

    @Override
    public void onVictory()
    {
        super.onVictory();

        this.glyph = null;
    }

    @Override
    public void atBattleStart()
    {
        super.atBattleStart();

        PCLActions.bottom.selectFromPile(name, 1, createGlyphGroup())
                .addCallback(selection -> {
                    if (selection.size() > 0)
                    {
                        Glyph e = (Glyph) selection.get(0);
                        e.onUse(null);
                        this.glyph = e;
                        flash();
                    }
                });
    }

    @Override
    public void atTurnStart()
    {
        super.atTurnStart();

        if (this.glyph != null)
        {
            for (PSkill be : this.glyph.getEffects())
            {
                be.triggerOnStartOfTurn();
            }
        }
    }

    @Override
    public void onPlayerEndTurn()
    {
        super.onPlayerEndTurn();

        if (this.glyph != null)
        {
            for (PSkill be : this.glyph.getEffects())
            {
                be.triggerOnEndOfTurn(false);
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb)
    {
        super.renderTip(sb);

        if (glyph != null)
        {
            glyph.drawScale = glyph.targetDrawScale = 0.8f;
            glyph.current_x = glyph.target_x = InputHelper.mX + (((InputHelper.mX > (Settings.WIDTH * 0.5f)) ? -1.505f : 1.505f) * EUITooltip.BOX_W);
            glyph.current_y = glyph.target_y = InputHelper.mY - (AbstractCard.IMG_HEIGHT * 0.5f);
            EUI.addPostRender(glyph::render);
        }
    }

}