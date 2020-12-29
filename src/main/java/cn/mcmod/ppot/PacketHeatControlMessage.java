package cn.mcmod.ppot;

import cn.mcmod.ppot.pot.cookingpot.ContainerCookingPot;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHeatControlMessage implements IMessage {
	private int currentItemBurnTime;
	public PacketHeatControlMessage() {
		
	}
	
	public PacketHeatControlMessage(int value_now) {
		currentItemBurnTime=value_now;
	}
    @Override
    public void fromBytes(ByteBuf buf) {
    	currentItemBurnTime = buf.getInt(0);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(currentItemBurnTime);
    }
    
    public static class PacketHeatMessageHandler implements IMessageHandler<PacketHeatControlMessage, IMessage> {

    	@Override
    	public IMessage onMessage(PacketHeatControlMessage message, MessageContext ctx) {
    		EntityPlayer player = ctx.getServerHandler().player;
    		if (player.openContainer instanceof ContainerCookingPot) {
    			ContainerCookingPot container = (ContainerCookingPot) player.openContainer;
    			container.getTileCampfire().setField(1, message.currentItemBurnTime);
    		}
    		
    		return null;
    	}
    	
    }
}