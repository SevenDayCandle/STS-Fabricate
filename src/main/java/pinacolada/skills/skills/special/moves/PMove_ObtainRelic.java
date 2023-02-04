package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_RelicID;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PMove_ObtainRelic extends PMove<PField_RelicID>
{
    public static final PSkillData<PField_RelicID> DATA = register(PMove_ObtainRelic.class, PField_RelicID.class)
            .selfTarget();

    public PMove_ObtainRelic()
    {
        super(DATA);
    }

    public PMove_ObtainRelic(Collection<String> relics)
    {
        super(DATA);
        fields.relicIDs.addAll(relics);
    }

    public PMove_ObtainRelic(String... relics)
    {
        super(DATA);
        fields.relicIDs.addAll(Arrays.asList(relics));
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
                for (String r : fields.relicIDs)
                {
                    AbstractRelic relic = RelicLibrary.getRelic(r);
                    if (relic != null)
                    {
                        tips.add(new EUITooltip(relic.name, relic.description));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_obtain(TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (String r : fields.relicIDs)
        {
            AbstractRelic relic = RelicLibrary.getRelic(r);
            if (relic != null)
            {
                GameUtilities.getCurrentRoom(true).addRelicToRewards(relic.makeCopy());
            }
        }

        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_obtain(fields.random ? fields.getRelicIDOrString() : fields.getRelicIDAndString());
    }
}
