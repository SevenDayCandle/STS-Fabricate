package pinacolada.interfaces.providers;

public interface RunAttributesProvider {
    int ascensionLevel();

    void disableConfirm(boolean value);

    default void onRefresh() {
    }

    default void onSelectSeries() {

    }
}
