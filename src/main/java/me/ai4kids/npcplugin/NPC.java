package me.ai4kids.npcplugin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    /*1.) Wir erstellen zuerst eine Liste die die NPCs speichert*/
    private static List<EntityPlayer> NPC = new ArrayList<EntityPlayer>();

    /*2.) Wir erstellen eine Funktion die den NPC erstellt*/

    /*Mit dieser Funktion erstellen wir einen NPC*/
    public static void createNPC(Player player) {/*Player player übergibt die koordianten den Spielers wo der NPC dann steht*/
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();/*Hiermit kann man den Server ansprechen*/
        WorldServer world = ((CraftWorld) Bukkit.getWorld(player.getWorld().getName())).getHandle();/*Hiermit kriegt man die Welt in der der Spieler steht*/
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "AI4Kids");/*Wir geben den NPC eine UUID und einen Namen, max 16 Buchstaben*/
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));/*Hier erstellen wir den NPC mithilfe der vom Server kommenden Daten*/
        npc.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());/*Der NPC wird immer auf die Stelle des Spielers gesetzt*/

        addNPCPacket(npc);/*4.) Wir senden das Paket
        Das Problem hierbei ist, das Spieler die neu joinen nicht den NPC kennen*/
        NPC.add(npc);
    }

    /*3.) Hier erstellen wir eine Funktion die allen Spielern mitteilt das der NPC auf dem Server existiert*/
    private static void addNPCPacket(EntityPlayer npc) {/*Wir übergeben der Funktion den NPC*/
        for (Player player : Bukkit.getOnlinePlayers()) { /*Für alle Spieler die MOMENTAN auf dem Server sind, senden wir das Paket, das der NPC existiert*/
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection; /*Hier sammeln wir alle Spieler*/
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));/*Hier nehmen wir den NPC als "Spieler" auf, dieser wird dann auch in der TAB Liste angezeigt*/
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));/*Spawnt den NPC für den Spieler Client*/
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));/*Wir übersetzen hier einen Winkel in eine Zahl zwichen 0-256 da es sonst zu Fehlen kommt*/
        }
    }
    /*5.) Hier erstellen wir eine Funktion die allen Spielern die später joinen sagt, das der NPC auf dem Server existiert*/
    public static void addJoinPacket(Player player){
        for (EntityPlayer npc : NPC){
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
        }
    }
    /*6.) Da wir die Liste mit NPCs privat gestaltet haben, müssen wir mit der Funktion die NPCs bekommen*/
    public static List<EntityPlayer> getNPCs(){
        return NPC;
    }
}
