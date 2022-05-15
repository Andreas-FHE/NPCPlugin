package me.ai4kids.npcplugin;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Join(), this);
        System.out.println("NPC Plugin wurde geladen.");
    }

    @Override
    public void onDisable() {

    }


    /*7.) Hier prüfen wir den Befehl zum Erstellen des NPCs*/
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("createnpc")){/*Wir müssen checken ob der Absender des Befehls eine Console oder ein Spieler ist*/
            if(!(sender instanceof Player)){/*Wenn es kein Spieler ist, passiert nichts*/
                return true;
            }
            Player player = (Player) sender;/*Wenn es ein Spieler ist, soll der NPC erstellt werden*/

            NPC.createNPC(player); /*Aufrufen der create Funktion für den NPC*/
            player.sendMessage(ChatColor.GREEN + "NPC erstellt!"); /*Rückmeldung für den Spieler, dass der NPC erfolgreich erstellt wurde*/
        }
        return false;
    }
}

