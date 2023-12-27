package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.text.EUITextHelper;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.utilities.RotatingList;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.cards.base.cardText.ConditionToken;
import pinacolada.cards.base.cardText.PointerToken;
import pinacolada.cards.base.cardText.SymbolToken;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.utilities.UniqueList;

import java.util.ArrayList;
import java.util.List;

import static pinacolada.skills.PSkill.CAPITAL_CHAR;
import static pinacolada.skills.PSkill.CHAR_OFFSET;

public interface ValueProvider {

    default AbstractCreature getSourceCreature() {
        return AbstractDungeon.player;
    }
    default int getXValue() {return 1;}
    default int timesUpgraded() {
        return 0;
    }
}
