package pinacolada.cards.base.attributes;

import pinacolada.cards.base.PCLCard;

public class BlockAttribute extends PCLAttribute
{
    public static final BlockAttribute instance = new BlockAttribute();

    public BlockAttribute()
    {
        icon = ICONS.block.texture();
        largeIcon = ICONS.blockL.texture();
        iconTag = null;
        suffix = null;
    }

    @Override
    public PCLAttribute setCard(PCLCard card)
    {
        suffix = null;
        pclText = card.getBlockString();
        // Damage will always be set before block
        if (card.pclTarget != null && card.getPrimaryInfo() == null)
        {
            iconTag = card.pclTarget.getTag();
        }
        else
        {
            iconTag = null;
        }

        return this;
    }
}
