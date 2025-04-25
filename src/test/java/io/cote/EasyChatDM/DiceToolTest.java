package io.cote.EasyChatDM;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DiceToolTest {

    @Autowired
    private DiceTool diceTool;

    @Test
    void testRoll() {
        String d6 = diceTool.roll("d6", "testing rolling");
        assertThat(Integer.parseInt(d6)).isBetween(1, 6).withFailMessage("d6 roll should be between 1 and 6.");

        String _3d6 = diceTool.roll("3d6", "testing rolling");
        assertThat(Integer.parseInt(_3d6)).isBetween(3, 18).withFailMessage("3d6 roll should be between 3 and 18.");

        // more complex roll, also tests that spaces are stripped
        String complex = diceTool.roll("4d12 + 12", "testing rolling");
        assertThat(Integer.parseInt(String.valueOf(complex))).isBetween(16, 60).withFailMessage(
          "4d12 + 12 roll should be between 54 and 66.");

    }

}
