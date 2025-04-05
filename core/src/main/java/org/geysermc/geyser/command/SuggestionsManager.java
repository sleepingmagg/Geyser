/*
 * Copyright (c) 2019-2024 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.command;

import lombok.Getter;
import org.geysermc.geyser.GeyserImpl;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SuggestionsManager {

    @Getter
    private static final Map<String, String> commandSuggestions = new HashMap<>();

    /**
     * Загружает подсказки команд из файла suggestions.yml
     *
     * @param geyser Экземпляр Geyser
     */
    public static void init(GeyserImpl geyser) {
        // Очищаем карту на случай повторного вызова (например, при перезагрузке)
        commandSuggestions.clear();

        // Вывести для отладки
        geyser.getLogger().info("Initializing command suggestions from suggestions.yml");

        // Путь к файлу подсказок
        Path suggestionsFile = geyser.getBootstrap().getConfigFolder().resolve("suggestions.yml");

        // Проверяем, существует ли файл, если нет - создаем его
        if (!Files.exists(suggestionsFile)) {
            try (InputStream input = GeyserImpl.class.getClassLoader().getResourceAsStream("suggestions.yml")) {
                if (input != null) {
                    Files.copy(input, suggestionsFile);
                    geyser.getLogger().info("Created default suggestions.yml");
                } else {
                    geyser.getLogger().error("Could not find default suggestions.yml in resources! Suggestions will not work.");
                    return;
                }
            } catch (IOException e) {
                geyser.getLogger().error("Error while creating suggestions.yml", e);
                return;
            }
        }

        // Загружаем файл подсказок
        try (FileInputStream input = new FileInputStream(suggestionsFile.toFile())) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);

            if (data == null) {
                geyser.getLogger().warning("suggestions.yml is empty. No command suggestions will be loaded.");
                return;
            }

            // Получаем секцию suggestions
            Object suggestionsObj = data.get("suggestions");
            if (suggestionsObj instanceof Map<?, ?> suggestions) {
                // Загружаем все подсказки
                for (Map.Entry<?, ?> entry : suggestions.entrySet()) {
                    if (entry.getKey() instanceof String command && entry.getValue() instanceof String description) {
                        commandSuggestions.put(command, description);
                        geyser.getLogger().debug("Loaded suggestion: " + command + " -> " + description);
                    }
                }
                geyser.getLogger().info("Loaded " + commandSuggestions.size() + " command suggestions from suggestions.yml");
            } else {
                geyser.getLogger().warning("No 'suggestions' section found in suggestions.yml");
            }
        } catch (IOException | ClassCastException e) {
            geyser.getLogger().error("Error while loading suggestions.yml", e);
        }
    }

    /**
     * Получает описание команды из загруженных подсказок
     *
     * @param command Название команды
     * @return Описание команды или null, если подсказка не найдена
     */
    public static String getSuggestion(String command) {
        String suggestion = commandSuggestions.get(command);
        if (suggestion != null) {
            GeyserImpl.getInstance().getLogger().debug("Found suggestion for command: " + command + " -> " + suggestion);
        }
        return suggestion;
    }

    /**
     * Получает все подсказки команд
     *
     * @return Неизменяемая карта команд и их описаний
     */
    public static Map<String, String> getAllSuggestions() {
        return Collections.unmodifiableMap(commandSuggestions);
    }
} 
