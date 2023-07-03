package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PGR;

// Modifier for displaying tags if they are applied to a card. Does NOT actually apply any tags
public class TagDisplayModifier extends AbstractCardModifier {
    public boolean hadCommonIcons;

    public static TagDisplayModifier get(AbstractCard c) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
            if (mod instanceof TagDisplayModifier) {
                return (TagDisplayModifier) mod;
            }
        }
        return null;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (PGR.config.displayCardTagDescription.get()) {
            String text = rawDescription;
            String preString = PCLCardTag.getTagTipPreString(card);
            if (!preString.isEmpty()) {
                text = preString + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + text;
            }
            String postString = PCLCardTag.getTagTipPostString(card);
            if (!postString.isEmpty()) {
                text = text + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + postString;
            }
            return text;
        }
        return rawDescription;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        hadCommonIcons = CommonKeywordIconsField.useIcons.get(card);
        CommonKeywordIconsField.useIcons.set(card, false);
    }

    @Override
    public void onRemove(AbstractCard card) {
        if (hadCommonIcons) {
            CommonKeywordIconsField.useIcons.set(card, true);
        }
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        PCLCardTag.renderTagsOnCard(sb, card, card.transparency);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TagDisplayModifier();
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !(card instanceof PCLCard);
    }
}
