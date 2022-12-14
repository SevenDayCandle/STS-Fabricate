package pinacolada.cards.base;

public class ChoiceCard<T> extends PCLDynamicCard
{
    public final T value;

    public ChoiceCard(ChoiceBuilder<T> builder)
    {
        super(builder);
        this.value = builder.object;
    }
}