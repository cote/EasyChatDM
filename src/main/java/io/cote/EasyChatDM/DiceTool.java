package io.cote.EasyChatDM;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class DiceTool {

    private static final Logger logger = LoggerFactory.getLogger(DiceTool.class);

    @Tool(name = "EasyChatDM_rollDice",
          description = """
                  Rolls dice according to the syntax defined in the D&D 5e rules.
                  Returns total of all dice rolled.
                  """)
    public String roll(@ToolParam(description =
                                    "Notation for dice to roll such a d6, 2d4+4, 3d6, d20-3, 1d20+7 etc.")
                                  String diceExpression,
                       @ToolParam(description = MCPUtils.CONTEXT_DESCRIPTION, required = true) String context) {

        DiceParser diceParser = new DefaultDiceParser();
        // DiceParser does not like spaces ¯\_(ツ)_/¯
        String nospaces = diceExpression.replaceAll("\\s", "");
        RollHistory rolls = new DefaultDiceParser().parse(nospaces, new DiceRoller());
        logger.info("Dice {} rolled resulting in {} total, dice rolled {} Context: {}", diceExpression,
                    rolls.getTotalRoll(), rolls.getRollResults(), context);
        return rolls.getTotalRoll().toString();
    }
}

