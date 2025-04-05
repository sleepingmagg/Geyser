/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
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

package org.geysermc.geyser.command.defaults;

import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.api.util.TriState;
import org.geysermc.geyser.command.GeyserCommand;
import org.geysermc.geyser.command.GeyserCommandSource;
import org.geysermc.geyser.command.SuggestionsManager;
import org.geysermc.geyser.text.ChatColor;
import org.geysermc.geyser.text.GeyserLocale;
import org.incendo.cloud.context.CommandContext;

public class ReloadCommand extends GeyserCommand {

    private final GeyserImpl geyser;

    public ReloadCommand(GeyserImpl geyser, String name, String description, String permission) {
        super(name, description, permission, TriState.NOT_SET);
        this.geyser = geyser;
    }

    @Override
    public void execute(CommandContext<GeyserCommandSource> context) {
        GeyserCommandSource sender = context.sender();
        boolean bedrockPlayer = sender.connection() != null;
        String message = context.getOrDefault("message", "");

        if (message.equals("suggestions")) {
            // Reload only suggestions.yml
            SuggestionsManager.init(geyser);
            sender.sendMessage(ChatColor.GREEN + GeyserLocale.getPlayerLocaleString("geyser.commands.reload.suggestions", sender.locale()));
            return;
        }

        if (GeyserImpl.getInstance().isReloading()) {
            sender.sendMessage(ChatColor.RED + GeyserLocale.getPlayerLocaleString("geyser.commands.reload.already_reloading", sender.locale()));
            return;
        }

        if (!bedrockPlayer) {
            sender.sendMessage(GeyserLocale.getPlayerLocaleString("geyser.commands.reload.console", sender.locale()));
        }

        geyser.reloadGeyser();

        sender.sendMessage(ChatColor.GREEN + GeyserLocale.getPlayerLocaleString("geyser.commands.reload.success", sender.locale()));
    }
}
