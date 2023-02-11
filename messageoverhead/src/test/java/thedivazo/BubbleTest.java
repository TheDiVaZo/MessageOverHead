package thedivazo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BubbleTest {

    @Test
    void test() {
        try {
            Class.forName("not.found.class.eee", false, getClass().getClassLoader());
            System.out.println("Не кринге");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Кринге");
        }
    }
}