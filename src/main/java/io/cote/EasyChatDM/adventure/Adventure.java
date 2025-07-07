package io.cote.EasyChatDM.adventure;

import java.util.List;

/**
 * An adventure is composed of several scenes.
 *
 * @param name
 * @param scenes any number of scenes.
 * @param currentScene which scene is currently being played.
 */
public record Adventure(String name, List<String> scenes, int currentScene) {}