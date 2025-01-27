package com.ryandw11.structure;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles configuration of NPCs.
 *
 * @author Marcel Schoen
 */
public class NpcHandler {

    private Map<String, NpcInfo> npcInfoMap = new HashMap<>();

    /**
     * Processes the NPC configuration
     *
     * @param dataFolder The base plugin data folder.
     * @param isDebug True if debug output is enabled.
     */
    public NpcHandler(File dataFolder, boolean isDebug) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        File npcFile = new File(dataFolder, "npcs.yml");
        if(npcFile.exists()) {
            try {
                yamlConfiguration.load(new File(dataFolder, "npcs.yml"));

                List<Map<?, ?>> npcs = yamlConfiguration.getMapList("CitizenNPCs");
                if(isDebug) Bukkit.getLogger().info("Number of NPCs configured: " + npcs.size());

                for(Map<?, ?> npc : npcs) {
                    String alias = "?";
                    try {
                        NpcInfo npcInfo = new NpcInfo();
                        alias = getStringValueWithDefault(npc, "alias", null);
                        if(alias != null) {
                            npcInfo.name = getStringValueWithDefault(npc, "name", "");
                            npcInfo.skinUrl = getStringValueWithDefault(npc, "skinUrl", null);
                            npcInfo.movesAround = getBooleanValueWithDefault(npc, "movesAround");
                            npcInfo.looksAtPlayer = getBooleanValueWithDefault(npc, "looksAtPlayer");
                            npcInfo.isProtected = getBooleanValueWithDefault(npc, "isProtected");
                            npcInfo.commandsSequential = getBooleanValueWithDefault(npc, "commandsSequential");
                            npcInfo.entityType = getStringValueWithDefault(npc, "entityType", "VILLAGER");
                            List<String> commandsOnCreate = (List<String>)npc.get("commandsOnCreate");
                            if(commandsOnCreate != null && !commandsOnCreate.isEmpty()) {
                                npcInfo.commandsOnCreate = commandsOnCreate;
                            }
                            List<String> commandsOnClick = (List<String>)npc.get("commandsOnClick");
                            if(commandsOnClick != null && !commandsOnClick.isEmpty()) {
                                npcInfo.commandsOnClick = commandsOnClick;
                            }
                            npcInfoMap.put(alias, npcInfo);
                            if(isDebug) Bukkit.getLogger().info("NPC '" + alias + "': " + npcInfo);
                        } else {
                            Bukkit.getLogger().info("NPC configuration error, no 'alias' configured!");
                        }
                    } catch(Exception e) {
                        Bukkit.getLogger().warning("Failed to process NPC '" + alias + "':" + e.toString());
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                Bukkit.getLogger().severe("NPC configuration error: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    private String getStringValueWithDefault(Map<?, ?> npc, String attributeName, String defaultValue) {
        if(npc.containsKey(attributeName)) {
            return (String) npc.get(attributeName);
        }
        return defaultValue;
    }

    private Boolean getBooleanValueWithDefault(Map<?, ?> npc, String attributeName) {
        if(npc.containsKey(attributeName)) {
            return (Boolean) npc.get(attributeName);
        }
        return false;
    }

    /**
     * Cleans up the NPC data.
     */
    public void cleanUp() {
        npcInfoMap.clear();
    }

    public NpcInfo getNpcInfoByAlias(String alias) {
        return npcInfoMap.get(alias);
    }

    /**
     * NPC config information holder
     */
    public class NpcInfo {
        public String name = "";
        public String skinUrl = "";
        public boolean movesAround = false;
        public boolean looksAtPlayer = false;
        public boolean isProtected = false;
        public String entityType = "VILLAGER";
        public List<String> commandsOnCreate = new ArrayList<>();
        public List<String> commandsOnClick = new ArrayList<>();
        public boolean commandsSequential = false;

        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
