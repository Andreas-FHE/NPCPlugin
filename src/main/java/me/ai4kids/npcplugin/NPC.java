package me.ai4kids.npcplugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    /*1.) Wir erstellen zuerst eine Liste, die die NPCs speichert*/
    private static List<EntityPlayer> NPC = new ArrayList<EntityPlayer>();

    /*2.) Wir erstellen eine Funktion, die den NPC erstellt*/

    /*Mit dieser Funktion erstellen wir einen NPC*/
    public static void createNPC(Player player, String skin) {/*Player player übergibt die Koordinaten des Spielers, wo der NPC dann steht*/
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();/*Hiermit kann man den Server ansprechen*/
        WorldServer world = ((CraftWorld) Bukkit.getWorld(player.getWorld().getName())).getHandle();/*Hiermit findet man die Welt, in der der Spieler steht*/
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.RED + "" + ChatColor.BOLD + skin);/*Wir geben den NPC eine UUID und einen Namen, max 16 Buchstaben*/
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));/*Hier erstellen wir den NPC mithilfe der vom Server kommenden Daten*/
        npc.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());/*Der NPC übernimmt die Position des Spielers (z.B. auch Kopfrotation)*/

        String[] name = getSkin(player, skin);/*Übergabe des Skins an ein Array*/
        gameProfile.getProperties().put("textures", new Property("textures", name[0], name[1]));/*Hier geben wir den Skin an den NPC, mithilfe des Arrays*/

        addNPCPacket(npc);/*4.) Wir senden das Paket
        Das Problem hierbei ist, das Spieler die neu joinen nicht den NPC kennen*/
        NPC.add(npc);/*Wir müssen den NPC der Liste hinzufügen, da dieser sonst nicht "gespeichert" wird"*/
    }

    /*In dieser Funktion wollen wir den Skin von einem Spieler bekommen*/
    private static String[] getSkin(Player player, String name){
        /*Try und catch gehören zusammen
        * Sie werden zum Fehler abfangen benoetigt*/
        try {
            /*Hier kommt die erste Anfrage fuer die id mit Hilfe des Namens*/
            URL req = new URL("https://api.mojang.com/users/profiles/minecraft/" + name); /*Hier senden wir eine Anfrage mit dem eingegebenen Namen*/
            /*Alles was in der Variable req steht einlesen*/
            InputStreamReader reader = new InputStreamReader(req.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();/*Hier lesen wir NUR die id aus*/

            /* BEISPIEL
            * Input: https://api.mojang.com/users/profiles/minecraft/H4cker_HD
            *
            * Output: {"name":"H4cker_HD","id":"dc19d798c55341dcad99b0fa79db4f93"}
            *
            */

            /*Hier kommt die zweite Anfrage mithilfe der ermittelten id*/
            URL req2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false"); /*Das letzte Kuerzel ist wichtig um auch an die signature zu kommen*/
            InputStreamReader reader2 = new InputStreamReader(req2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject(); /*Auslesen der properties*/
            String texture = property.get("value").getAsString();/*Auslesen des values*/
            String signature = property.get("signature").getAsString();/*Auslesen der signature*/
            return new String[] {texture, signature}; /*zurueckgeben des Skins*/

            /*BEISPIEL
            * Input: https://sessionserver.mojang.com/session/minecraft/profile/dc19d798c55341dcad99b0fa79db4f93
            * output: {
            * "id" : "dc19d798c55341dcad99b0fa79db4f93",
            * "name" : "H4cker_HD",
            * "properties" : [ {
            *   "name" : "textures",
            *   "value" : "ewogICJ0aW1lc3RhbXAiIDogMTY1NjE2ODU0NjE4NiwKICAicHJvZmlsZUlkIiA6ICJkYzE5ZDc5OGM1NTM0MWRjYWQ5
                * OWIwZmE3OWRiNGY5MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJINGNrZXJfSEQiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgO
                * iB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgzYTE1M2Y1MDRjYjJiM2JhND
                * VkMTMyOGYxOTIxNmUxMjQwMjkyYjgwOTBmYTcxMzkzNDRjZDdjZGVmMjU3ZSIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ
                * 1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5
                * ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0="
            * } ]
            */

            /*Sollte ein Fehler auftreten(falscher Name), wird die Textur von dem Absender des commands genommen*/
        }catch (Exception e){
            /*Hier passiert das gleiche wie im try Block*/
            EntityPlayer p = ((CraftPlayer) player).getHandle();
            GameProfile profile = p.getProfile();
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new String[]{texture, signature};
        }
    }

    /*3.) Hier erstellen wir eine Funktion, die allen Spielern mitteilt, dass der NPC auf dem Server existiert*/
    private static void addNPCPacket(EntityPlayer npc) {/*Wir übergeben der Funktion den NPC*/
        for (Player player : Bukkit.getOnlinePlayers()) { /*Für alle Spieler die MOMENTAN auf dem Server sind, senden wir das Paket, das der NPC existiert*/
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection; /*Hier sammeln wir alle Spieler*/
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));/*Hier nehmen wir den NPC als "Spieler" auf, dieser wird dann auch in der TAB Liste angezeigt*/
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));/*Spawnt den NPC für den Spieler Client*/
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));/*Wir übersetzen hier einen Winkel in eine Zahl zwischen 0-256, da es sonst zu Fehlern kommt*/
        }
    }
    /*5.) Hier erstellen wir eine Funktion, die allen Spielern, die später joinen, sagt, dass der NPC auf dem Server existiert*/
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
