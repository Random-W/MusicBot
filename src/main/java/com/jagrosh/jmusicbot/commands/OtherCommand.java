package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.other.PubgCmd;

public abstract class OtherCommand extends Command
{
    protected final Bot bot;

    public OtherCommand(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = true;
        this.name = "pubg";
        this.category = new Category("Pubg");
    }

    @Override
    protected void execute(CommandEvent event)
    {
        doCommand(event);
    }

    public abstract void doCommand(CommandEvent event);
}