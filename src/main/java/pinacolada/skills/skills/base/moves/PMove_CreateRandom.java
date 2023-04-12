package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_CreateRandom extends PCallbackMove<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_CreateRandom.class, PField_CardCategory.class)
            .setExtra(1, DEFAULT_MAX)
            .selfTarget();

    public PMove_CreateRandom()
    {
        this(1);
    }

    public PMove_CreateRandom(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_CreateRandom(int copies, PCLCardGroupHelper... gt)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardGroup(gt);
    }

    public PMove_CreateRandom(int copies, int extra, PCLCardGroupHelper... gt)
    {
        super(DATA, PCLCardTarget.None, copies, extra);
        fields.setCardGroup(gt);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_addToPile(TEXT.subjects_x, TEXT.subjects_randomX(TEXT.subjects_card), TEXT.cedit_pile);
    }

    @Override
    public void use(PCLUseInfo info, ActionT1<PCLUseInfo> callback)
    {
        final CardGroup choice = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        final int limit = Math.max(extra, amount);
        choice.group = EUIUtils.map(!fields.colors.isEmpty() ? GameUtilities.getRandomAnyColorCards(fields.getFullCardFilter(), limit) : GameUtilities.getRandomCards(fields.getFullCardFilter(), limit),
                AbstractCard::makeCopy);

        boolean automatic = choice.size() <= amount;
        getActions().selectFromPile(getName(), amount, choice)
                .setOptions((automatic ? PCLCardSelection.Random : PCLCardSelection.Manual).toSelection(), automatic)
                .addCallback(cards -> {
                    for (AbstractCard c : cards)
                    {
                        getActions().makeCard(c, fields.groupTypes.size() > 0 ? fields.groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand);
                    }
                    info.setData(choice.group);
                    callback.invoke(info);
                    if (this.childEffect != null)
                    {
                        this.childEffect.use(info);
                    }
                });
    }

    @Override
    public String getSubText()
    {
        String amString = extra > amount ? TEXT.subjects_xOfY(getAmountRawString(), getExtraRawString()) : getAmountRawString();
        String cString = fields.getCardOrString(getRawString(EXTRA_CHAR));
        return fields.groupTypes.size() > 0 ? TEXT.act_addToPile(amString, cString, fields.groupTypes.get(0).name) : TEXT.act_addToPile(amString, cString, PCLCardGroupHelper.Hand.name);
    }
}
