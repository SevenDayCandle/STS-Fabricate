package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PMove_ObtainRelic extends PMove implements Hidden
{
    public static final PSkillData DATA = register(PMove_ObtainRelic.class, PCLEffectType.Card)
            .selfTarget();

    protected ArrayList<AbstractRelic> relics = new ArrayList<>();

    public PMove_ObtainRelic()
    {
        this((AbstractRelic) null);
    }

    public PMove_ObtainRelic(Collection<AbstractRelic> relics)
    {
        super(DATA);
        this.relics.addAll(relics);
    }

    public PMove_ObtainRelic(AbstractRelic... relics)
    {
        this(Arrays.asList(relics));
    }

    @Override
    public PMove_ObtainRelic onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (card instanceof TooltipProvider)
        {
            List<EUITooltip> tips = ((TooltipProvider) card).getTips();
            if (tips != null)
            {
                for (AbstractRelic r : relics)
                {
                    tips.add(new EUITooltip(r.name, r.description));
                }
            }
        }
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.obtain("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractRelic r : relics)
        {
            GameUtilities.getCurrentRoom(true).addRelicToRewards(r.makeCopy());
        }

        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.obtain(PCLCoreStrings.joinWithAnd(EUIUtils.map(relics, g -> "{" + PGR.getRelicStrings(g.relicId).NAME + "}")));
    }
}
