package com.creativecrateblacklist.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class BlacklistConfig {

    private static final Path CONFIG_PATH =
            Path.of("config", "creative_crate_blacklist.toml");

    private static final Set<ResourceLocation> BLACKLIST =
            new HashSet<>();

    private BlacklistConfig() {
    }

    public static void load() {

        BLACKLIST.clear();

        try {

            Files.createDirectories(
                    CONFIG_PATH.getParent()
            );

            if (!Files.exists(CONFIG_PATH)) {

                Files.writeString(
                        CONFIG_PATH,
                        """
                        # Creative Crate Blacklist
                        #
                        # Items listed below cannot be inserted
                        # into Create's Creative Crate.
                        #
                        # Example:
                        #
                        # [blacklist]
                        # items = [
                        #     "minecraft:nether_star",
                        #     "minecraft:diamond",
                        #     "create:brass_ingot"
                        # ]

                        [blacklist]

                        items = [
                            "minecraft:nether_star"
                        ]
                        """
                );
            }

            parseConfig();

            System.out.println(
                    "[Creative Crate Blacklist] Loaded "
                            + BLACKLIST.size()
                            + " blacklisted item(s)."
            );

        } catch (IOException exception) {

            System.err.println(
                    "[Creative Crate Blacklist] "
                            + "Could not load configuration."
            );
        }
    }

    private static void parseConfig()
            throws IOException {

        boolean inBlacklist = false;
        boolean readingItems = false;

        for (String rawLine :
                Files.readAllLines(CONFIG_PATH)) {

            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            if (line.equals("[blacklist]")) {

                inBlacklist = true;
                continue;
            }
            if (line.startsWith("[")
                    && line.endsWith("]")) {

                inBlacklist = false;
                readingItems = false;
                continue;
            }

            if (!inBlacklist) {
                continue;
            }
            if (line.startsWith("items")) {

                readingItems = true;
            }
            if (!readingItems) {
                continue;
            }
            parseItemIds(line);
            if (line.contains("]")) {

                readingItems = false;
            }
        }
    }

    private static void parseItemIds(String line) {

        String[] parts = line.split("\"");

        for (String part : parts) {

            String id = part.trim();

            if (id.isEmpty()) {
                continue;
            }

            if (!id.contains(":")) {
                continue;
            }

            ResourceLocation location =
                    ResourceLocation.tryParse(id);

            if (location == null) {
                continue;
            }

            BLACKLIST.add(location);
        }
    }

    public static boolean isBlacklisted(
            ItemStack stack
    ) {

        if (stack == null || stack.isEmpty()) {
            return false;
        }

        ResourceLocation id =
                BuiltInRegistries.ITEM.getKey(
                        stack.getItem()
                );

        return BLACKLIST.contains(id);
    }
}