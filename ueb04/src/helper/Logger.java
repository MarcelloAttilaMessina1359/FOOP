package helper;

import java.util.List;

/**
 * Ein Logbuch.
 * 
 * @author kar, mhe
 * 
 */
public interface Logger {

    /**
     * Liefert eine Kopie des aktuellen Logs im aktuellen Zustand zurück.
     * 
     * @return Die Kopie des aktuellen Logs
     */
    List<String> getLog();

    /**
     * Fügt den übergebenen Eintrag am Ende des Logs hinzu.
     * 
     * @param e Der hinzuzufügene Eintrag
     */
    void addEntry(String e);

}
