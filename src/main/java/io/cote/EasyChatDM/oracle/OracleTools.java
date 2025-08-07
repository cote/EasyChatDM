package io.cote.EasyChatDM.oracle;

import io.cote.EasyChatDM.MCPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Role playing game Oracles to answer yes/no questions. Based on the <a
 * href="https://jeansenvaars.itch.io/plot-unfolding-machine">Plot Unfolding Machine</a>.
 *
 * @author <a href="https://cote.io">Coté</a>
 */
@Service
public class OracleTools {

    private final Logger logger = LoggerFactory.getLogger(OracleTools.class);
    private final Random random = new Random();
    private final OracleRegistry oracleRegistry;

    public OracleTools(OracleRegistry oracleRegistry) {
        this.oracleRegistry = oracleRegistry;
    }

    // Tool name cannot have spaces. Start EasyChatDM to make searching logs easier.
    // Can't use & in MCP, so we type DnD instead of D&D
    @Tool(name = "EasyChatDM_Deterministic_Yes_No_Oracle", description = """
                                                                  When playing Dungeons and Dragons (DnD), use this oracle tool to determine straight forward yes/no questions.
                                                                  Deterministic answers are useful when you need a concrete response to move forward in the game.
                                                                  The answers are on a gradient from strong no, no, weak no, weak yes, yes, and strong yes.
                                                                  Use the degree (strong, weak, or just yes and no) to determine the degree to what happens.
                                                                  The strong it is, to more extreme the answer, etc.
                                                                  Examples of oracle questions:
                                                                  (1) is there a goblins hiding in the bushes?
                                                                  (2) does the merchant want to sell the cloak at a discount?
                                                                  (3) does the planned for action happen in the adventure?
                                                                  """)
    public Map<String, String> deterministicOracle(@ToolParam(description = """
                                                                            The context of this question: why are you doing this check and what might you do with the result.
                                                                            For example, what might you do depending on the result?""", required = true) String questionContext) {
        String[] answers = {"Strong no", "No", "No", "No", "Weak no", "Weak yes", "Yes", "Yes", "Yes", "Strong yes"};
        String answer = pickRandom(answers);

        logger.info("Deterministic OracleTools called: {} -> {}", questionContext, answer);
        return Map.of("deterministic oracle answer", answer);
    }

    @Tool(name = "EasyChatDM_Subjective_Yes_No_Oracle", description = """
               When playing Dungeons and Dragons (DnD), use this oracle tool to determine 
               open ended questions that can lead to unexpected plot points. You should 
               determine an interpretation of the answer rather than having it be a clear, 
               binary yes or no. The answers may be clear ("yes" or "no"), or they may
               lead to more details. For example, "No, not yet..." might mean an action
               will happen in the future, perhaps because the player triggered it, or not.
               "It depends..." might mean the player needs to take some action or perform
               a skills check. For example, your PCs may not yet be ready to know the answer 
               at all, and need to figure out another way to find an answer, or it might 
               require a skill test to figure it out before they can, or perhaps they have to 
               hurry up a to find out. Play with the answers in a fun way.
               """)
    public String subjectiveOracle(@ToolParam(description = MCPUtils.INTENT_DESCRIPTION) String intent) {
        String[] answers = {"No, definitely not", "Apparently not", "No, not yet...",
                            "No, but...", "Don't know, can't tell", "It depends...",
                            "Yes, but...", "Yes, for now", "Yes, apparently",
                            "Yes, absolutely"};
        String answer = pickRandom(answers);
        logger.info("Deterministic OracleTools called: {} -> {}", intent, answer);
        return answer;
    }

    /**
     * An oracle that gives inspiration for how something looks. The list of options of loaded from the resource
     * /oracle/looks.txt which is inspired by the PUM description/looks table, found <a
     * href="https://github.com/saif-ellafi/play-by-the-writing/blob/main/tables/pum_looks.txt">here</a> as plain text.
     *
     * @param questionContext
     * @return answer.
     */
    @Tool(name = "EasyChatDM_description_looks", description = """
               When you want to determine how something looks use this oracle to give inspiration for 
               detail, appearance, impression, or other things related to a description based on looks. 
               """)
    public String descriptionLooksOracle(@ToolParam(description = """
        The context of this question: why are you doing this check and 
        what might you do with the result. For example, what are you describing.
        """, required = true) String questionContext)  {
        Oracle looks = oracleRegistry.get("looks");
        String look = looks.randomResult();
        logger.info("Description Looks OracleTools called: {} -> {}", questionContext, look);
        return look;
    }

    // @formatter:off
    @Tool(name = "EasyChatDM_NPC_Motivations",
          description = """
            Use this to determine an NPC's current motivation, mindset, and overall mood.
            For example,if you've created a new NPC, call this to figure out their current
            state of mind. Or, if it's an NPC you've interacted with before, call this to
            see how they feel about the player, situation, etc. You don't have to be strict
            in how you apply the result, it can be just inspiration, or you
            could just take it word for word.
            """)
    // @formatter:on
    public String npcMotivation(@ToolParam(description = "The context of this question: why are you doing this check and what might you do with the result.", required = true) String questionContext) {
        Oracle motivations = oracleRegistry.get("npc_motivations");
        String motivation = motivations.randomResult();
        logger.info("Description NPC Motivations OracleTools called: {} -> {}", questionContext, motivation);
        return motivation;
    }

    @Tool(name = "EasyChatDM_named_oracle", description = """
                                                          Call a named Oracle which will return the result of the oracle.
                                                          Use the result as your inspiration for what happens next, how to describe
                                                          something,etc. You can get a list of named oracles 
                                                          by calling the tool EasyChatDM_list_named_oracles
                                                          """)
    public String namedOracle(@ToolParam(description = MCPUtils.INTENT_DESCRIPTION) String intent,
                              @ToolParam(description = "Name of oracle to be used. If you do not know the name of any Oracles, call the EasyChatDM_list_named_oracles tool.") String oracleName) {
        Oracle namedOracle = oracleRegistry.get(oracleName);
        if (namedOracle != null) {
            String result = namedOracle.randomResult();
            logger.info("Named OracleTools called: {} -> {}", intent, result);
            return result;
        } else {
            // TK probably should look at a system property for a default, user supplied value.
            return "(No oracle by that name, make up your own answer based on what Susan Sontag would say.)";
        }
    }

    @Tool(name = "EasyChatDM_list_named_oracles", description = """
                                                                When playing a role playing game, like D&D, it is useful to have Oracles to randomly
                                                                determine what happens and come up with ideas. This tool gets a list of the named oracle 
                                                                available, listing the name of the Oracle and how they could be used. Oracles in
                                                                solo D&D serve as randomized decision-making tools that replace a human Dungeon Master, 
                                                                allowing lone players to experience unpredictable gameplay. They generate impartial 
                                                                responses to player questions, create emergent storytelling by introducing unexpected 
                                                                elements, fill in world details like NPC motivations or location features, 
                                                                make objective rulings on action success, and maintain game balance through 
                                                                complications or twists. Ranging from simple yes/no probability tools to 
                                                                complex tables and random event generators, oracles provide the genuine 
                                                                surprise and challenge typically supplied by another person. By consulting 
                                                                these systems at key decision points, solo players can avoid predetermined 
                                                                outcomes and experience a dynamic narrative that unfolds organically rather 
                                                                than following a scripted path they'd consciously or unconsciously create themselves.
                                                                The names of oracles may be vague and if there is not a description available, you 
                                                                should think on how to interpret them. Lastly, if there other other oracles available that
                                                                are NOT named, prefer using those. for example, there is a tool called
                                                                EasyChatDM_NPC_Motivations that you should use instead of a named oracle that
                                                                determines NPC Motivations. Or not, you decide how crazy you want to be. Use both 
                                                                and choose which answer is coolest, or most dreadful, depending on the situation.""")
    public Map<String, String> listNamedOracles(@ToolParam(description = MCPUtils.INTENT_DESCRIPTION) String intent) {

        Map<String, String> oraclesPairs = new HashMap<>();
        Collection<Oracle> oracles = oracleRegistry.getAll();
        for (Oracle oracle : oracles) {
            oraclesPairs.put(oracle.name(), oracle.description());
        }

        logger.info("List OracleTools called with context {} listing oracles {}", intent, oraclesPairs);
        return oraclesPairs;
    }


    private String pickRandom(List<String> lines) {
        if (!lines.isEmpty()) {
            return lines.get(random.nextInt(lines.size()));
        } else {
            return "";
        }
    }

    private String pickRandom(String[] lines) {
        return pickRandom(List.of(lines));
    }

}