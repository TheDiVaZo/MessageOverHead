package io.messageoverhead.thedivazo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Bubble {
    private boolean isSmall = true;
    private boolean noBasePlate = true;
    private boolean isMarker = true;
    private boolean invisible = true;

    private String msg;
    private boolean visibleCustomName = true;
    private ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    private static int CustomNameIndex = 2;
    private static int CustomNameVisibleIndex = 3;
    private static int ParamArmorStandIndex = 14;
    static {
        if(Bukkit.getVersion().indexOf("1.17") != -1) {
            ParamArmorStandIndex = 15;
        }
        if(Bukkit.getVersion().indexOf("1.14") != -1) {
            ParamArmorStandIndex = 13;
        }
        if(Bukkit.getVersion().indexOf("1.13") != -1) {
            ParamArmorStandIndex = 13;
        }
    }
    private boolean removed = false;
    private Serializer SerChatComponent = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
    private Serializer SerBoolean = WrappedDataWatcher.Registry.get(Boolean.class);
    private Serializer SerByte = WrappedDataWatcher.Registry.get(Byte.class);

    private WrappedDataWatcher metadata = new WrappedDataWatcher();

    private Location loc;
    private int id = (int) Math.floor(Math.random() * Integer.MAX_VALUE);
    private UUID uuid = UUID.randomUUID();

    public Bubble(String message, Location loc) {
        msg = message;
        setPosition(loc);
        setMetadata();
    }

    public void spawn(int distance) {
        for(Player p: loc.getWorld().getPlayers()) {
            if(p.getLocation().distance(loc) <= distance) {
                spawn(p);
            }
        }
    }

    public void spawn(Player player) {
        PacketContainer MetaPacket = getMetaPacket();
        PacketContainer StandPacket = getFakeStandPacket();

        try {
            pm.sendServerPacket(player, StandPacket);
            pm.sendServerPacket(player, MetaPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void setMetadata() {
        Optional<?> opt;
        if (Bukkit.getVersion().indexOf("1.15") == -1 && Bukkit.getVersion().indexOf("1.16") == -1 && Bukkit.getVersion().indexOf("1.17") == -1 && Bukkit.getVersion().indexOf("1.18") == -1) {
            opt = Optional.of(WrappedChatComponent.fromText(msg));
        }
        else {
            opt = Optional.of(WrappedChatComponent.fromChatMessage(msg)[0].getHandle());
        }
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CustomNameIndex,  SerChatComponent), opt);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CustomNameVisibleIndex, SerBoolean), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(ParamArmorStandIndex, SerByte),(byte)
                ((isMarker ? 0x10:0)|(isSmall ? 0x01:0)|(noBasePlate ? 0x08:0))
        );
        //metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(ParamArmorStandIndex, SerByte),(byte) (0x10|0x01|0x08));
        metadata.setObject(0, SerByte, (byte)(invisible?0x20:0));


    }

    private PacketContainer getFakeStandPacket() {
        return new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY){{
            getIntegers().write(0, id);

            getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            // Set optional velocity (/8000)
            getIntegers().write(1, 0);
            getIntegers().write(2, 0);
            getIntegers().write(3, 0);
            // Set yaw pitch
            getIntegers().write(4, 0);
            getIntegers().write(5, 0);
            // Set location
            getDoubles().write(0, loc.getX());
            getDoubles().write(1, loc.getY());
            getDoubles().write(2, loc.getZ());
            getUUIDs().write(0, uuid);
        }};
    }

    private PacketContainer getMetaPacket() {
        PacketContainer PackageMeta = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        PackageMeta.getIntegers().write(0, id);
        PackageMeta.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        return PackageMeta;
    }

    private void setPosition() {
        PacketContainer TeleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT){{
            getModifier().writeDefaults();
            getIntegers().write(0, id);
            getDoubles().write(0, loc.getX());
            getDoubles().write(1, loc.getY());
            getDoubles().write(2, loc.getZ());
            getBooleans().write(0, false);
        }};
        pm.broadcastServerPacket(TeleportPacket);

    }

    public void setPosition(Location loc) {
        this.loc = loc;
        setPosition();
    }

    public void setPosition(double x, double y, double z) {
        this.loc = new Location(loc.getWorld(),x,y,z);
        setPosition();
    }

    public void remove() {
        if(removed) return;
        PacketContainer RemovePacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY){{
            getModifier().writeSafely(0, (Bukkit.getVersion().indexOf("1.17")!=-1? new IntArrayList(new int[]{id}):(new int[]{id})));
        }};
        pm.broadcastServerPacket(RemovePacket);
    }
}
