package pinacolada.cards.base;

import extendedui.configuration.EUIHotkeys;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.skills.PSkill;
import pinacolada.utilities.RotatingList;

public class PCLCardPreviews
{
    private static final RotatingList<EUICardPreview> Previews = new RotatingList<>();
    private static EUICardPreview lastPreview = null;
    private static PCLCard lastCard = null;

    public static EUICardPreview getCardPreview(PCLCard card)
    {
        setPreviews(card);

        EUICardPreview preview;
        if (Previews.count() > 1)
        {
            if (EUIHotkeys.cycle.isJustPressed())
            {
                preview = Previews.next(true);
            }
            else
            {
                preview = Previews.current();
            }
            preview.isMultiPreview = true;
        }
        else
        {
            preview = Previews.current();
        }

        if (lastPreview != preview && preview != null)
        {
            lastPreview = preview;
            PCLCard defaultPreview = PCLCard.cast(preview.defaultPreview);
            PCLCard upgradedPreview = PCLCard.cast(preview.upgradedPreview);

            if (defaultPreview != null && defaultPreview.affinities != null)
            {
                defaultPreview.affinities.updateSortedList();
            }
            if (upgradedPreview != null && upgradedPreview.affinities != null)
            {
                upgradedPreview.affinities.updateSortedList();
            }
        }

        return preview;
    }

    public static void invalidate()
    {
        Previews.clear();
        lastCard = null;
        lastPreview = null;
    }

    public static void setPreviews(PCLCard card)
    {
        if (card != null && lastCard != card)
        {
            lastCard = card;
            Previews.clear();
            for (PSkill effect : card.getEffects())
            {
                if (effect == null)
                {
                    continue;
                }
                effect.makePreviews(Previews);
            }
            for (PSkill effect : card.getPowerEffects())
            {
                if (effect == null)
                {
                    continue;
                }
                effect.makePreviews(Previews);
            }
        }
    }
}
