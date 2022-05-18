package me.ai4kids.npcplugin;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/*8.) Klasse erstellen, um den NPC für neue Spieler "sichtbar" zu machen*/
public class Join implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(NPC.getNPCs()==null)
            return;
        if(NPC.getNPCs().isEmpty())/*Sollte es keinen NPC geben, passiert nichts*/
            return;
        NPC.addJoinPacket(event.getPlayer());/*Sollte es einen NPC geben, wird dem Spieler mitgeteilt, dass dieser existiert*/
    }
}
