# EasyChatDM - Demonstrates creating a simple MCP Server tools

Example MCP Server tools for playing D&amp;D with generative AIs. These tools
are oracles that are used to determine
random yes/no questions but also descriptions, events, NPCs, etc. They are "
random tables" to use when you want to
introduce some uncertainty, twists and turns, and randomness to your gaming.
This project is for educational
purposes so little attention is given to good code design.

These tools are MCP Server tools that will work with Claude desktop and other
MCP Clients. They're written in
Spring AI MCP. This project is for education, so no consideration is given to
good design. Instesd, the code is
written to make it easy to follow step-by-step how to create tools.

# Running it

You should copy over the contents of contents of 
<code>src/main/easychatdmdir/prompts/</code> to <code>~/.easychatdm</code>. 
These are files for prompts, oracles, etc. uses.

# Acknowledgements

- Some oracle values and schemes used from [JeansenVaars's Play By Writing]
  (https://github.com/saif-ellafi/play-by-the-writing), 
  and PUM, GUM, etc. systems.

# More 

See [the ChatDM project](https://github.com/cote/chatdm/tree/main) for a more
complex, feature-ful somewhat better designed version of this EasyChatDM.

Here
are [some videos](https://www.youtube.com/playlist?list=PLk_5VqpWEtiWA4NtTC_QwTofEpd34fRFx)
going over the project:

1. [Part one of a video series](https://www.youtube.com/watch?v=iROihhd_OiI) 
   where I build a very simple MCP too-land.
2. In [part two of the video series](https://www.youtube.com/watch?v=VD1GFZgtzuI) where I build a file-based oracle
and go over how to use the logs Claude makes and start doing your own logging.
3. In [part three, I build an MCP Resource](https://www.youtube.com/watch?v=b_vKjph8W2o) to serve as a DM Journal to
   persist game information between play sessions.
4. Finally, [I get around to making an MCP Promopt](https://www.youtube.com/watch?v=xEtYBznneFg), which is really exciting. 
   This is the first glimpse at something that feels "agentic."

Keep an eye on
[the playlist](https://www.youtube.com/playlist?list=PLk_5VqpWEtiWA4NtTC_QwTofEpd34fRFx)
for the rest of the videos
as they come out.

# References

- [Enabling developer mode in Claude desktop](https://modelcontextprotocol.io/quickstart/user)
- [The ChatDM project](https://github.com/cote/chatdm/tree/main) - more
  sophisticated ChatDM tool that's better designed, has more tools, etc.
- [Dan Vega's MCP examples](https://github.com/danvega/dv-courses-mcp).
- [Plot Unfolding Machine](https://jeansenvaars.itch.io/plot-unfolding-machine) -
  some prompts are based on
  JeansenVaar's PUM system, which is CC BY-NC-SA 4.0.
- [My experience playing D&amp;D with ChatGPT and generative AI](https://www.youtube.com/playlist?list=PLk_5VqpWEtiWbS-AHbk6WxgMfnpYaIx3g) -
  video notebook of how I play D&amp;D with the AIs.