package pinacolada.cards.base;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUIGameUtils;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class ChoiceCard<T> extends PCLDynamicCard {
    public final T value;

    public ChoiceCard(ChoiceCardData<T> builder) {
        super(builder);
        this.value = builder.object;
    }

    @Override
    public void initializeDescription() {
        if (cardText != null) {
            this.cardText.overrideDescription(StringUtils.isEmpty(cardData.strings.DESCRIPTION) ? null : cardData.strings.DESCRIPTION);
            this.cardText.forceRefresh();
        }
    }

    @Override
    protected void initializeTextures() {
        if (((ChoiceCardData<?>) builder).fromCustom) {
            super.initializeTextures();
        }
    }

    //No-op
    @Override
    protected void renderAffinities(SpriteBatch sb) {
    }

    //No-op
    @Override
    protected void renderAttributes(SpriteBatch sb) {
    }

    //No-op
    @Override
    protected void renderIcons(SpriteBatch sb) {
    }

    //No-op
    @Override
    protected void renderType(SpriteBatch sb) {
    }
}