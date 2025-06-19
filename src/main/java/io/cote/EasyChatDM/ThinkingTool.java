package io.cote.EasyChatDM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * A "thinking tool" based on
 * <a href="https://www.anthropic.com/engineering/claude-think-tool">the Anthropic blog post on this topic</a>.
 *
 * The thinking tool just takes the input and returns it. This gives the MCP Client the chance to use it
 * as a scratchpad - it takes time to think something when it makes the tool call, generates the thought, and then
 * puts it in the context window for later.
 */
@Service
public class ThinkingTool {

    private final Logger logger = LoggerFactory.getLogger(ThinkingTool.class);

    @Tool(name = "EasyChatDM_Think",
                 description = """
                            Use the think tool whenever you need to pause, ponder, and plan without revealing your intentions to the player. 
                            This is your private scratchpad—use it to design the next scene, decide what NPCs want, reflect on past choices, 
                            or consider what should happen next. You can think through encounter ideas, twists, pacing, or narrative threads. 
                            Don't output anything to the player—just write your thoughts clearly and briefly, 
                            like prepping behind the DM screen. This will be included your context window so you can refer back to it as needed.
                            Examples:
                            - The player is wandering town aimlessly. I'll introduce a hook: a courier with a sealed letter from a past ally.
                            - They discovered the hidden door. I need to decide what’s inside—cult lair fits best with the old journal entries.
                            - The barkeep just got threatened. I'll have him play innocent, but report the player to the local guild.
                            - They finished a dungeon arc. Good moment to slow the pace—maybe a strange dream, or travel complications.
                            - Too many action scenes lately. I'll plan a quieter, mysterious encounter next to shift tone.
                            You can use this before or after or between tool calls or after major choices to clarify what happens next,
                            or whenever you think it would be useful to stop and think.
                             """)
    public String think(@ToolParam(description = MCPUtils.CONTEXT_DESCRIPTION+"Do not use this as part of the " +
                                                 "thought, but more to explain why you've decided to call the " +
                                                 "thinking tool. This should not include information that is part of " +
                                                 "the thought",
                              required = true) String context,
                        @ToolParam(description= "the thinking", required = true)
                        String thought) {
        logger.info("ThinkingTool called with context {} and thought {}", context, thought);
        return "I'm thinking about <thought>" + thought + "</thought>";
    }

}
