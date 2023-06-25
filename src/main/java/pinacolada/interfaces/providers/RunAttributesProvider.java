package pinacolada.interfaces.providers;

public interface RunAttributesProvider {
    default void onRefresh() {
    }

    default void onSelectSeries() {

    }

    int ascensionLevel();

    void disableConfirm(boolean value);
}
