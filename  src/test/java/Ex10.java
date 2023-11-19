package test.java;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex10 {

    @ParameterizedTest
    @ValueSource(strings = {"тестБольшеЧемПятнадцатьСиволов", "меньше15"})
    public void e10(String text) {
        assertTrue(text.length() > 15, "Text less than 15 characters");
    }
}