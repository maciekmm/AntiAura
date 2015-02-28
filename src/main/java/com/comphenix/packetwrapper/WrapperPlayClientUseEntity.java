package com.comphenix.packetwrapper;

import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

public class WrapperPlayClientUseEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;
    
    public WrapperPlayClientUseEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientUseEntity(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Target.
     * @return The current Target
     */
    public int getTarget() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Target.
     * @param value - new value.
     */
    public void setTarget(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Type.
     * <p>
     * Notes: 0 = INTERACT, 1 = ATTACK, 2 = INTERACT_AT
     * @return The current Type
     */
    public EntityUseAction getType() {
        return handle.getEntityUseActions().read(0);
    }
    
    /**
     * Set Type.
     * @param value - new value.
     */
    public void setType(EntityUseAction value) {
        handle.getEntityUseActions().write(0, value);
    }

    public Vector getTargetVector() {
    	return handle.getVectors().read(0);
    }

    public void setTargetVector(Vector value) {
    	handle.getVectors().write(0, value);
    }
 
}