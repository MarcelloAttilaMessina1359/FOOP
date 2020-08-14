import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {
    /**
     * Liest eine JSON-Datei ein.
     * 
     * @param fileName Name der einzulesenden JSON-Datei im Ordner "test/examples/json/" ohne Endung
     * @return Dateiinhalt von fileName
     * @throws IOException Fehler beim Einlesen
     */
    public static String readExampleJson(String fileName) throws IOException {
        return new String(Files.readAllBytes(
                Paths.get("test/examples/json/" + fileName + ".json")));
    }
}
