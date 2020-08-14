package wson;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enthält Hilfsmethoden für {@link Wson#fromJson} zum Parsen eines JSON-Strings.
 * 
 * Diese Klasse ist vollständig vorgegeben und darf nicht verändert werden.
 * 
 * @author kar, mhe
 *
 */
public class JSONParser {
    /**
     * Überliest alle Whitespaces (Leerzeichen, Tabs, Newlines). Gibt den Reader an der Position des
     * ersten Zeichens, welches kein Whitespace mehr ist, zurück.
     * 
     * @param reader Reader an der aktuellen Position (in/out)
     * @throws IOException Lesefehler des Readers
     */
    private static void skipWhitespace(PushbackReader reader) throws IOException {
        int character;
        do {
            character = reader.read();
        } while (Character.isWhitespace(character));

        // letztes gelesenes Zeichen (kein Whitespace mehr) zurückschreiben
        reader.unread(character);
    }

    /**
     * Löscht Whitespaces vor und hinter einem Separator und prüft um es sich um den übergebenen
     * Separator handelt. Der Separator ist ein einzelnes Zeichen und bei JSON entweder ':' oder
     * ','.
     * 
     * @param reader Reader
     * @param separator gesuchter Separator
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Illegales Trennzeichen gefunden
     */
    private static void checkForSeparator(PushbackReader reader, char separator)
            throws IOException, JSONSyntaxException {
        skipWhitespace(reader);

        int character = reader.read();
        if (character != separator) {
            throw new JSONSyntaxException("Illegal Separator: " + (char) character + " found!");
        }

        skipWhitespace(reader);
    }

    /**
     * Liest ein Schlüsselwort oder eine Zahl als String ein, also alles bis zu einem Whitespace
     * oder Trennzeichen ("}", "]" oder ",").
     * 
     * @param reader Reader
     * @return Das Schlüsselwort
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    private static String readKeywordOrNumber(PushbackReader reader)
            throws IOException, JSONSyntaxException {
        final StringBuilder res = new StringBuilder();
        int character;

        character = reader.read();

        while ((character != -1) && !Character.isWhitespace(character) && character != '}'
                && character != ']' && character != ',') {
            // Zeichen anfügen
            res.append((char) character);
            character = reader.read();
        }
        reader.unread(character); // letztes gelesenes Zeichen zurückschreiben

        return res.toString();
    }

    /**
     * Liest eine Zahl ein.
     * 
     * @param reader Reader
     * @return Die Zahl
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    private static double readNumber(PushbackReader reader)
            throws IOException, JSONSyntaxException {
        try {
            return Double.parseDouble(readKeywordOrNumber(reader));
        } catch (NumberFormatException e) {
            throw new JSONSyntaxException("wrong number format");
        }
    }

    /**
     * Liest einen String ein. Der String beginnt und endet mit doppelten Anführungszeichen (").
     * Maskierte Zeichen (außer \ u) werden korrekt umgewandelt.
     * 
     * @param reader Reader.
     * @return eingelesener String
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    private static String readString(PushbackReader reader)
            throws IOException, JSONSyntaxException {
        final StringBuilder res = new StringBuilder();
        int character;

        // Anfangs-Tüddelchen verwerfen - (die sind aus Konsistenzgründen da drin)
        character = reader.read();
        assert character == '"';

        character = reader.read();
        while (character != -1 && (character != '"')) {
            // Auf Escape-Sequenzen prüfen
            if (character == '\\') {
                final int c2 = reader.read();
                switch (c2) {
                    case '"':
                    case '\\':
                    case '/':
                        // bei gequoteten Werten Quote entfernen
                        character = c2;
                        break;
                    case 'b':
                        character = '\b';
                        break;
                    case 'f':
                        character = '\f';
                        break;
                    case 'n':
                        character = '\n';
                        break;
                    case 'r':
                        character = '\r';
                        break;
                    case 't':
                        character = '\t';
                        break;
                    case 'u':
                        throw new JSONSyntaxException("escape sequence \\u not allowed");
                    default:
                        throw new JSONSyntaxException("illegal escape sequence");
                }
            }

            res.append((char) character);

            character = reader.read();
        }
        if (character == -1) {
            // Dateiende vor Stringende erreicht
            throw new JSONSyntaxException("Reached file end to soon");
        }

        // letztes Zeichen wird nicht wieder zurückgeschrieben, da die " am Ende
        // des Strings verworfen werden sollen
        return res.toString();
    }

    /**
     * Liest einen null-Wert ein und validiert ob die Eingabe korrekt ist.
     * 
     * @param reader Reader
     * @return null
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    private static Object readNull(PushbackReader reader) throws JSONSyntaxException, IOException {
        String s = readKeywordOrNumber(reader);
        switch (s) {
            case "null":
                return null;
            default:
                throw new JSONSyntaxException("unknown value: " + s);
        }
    }

    /**
     * Liest ein Boolean ein und validiert ob die Eingabe korrekt ist.
     * 
     * @param reader Reader
     * @return boolescher Wert der dem gelesenen booleschen Wert entspricht (true oder false)
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    private static boolean readBoolean(PushbackReader reader)
            throws JSONSyntaxException, IOException {
        String s = readKeywordOrNumber(reader);
        switch (s) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new JSONSyntaxException("unknown value: " + s);
        }
    }

    /**
     * Liest ein JSON-Object als Java-Map ein.
     * 
     * @param reader zu nutzender Reader
     * @return die eingelesene Map
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    private static Map<String, Object> readObject(PushbackReader reader)
            throws IOException, JSONSyntaxException {
        int character = reader.read();
        assert (character == '{');

        Map<String, Object> result = new HashMap<>();

        skipWhitespace(reader);
        character = reader.read();

        if (character == ',') {
            throw new JSONSyntaxException(
                    "Syntax error at: " + (char) character + ". Expected: \" or }");
        } else if (character == '"') {
            reader.unread(character);
            character = ','; // erwarteter Wert, wir können die Schleife betreten
        }
        while (character == ',') {
            skipWhitespace(reader);

            int c = reader.read();
            reader.unread(c);
            if (c != '"') {
                throw new JSONSyntaxException(
                        "Syntax error at: " + (char) character + ". Expected: \"");
            }
            String key = readString(reader);
            checkForSeparator(reader, ':');
            Object value = readElement(reader);

            result.put(key, value);

            skipWhitespace(reader);
            character = reader.read();
        }
        if (character == -1) {
            throw new JSONSyntaxException("reached file end to soon.");
        } else if (character != '}') {
            throw new JSONSyntaxException("Syntax error at:" + (char) character + ". Expected: }");
        }
        return result;
    }

    /**
     * Liest ein JSON-Array als Java-Liste ein.
     * 
     * @param reader zu nutzender Reader
     * @return die eingelesene Liste
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    private static List<Object> readArray(PushbackReader reader)
            throws IOException, JSONSyntaxException {
        int character = reader.read();
        assert (character == '[');

        List<Object> list = new ArrayList<>();

        // Analog zu readObject:
        skipWhitespace(reader);
        character = reader.read();
        if (character == ',') {
            throw new JSONSyntaxException("Syntax error at: " + (char) character + ".");
        } else {
            reader.unread(character);
            character = ',';
        }
        while (character == ',') {
            list.add(readElement(reader));

            skipWhitespace(reader);
            character = reader.read();
        }

        if (character == -1) {
            throw new JSONSyntaxException("reached file end to soon.");
        } else if (character != ']') {
            throw new JSONSyntaxException("Syntax error at:" + (char) character + ". Expected: ]");
        }

        return list;
    }

    /**
     * Liest einen unbekannten Wert aus einer JSON-Zeichenfolge ein. (Sollte auch als Einstiegspunkt
     * für die Verarbeitung dienen.)
     * 
     * @param reader Reader an der aktuellen Stelle des Streams
     * @return das eingelesene Element
     * @throws IOException Lesefehler des Readers
     * @throws JSONSyntaxException Syntaxfehler beim Einlesen
     */
    public static Object readElement(PushbackReader reader)
            throws IOException, JSONSyntaxException {

        // Whitespaces am Anfang überlesen
        skipWhitespace(reader);

        int character = reader.read();

        // gelesenes Zeichen zurück in die Datei schreiben
        // um einen definierten Anfangszustand für jede Art von Objekt zu haben
        reader.unread(character);

        switch (character) {
            case '{':
                return readObject(reader);
            case '[':
                return readArray(reader);
            case '"':
                return readString(reader);
            case 't':
            case 'f':
                return readBoolean(reader);
            case 'n':
                return readNull(reader);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
                return readNumber(reader);
            default:
                throw new JSONSyntaxException("no value starts with: " + character);
        }
    }
}
