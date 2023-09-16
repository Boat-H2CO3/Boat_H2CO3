package org.koishi.launcher.h2co3.core.utils.json;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;
import org.koishi.launcher.h2co3.core.utils.json.model.JsonArray;
import org.koishi.launcher.h2co3.core.utils.json.model.JsonBoolean;
import org.koishi.launcher.h2co3.core.utils.json.model.JsonNull;
import org.koishi.launcher.h2co3.core.utils.json.model.JsonNumber;
import org.koishi.launcher.h2co3.core.utils.json.model.JsonObject;
import org.koishi.launcher.h2co3.core.utils.json.model.JsonString;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * The class Json parser is used to parse a {@link String} or a {@link File} to {@link JsonValue}
 */
public class JsonParser {

    private char[] jsonBuffer;
    private int jsonBufferPosition = 0;

    /**
     * Parse {@link InputStream} to {@link JsonValue} and cast it to {@link Class<T>}
     *
     * @param <T>         the type parameter determines the type of {@link JsonValue}
     * @param inputStream the input stream is used to parse
     * @param clazz       the clazz is used to cast {@link T}
     * @return the value {@link T}
     */
    public <T extends JsonValue> T parse(InputStream inputStream, Class<T> clazz) {
        return clazz.cast(parse(inputStream));
    }

    /**
     * Parse json value with {@link InputStream}
     *
     * @param inputStream the input stream is used to parse
     * @return the json value is the raw return type
     */
    public JsonValue parse(InputStream inputStream) {
        try {
            prepareParser(inputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        skipWhiteSpace();
        switch (peekChar()) {
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
            case '.':
            case 'E':
            case 'e':
            case '+':
            case '-': {
                return parseJsonNumber();
            }
            case 'f':
            case 't': {
                return parseJsonBoolean();
            }
            case 'n': {
                return parseJsonNull();
            }
            case '"': {
                return parseJsonString();
            }
            case '{': {
                JsonObject jsonObject = parseJsonObject();
                jsonBuffer = null;
                jsonBufferPosition = 0;
                return jsonObject;
            }
            case '[': {
                JsonArray jsonArray = parseJsonArray();
                jsonBuffer = null;
                jsonBufferPosition = 0;
                return jsonArray;
            }
            default: {
                throw new RuntimeException("Could not parse file to Json!");
            }
        }
    }


    /**
     * Parse {@link String} to {@link JsonValue} and cast it to {@link Class<T>}
     *
     * @param <T>   the type parameter determines the type of {@link JsonValue}
     * @param json  the input stream is used to parse
     * @param clazz the clazz is used to cast {@link T}
     * @return the value {@link T}
     */
    public <T extends JsonValue> T parse(String json, Class<T> clazz) {
        return clazz.cast(parse(json));
    }

    /**
     * Parse {@link String} to {@link JsonValue}.
     *
     * @param json the json used to parse
     * @return the json value is the raw return type
     */
    public JsonValue parse(String json) {
        return parse(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }


    /**
     * Parse {@link File} to {@link JsonValue} and cast it to {@link Class<T>}
     *
     * @param <T>   the type parameter determines the type of {@link JsonValue}
     * @param file  the file is used to parse
     * @param clazz the clazz is used to cast {@link T}
     * @return the value {@link T}
     * @throws FileNotFoundException the file not found exception
     */
    public <T extends JsonValue> T parse(File file, Class<T> clazz) throws FileNotFoundException {
        return clazz.cast(parse(file));
    }

    /**
     * Parse {@link File} to {@link JsonValue}.
     *
     * @param file the file used to parse
     * @return the json value raw return type
     * @throws FileNotFoundException the file not found exception
     */
    public JsonValue parse(File file) throws FileNotFoundException {
        return parse(new FileInputStream(file));
    }


    /**
     * Prepare parser to parse given input to {@link JsonValue}.
     *
     * @param inputStream the input stream is used to parse
     * @throws IOException the io exception
     */
    public void prepareParser(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        jsonBuffer = stringBuilder.toString().toCharArray();
        bufferedReader.close();
    }

    /**
     * Parse char from {@link #jsonBuffer} and increase {@link #jsonBufferPosition}
     *
     * @return the char
     */
    public char parseChar() {
        try {
            return jsonBuffer[jsonBufferPosition++];
        } catch (IndexOutOfBoundsException exception) {
            return (char) -1;
        }
    }

    /**
     * Peek char with {@link #parseChar()} and revoke {@link #jsonBufferPosition}
     *
     * @return the char
     */
    public char peekChar() {
        char currentChar = parseChar();
        jsonBufferPosition--;
        return currentChar;
    }

    /**
     * Skip white space.
     */
    public void skipWhiteSpace() {
        while (isWhiteSpace()) {
            parseChar();
        }
    }


    /**
     * Parse json object json object.
     * Iterates through chars and construct json object.
     *
     * @return the json object
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public JsonObject parseJsonObject() {
        JsonObject jsonObject = new JsonObject();

        skipWhiteSpace();
        // Skip '{'
        parseChar();

        char currentChar = peekChar();
        while (currentChar != '}') {
            skipWhiteSpace();

            // Check the next char and handle it!
            switch (peekChar()) {
                case '"': {

                    String key = parseJsonString().getValue();

                    skipWhiteSpace();
                    // Skip ':'
                    currentChar = parseChar();
                    skipWhiteSpace();

                    switch (peekChar()) {
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
                        case '.':
                        case 'E':
                        case 'e':
                        case '+':
                        case '-': {
                            JsonNumber jsonNumber = parseJsonNumber();
                            jsonNumber.pushEventKey(key);
                            jsonObject.add(jsonNumber);
                            break;
                        }
                        case 'f':
                        case 't': {
                            JsonBoolean jsonBoolean = parseJsonBoolean();
                            jsonBoolean.pushEventKey(key);
                            jsonObject.add(jsonBoolean);
                            break;
                        }
                        case 'n': {
                            JsonNull jsonNull = parseJsonNull();
                            jsonNull.pushEventKey(key);
                            jsonObject.add(jsonNull);
                            break;
                        }
                        case '"': {
                            JsonString jsonString = parseJsonString();
                            jsonString.pushEventKey(key);
                            jsonObject.add(jsonString);
                            break;
                        }
                        case '{': {
                            JsonObject object = parseJsonObject();
                            object.pushEventKey(key);
                            jsonObject.add(object);
                            break;
                        }
                        case '[': {
                            JsonArray jsonArray = parseJsonArray();
                            jsonArray.pushEventKey(key);
                            jsonObject.add(jsonArray);
                            break;
                        }
                    }

                    break;
                }
                case '}':
                case ',': {
                    currentChar = parseChar();
                    break;
                }
                default: {
                    throw new RuntimeException(String.format("Could not parse JsonObject stuck at Index: %s as Char: %s", jsonBufferPosition, jsonBuffer[jsonBufferPosition]));
                }

            }
        }
        return jsonObject;
    }

    /**
     * Parse json array json array.
     * Iterates through chars and construct json array.
     *
     * @return the json array
     */
    public JsonArray parseJsonArray() {
        JsonArray jsonArray = new JsonArray();

        skipWhiteSpace();
        // Skip '['
        parseChar();

        char currentChar = peekChar();


        while (currentChar != ']') {
            skipWhiteSpace();

            // Check the next char and handle it!
            switch (peekChar()) {
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
                case '.':
                case 'E':
                case 'e':
                case '+':
                case '-': {
                    jsonArray.add(parseJsonNumber());
                    break;
                }
                case '"': {
                    jsonArray.add(parseJsonString());
                    break;
                }
                case 'f':
                case 't': {
                    jsonArray.add(parseJsonBoolean());
                    break;
                }
                case 'n': {
                    jsonArray.add(parseJsonNull());
                    break;
                }
                case '{': {
                    jsonArray.add(parseJsonObject());
                    break;
                }
                case '[': {
                    jsonArray.add(parseJsonArray());
                    break;
                }
                case ']':
                case ',': {
                    currentChar = parseChar();
                    break;
                }
                default: {
                    throw new RuntimeException(String.format("Could not parse JsonArray stuck at Index: %s as Char: %s", jsonBufferPosition, jsonBuffer[jsonBufferPosition]));
                }
            }
        }

        return jsonArray;
    }

    /**
     * Parse json string.
     * Iterates through chars and construct json string.
     *
     * @return the json string
     */
    public JsonString parseJsonString() {
        JsonString jsonString = new JsonString();

        StringBuilder stringBuilder = new StringBuilder();

        skipWhiteSpace();


        // Take the first '"'
        parseChar();

        char currentChar = parseChar();

        while (currentChar != '"') {
            // Check if in a string is an escape char
            if (currentChar == '\\') {
                stringBuilder.append(currentChar);
                currentChar = parseChar();
            }

            stringBuilder.append(currentChar);
            currentChar = parseChar();
        }

        skipWhiteSpace();

        jsonString.setValue(stringBuilder.toString());

        return jsonString;
    }

    /**
     * Parse json null.
     * Iterates through chars and check if json null is given.
     *
     * @return the json null
     */
    public JsonNull parseJsonNull() {
        char currentChar = parseChar();
        if (currentChar == 'n' && isNextChar('u', 0) && isNextChar('l', 1) && isNextChar('l', 2)) {
            for (int i = 0; i < 3; i++) {
                jsonBufferPosition++;
            }
            return new JsonNull();
        }
        return null;
    }

    /**
     * Parse json boolean.
     * Iterates through chars and check if json boolean is given.
     *
     * @return the json boolean
     */
    public JsonBoolean parseJsonBoolean() {
        char currentChar = parseChar();
        JsonBoolean jsonBoolean = null;
        if (currentChar == 't' && isNextChar('r', 0) && isNextChar('u', 1) && isNextChar('e', 2)) {
            for (int i = 0; i < 3; i++) {
                jsonBufferPosition++;
            }
            jsonBoolean = new JsonBoolean();
            jsonBoolean.setValue(true);
        } else if (currentChar == 'f' && isNextChar('a', 0) && isNextChar('l', 1) && isNextChar('s', 2) && isNextChar('e', 3)) {
            for (int i = 0; i < 4; i++) {
                jsonBufferPosition++;
            }
            jsonBoolean = new JsonBoolean();
            jsonBoolean.setValue(false);
        }
        return jsonBoolean;
    }

    /**
     * Parse json number.
     * Iterates through chars and check if json number is given.
     *
     * @return the json number
     */
    public JsonNumber parseJsonNumber() {
        StringBuilder stringBuilder = new StringBuilder();

        char currentChar = parseChar();

        while (isNumber(currentChar)) {
            stringBuilder.append(currentChar);

            currentChar = parseChar();
        }

        jsonBufferPosition--;

        JsonNumber jsonNumber = new JsonNumber();
        jsonNumber.setValue(stringBuilder.toString());

        return jsonNumber;
    }

    /**
     * Is white space boolean.
     * Check if current char in {@link #jsonBuffer} is a whitespace
     *
     * @return the boolean true or false
     */
    public boolean isWhiteSpace() {
        char currentChar = jsonBuffer[jsonBufferPosition];
        return currentChar == ' ' || currentChar == '\t' || currentChar == '\r' || currentChar == '\n';
    }

    /**
     * Is next char boolean.
     * Check if next char is given char by position
     *
     * @param currentChar the current char is the char that is going to be checked
     * @param position    the position used to get char at position from {@link #jsonBuffer}
     * @return the boolean true or false
     */
    public boolean isNextChar(char currentChar, int position) {
        for (int i = 0; i < position; i++) {
            parseChar();
        }
        char nextChar = peekChar();
        for (int i = 0; i < position; i++) {
            jsonBufferPosition--;
        }
        return nextChar == currentChar;
    }

    /**
     * Is number boolean.
     * Check if current char is a number
     *
     * @param currentChar the current char is used to check
     * @return the boolean true or false
     */
    public boolean isNumber(char currentChar) {
        char[] numberElements = new char[]{
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                '.',
                'E',
                'e',
                '+',
                '-'
        };

        for (char listChar : numberElements) {
            if (listChar == currentChar) {
                return true;
            }
        }
        return false;
    }


}
