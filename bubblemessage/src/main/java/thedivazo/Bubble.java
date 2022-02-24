package thedivazo;

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
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class Bubble {
    private Random random = new Random();

    private boolean isSmall = true;
    private boolean noBasePlate = true;
    private boolean isMarker = true;
    private boolean invisible = true;

    private String message;

    private ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    private static int customNameIndex = 2;
    private static int customNameVisibleIndex = 3;
    private static int paramArmorStandIndex = 14;

    private static String version;

    static {
        version = Bukkit.getVersion();
        if (version.contains("1.17")) {
            paramArmorStandIndex = 15;
        }
        if (version.contains("1.14")) {
            paramArmorStandIndex = 13;
        }
        if (version.contains("1.13")) {
            paramArmorStandIndex = 13;
        }
    }

    private boolean removedBubble = false;
    private Serializer serChatComponent = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
    private Serializer serBoolean = WrappedDataWatcher.Registry.get(Boolean.class);
    private Serializer serByte = WrappedDataWatcher.Registry.get(Byte.class);

    private WrappedDataWatcher metadata = new WrappedDataWatcher();

    private Location loc;
    private int id = random.nextInt(Integer.MAX_VALUE);
    private UUID uuid = UUID.randomUUID();

    public Bubble(String message, Location loc) {
        this.message = message;
        setPosition(loc);
        setMetadata();
    }

    public void spawn(int distance) {
        for (Player p : Objects.requireNonNull(loc.getWorld()).getPlayers()) {
            if (p.getLocation().distance(loc) <= distance) {
                spawn(p);
            }
        }
    }

    public void spawn(Player player) {
        PacketContainer metaPacket = getMetaPacket();
        PacketContainer fakeStandPacket = getFakeStandPacket();

        try {
            pm.sendServerPacket(player, fakeStandPacket);
            pm.sendServerPacket(player, metaPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void setMetadata() {
        Optional<?> opt;
        if (!version.contains("1.15") && !version.contains("1.16") && !version.contains("1.17") && !version.contains("1.18")) {
            opt = Optional.of(WrappedChatComponent.fromText(message));
        } else {
            opt = Optional.of(WrappedChatComponent.fromChatMessage(message)[0].getHandle());
        }
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(customNameIndex, serChatComponent), opt);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(customNameVisibleIndex, serBoolean), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(paramArmorStandIndex, serByte), (byte)
                ((isMarker ? 0x10 : 0) | (isSmall ? 0x01 : 0) | (noBasePlate ? 0x08 : 0))
        );
        metadata.setObject(0, serByte, (byte) (invisible ? 0x20 : 0));


    }

    private PacketContainer getFakeStandPacket() {
        return new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY) {{
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
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        metaPacket.getIntegers().write(0, id);
        metaPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        return metaPacket;
    }

    private void setPosition() {
        PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT) {{
            getModifier().writeDefaults();
            getIntegers().write(0, id);
            getDoubles().write(0, loc.getX());
            getDoubles().write(1, loc.getY());
            getDoubles().write(2, loc.getZ());
            getBooleans().write(0, false);
        }};
        pm.broadcastServerPacket(teleportPacket);

    }

    public void setPosition(Location loc) {
        this.loc = loc;
        setPosition();
    }

    public void setPosition(double x, double y, double z) {
        this.loc = new Location(loc.getWorld(), x, y, z);
        setPosition();
    }

    public void remove() {
        if (removedBubble) return;
        PacketContainer removeStandPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY) {{
            getModifier().writeSafely(0,
                    (version.contains("1.17") ?
                    new IntArrayList(new int[]{id}) :
                    (new int[]{id})));
        }};
        pm.broadcastServerPacket(removeStandPacket);
    }
}
