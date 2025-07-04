package io.cote.EasyChatDM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class JournalTool {

    private final Logger logger = LoggerFactory.getLogger(JournalTool.class);
    private final DMJournalRepository dmRepository;

    public JournalTool(DMJournalRepository dmRepository) {
        this.dmRepository = dmRepository;
    }

    @Tool(name = "EasyChatDM_addDMJournalEntry",
            description = """
                          This is the DM Journal tool: the Dungeon Master uses it to record and consult private notes about the adventure.
                          Most of these entries are not for player's eyes but for the DM to remember.
                          Use to maintain, remember, and continue game state.
                          You only need to record the sections relevant to the update; omit any fields that have no changes. 
                          For example, if the only new information is meeting a new NPC, an entry with just Scene & Date and NPC Highlights 
                          is sufficient.
                          
                          ## 1. Scene & Date
                          
                          _Tag each entry with where and when._
                          
                          - Scene 8 (Eighth night, Moor of Thar)
                          - Scene 2 (Arrival at Melvaunt, afternoon)
                          - Scene 5 (Gatehouse breach, early dawn)
                          
                          ## 2. Two-Sentence Summary
                          
                          _What just happened, straight-to-the-point._
                          
                          - Owlbear ambushed the party at first light; PCs drove it off after a bloody melee.
                          - Lord Nanther offered 1,000 pp for Oreal's return and warned of noble rivals.
                          - Five Leiyraghon bravos tried to rob the PCs in the alley; Glynt and Gwendar intervened.
                          
                          ## 3. NPC Highlights
                          
                          _Which characters shifted, how they stand now._
                          
                          - Haravak (ally) provided a charcoal map and tips on the west-wall breach.
                          - Woarsten Nanther (desperate) will fund river guides if needed.
                          - Bremen Leiyraghon (hostile) secretly ordered the ambush; the PCs are on his blacklist.
                          
                          ## 4. Key Decisions & Threads
                          
                          _Promises, bargains, loose ends._
                          
                          - PCs vowed to recover Haravak's wolf-fang.
                          - They declined Zhentarim coin but left the offer open.
                          - Oroth volunteered to delay pursuing orcs.
                          
                          ## 5. Loot & Resources
                          
                          _Important gear, clues, cash._
                          
                          - Recovered charcoal map pointing to a hidden sluice under the west wall.
                          - Found Oreal's signet ring (50 gp value).
                          - Looted an Owlbear claw (2 gp) and a potion of "longstrider".
                          
                          ## 6. PC Status (brief)
                          
                          _Health, spells, conditions worth remembering._
                          
                          - Aria: HP 32/38; both "Invisibility" slots spent.
                          - Thorne: HP 14/44; one Hit Die left; Exhaustion 1.
                          - Glynt: HP 18/30; battle-scar wound (-1 Str checks).
                          
                          ## 7. Plans & Branching Paths
                          
                          _What’s coming next, with if/then forks._
                          
                           - **If** PCs scout the collapsed wall -> spot two orc archers and can ambush them.
                           - **If** they head straight to the ritual chamber -> face heavy orog guards (EL 6).
                          - **Otherwise** a detour to the eastern cave may reveal a troll lair—potential ally or threat.
                          
                          ---
                          
                          ## When to write
                          
                          - After each scene concludes
                          - Whenever an NPC's attitude or role changes
                          - Upon major loot or clue discoveries
                          - At session end: add a one-line "Day X wrap-up" and carry forward only today's essentials
                          
                          ---
                          
                          # Examples
                          
                          ### Example 1 - long update
                          
                          **Scene 2 (Arrival in Melvaunt, late afternoon)**
                          Two-Sentence Summary
                          Lord Nanther pressed the PCs for Oreal's safe return and dropped a gold-tipped ivory cane as proof of his seriousness; 
                          he sketched his son's features in charcoal before collapsing in frustration. The seal of House Nanther opened every gate, 
                          but tensions simmer beneath the city's grime.
                          
                          NPC Highlights
                          - Woarsten Nanther (quest-giver): courteous but desperate, will underwrite river guides if needed.
                          - Ongom the bouncer: grudgingly impressed by the seal, mentioned "rusty-bucket rumors."
                          - Pluarty Crow (tavern host): neutral, offered free room for a favor later.
                          
                          Key Decisions & Threads
                          - PCs accepted 1,000 pp upfront, declined to haggle.
                          - Promise to keep Nanther's name out of clan-politics gossip.
                          - Rumors of a secret meeting beneath the Crow's Nest bonfire circle.
                          
                          Loot & Resources
                          - Seal of House Nanther (no mechanical effect, but grants respect).
                          - Sketch of Oreal's face on parchment.
                          - 50 gp advanced for expenses.
                          
                          PC Status
                          - Aria: HP 38/38, all spell slots intact.
                          - Thorne: HP 44/44, no conditions.
                          
                          Plans & Branching Paths
                          - **If** they question tavern patrons → likely hear of Old Tom's treasure-map scams.
                          - **If** they sneak onto the Crow's Nest back patio → could spot Argens Bruil meeting a cloaked contact.
                          - **Otherwise** head to Rusty Bucket to track the half-elf with the raven.
                          
                          ### Example 2 - exaple of a long update
                          
                          **Scene 6 (Bleak Road, mid-morning of Day 3)**
                          Two-Sentence Summary
                          A hungry dire wolf burst from the fog and charged the campfire; the PCs drove it off with crossbow bolts and hand axes, 
                          leaving it limping into the brush. A torn piece of a scion's cloak snagged on its collar hinted at a close call.
                          
                          NPC Highlights
                          - Grûnhawr's distant howl suggested Haravak was watching.
                          - Passing orc scout tracks indicate a party of six moved northeast recently.
                          
                          Key Decisions & Threads
                          - PCs spared the wolf, hoping to track it back to its den.
                          - They chose to follow the blood trail instead of staying on the road.
                          - Thread: wolf den could hide a cache—or an orc ambush.
                          
                          Loot & Resources
                          - Dire wolf pelt (worth 10 gp).
                          - Half-torn cloak fragment (noble cloth pattern).
                          
                          PC Status
                          - Aria: HP 29/38 after spending a Hit Die.
                          - Thorne: HP 37/44 after tripping the wolf.
                          
                          Plans & Branching Paths
                           - **If** they track the wolf den -> might find a troll or forest druid ally.
                           - **If** they retrace orc prints -> leads to an orc scout camp.
                          - **Otherwise** press on to the next road junction and risk losing the trail.
                          
                          ### Example 3
                          
                          **Scene 11 (Xul-Jarak Cell Block, early evening)**
                          Two-Sentence Summary
                          Using the shrine key, the PCs slipped open the bars and freed Oreal and Kalman, both dazed but alive; 
                          Kara Calaudra's raven guided them toward the forge exit. Chains and blood-stained straw marked where the last scion fell.
                          
                          NPC Highlights
                          - Oreal Nanther: shaken, swore off peace talks until his people are safe.
                          - Kalman Leiyraghon: mistrustful, demanded proof they aren't Zhentarim spies.
                          - Esselios (raven): perched on Thorne's shoulder, cawed at every shadow.
                          
                          Key Decisions & Threads
                          - PCs left Oroth behind (he volunteered to delay pursuers).
                          - Promise to fetch Kalman's signet to clear his name.
                          - Thread: residual orc magic hints at a hidden ward.
                          
                          Loot & Resources
                          - Shrine key (spent) and a spare lockpick set.
                          - Two masterwork shields abandoned by orc guards.
                          
                          PC Status
                          - Aria: Exhaustion 1 after using "Invisibility".
                          - Thorne: HP 18/44 after a crossbow bolt.
                          
                          Plans & Branching Paths
                           - **If** they head north through the forge tunnel -> expect 4 orog guards.
                           - **If** they circle back to the courtyard -> can steal the wyvern's saddlebags.
                          - **Otherwise** use "Silence" to slip past the throne hall and avoid Thrull.
                          
                          ### Example 4 - example of a simple update
                          2025-04-21 Scene 10
                          NPC Highlights:
                          - Garlan the Smith (friendly) offered to repair weapons for a modest fee.
                          
                          ### Example 5 - example of a simple update
                          2025-04-22 Scene 11
                          Loot & Resources:
                          - Discovered a hidden chest containing 3 silver coins and an ornate iron key.
                          """
                          )
    public void addDMJournalEntry(@ToolParam(description = "The DM journal entry.") String entry,
                                  @ToolParam(description = MCPUtils.INTENT_DESCRIPTION) String intent)
            throws IOException {
        dmRepository.addEntry(entry);
        logger.debug("addDMJournalEntry called with context {}", intent);
    }

    /**
     * Read the entire DM Journal.
     *
     * This should really only be an MCP Resource, but Claude desktop does not
     * look up resources on its own (even after you authorize and hook it up), so
     * having this as a tool allows Claude desktop to load this on its own.
     *
     * @param context
     * @return
     */
    @Tool(name = "EasyChatDM_readDMJournal",
            description = """
                          This is a DM Journal Resource. You can use it to read and interpret the state of the game, events, etc.
                          You have a separate tool used to add to the journal entry, probably called something like
                          EasyChatDM_addDMJournalEntry

                          ### What each entry contains

                          - **Scene & Date**: anchors the moment in the narrative.
                          - **Summary**: the key events that occurred.
                          - **NPC Highlights**: updates to relationships, new allies or enemies.
                          - **Key Decisions & Threads**: promises, bargains, unresolved plot lines.
                          - **Loot & Resources**: items, clues, and funds acquired.
                          - **PC Status**: health, spells, conditions, exhaustion.
                          - **Plans & Branching Paths**: upcoming options and forks.

                          ### How to use this journal

                          - **Before starting a scene**, review the most recent entries to set context.
                          - **When players ask** "What happened last time?" quote the Summary of the last entry.
                          - **When an NPC reappears**, consult NPC Highlights to keep their attitude consistent.
                          - **If a plot thread is pursued**, refer to Key Decisions & Threads for background.
                          - **When inventory or clues are needed**, draw from Loot & Resources.
                          - **During combat or healing**, honor the PC Status details.
                          - **When offering choices**, present the Plans & Branching Paths forks or create new branches.
                    """)
    public Journal readDMJournal(@ToolParam(description = """
            Any relevant context for why you're asking for the DM journal right now,
            what you're looking for and what you might do with it, etc.
            """) String context) {
        logger.info("readDMJournal called with context {}", context);
        Journal dmJournal = dmRepository.readJournal();

        if (dmJournal != null) {
            logger.debug("readDMJournal returning DM journal file {} with {} entries", dmJournal, dmJournal.getEntries().size());
            return dmJournal;
        } else {
            logger.warn("readDMJournal returning empty DM journal");
            return new Journal(Journal.Entry.AUTHOR_DM);
        }
    }


}
