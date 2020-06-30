package com.tiviacz.travelersbackpack.gui;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.gui.container.ContainerTravelersBackpack;
import com.tiviacz.travelersbackpack.gui.inventory.IInventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.network.CycleToolPacket;
import com.tiviacz.travelersbackpack.network.EquipBackpackPacket;
import com.tiviacz.travelersbackpack.network.SleepingBagPacket;
import com.tiviacz.travelersbackpack.network.UnequipBackpackPacket;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.EnumSource;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.io.IOException;

public class GuiTravelersBackpack extends GuiContainer
{
	public static final ResourceLocation GUI_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
	private static final GuiImageButtonNormal bedButton = new GuiImageButtonNormal(5, 96, 18, 18);
	private static final GuiImageButtonNormal equipButton = new GuiImageButtonNormal(5, 96, 18, 18);
    private static final GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(5, 96, 18, 18);
    private static final GuiImageButtonNormal emptyTankButtonLeft = new GuiImageButtonNormal(14, 86, 9, 9);
    private static final GuiImageButtonNormal emptyTankButtonRight = new GuiImageButtonNormal(225, 86, 9, 9);
    private static final GuiImageButtonNormal disabledCraftingButton = new GuiImageButtonNormal(225, 96, 18, 18);
	private TileEntityTravelersBackpack tile;
	private final InventoryPlayer playerInventory;
	private final IInventoryTravelersBackpack inventory;
	private boolean isWearing;
	private final GuiTank tankLeft;
	private final GuiTank tankRight;
	
	public GuiTravelersBackpack(World world, InventoryPlayer playerInventory, IInventoryTravelersBackpack inventory, boolean isWearing)
	{
		super(new ContainerTravelersBackpack(world, playerInventory, inventory, isWearing ? EnumSource.WEARABLE : EnumSource.ITEM));
		this.playerInventory = playerInventory;
		this.inventory = inventory;
		this.isWearing = isWearing;
		this.tankLeft = new GuiTank(inventory.getLeftTank(), 25, 7, 100, 16);
		this.tankRight = new GuiTank(inventory.getRightTank(), 207, 7, 100, 16);
		
		this.xSize = 248;
		this.ySize = 207;
	}
	
	public GuiTravelersBackpack(World world, InventoryPlayer playerInventory, TileEntityTravelersBackpack tile)
	{
		super(new ContainerTravelersBackpack(world, playerInventory, tile, EnumSource.TILE));
		this.playerInventory = playerInventory;
		this.inventory = tile;
		this.tile = tile;
		this.tankLeft = new GuiTank(inventory.getLeftTank(), 25, 7, 100, 16);
		this.tankRight = new GuiTank(inventory.getRightTank(), 207, 7, 100, 16);
		
		this.xSize = 248;
		this.ySize = 207;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) 
	{
		if(this.inventory.getLeftTank().getFluid() != null)
		{
			this.tankLeft.drawGuiFluidBar();
		}
		if(this.inventory.getRightTank().getFluid() != null)
		{
			this.tankRight.drawGuiFluidBar();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        
        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
        	this.drawHoveringText(this.tankLeft.getTankTooltip(), mouseX, mouseY, this.fontRenderer);
        }
        
        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
        	this.drawHoveringText(this.tankRight.getTankTooltip(), mouseX, mouseY, this.fontRenderer);
        }
        
        if(this.isWearing)
        {
        	if(emptyTankButtonLeft.inButton(this, mouseX, mouseY) || emptyTankButtonRight.inButton(this, mouseX, mouseY))
            {
            	this.drawHoveringText(I18n.format("gui.empty.name"), mouseX, mouseY);
            }
        }
        
        if(ConfigHandler.server.disableCrafting)
        {
        	if(disabledCraftingButton.inButton(this, mouseX, mouseY))
    		{
    			this.drawHoveringText(I18n.format("gui.disabled_crafting.name"), mouseX, mouseY);
    		}
        }
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TRAVELERS_BACKPACK);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		
		if(ConfigHandler.server.disableCrafting)
		{
			disabledCraftingButton.draw(this, 77, 208);
		}
		
		if(inventory.hasTileEntity())
        {
            if(bedButton.inButton(this, mouseX, mouseY))
            {
                bedButton.draw(this, 20, 227);
            } 
            else
            {
                bedButton.draw(this, 1, 227);
            }
        }
		if(!inventory.hasTileEntity())
		{
			if(!CapabilityUtils.isWearingBackpack(playerInventory.player))
			{
				if(equipButton.inButton(this, mouseX, mouseY))
				{
					equipButton.draw(this, 58, 208);
				}
				else
				{
					equipButton.draw(this, 39, 208);
				}
			}
			
			if(CapabilityUtils.isWearingBackpack(playerInventory.player) && isWearing)
			{
				if(unequipButton.inButton(this, mouseX, mouseY))
				{
					unequipButton.draw(this, 58, 227);
				}
				else
				{
					unequipButton.draw(this, 39, 227);
				}
				
				if(ConfigHandler.server.enableEmptyTankButton)
				{
					if(emptyTankButtonLeft.inButton(this, mouseX, mouseY))
					{
						emptyTankButtonLeft.draw(this, 29, 217);
					}
					else
					{
						emptyTankButtonLeft.draw(this, 10, 217);
					}
					
					if(emptyTankButtonRight.inButton(this, mouseX, mouseY))
					{
						emptyTankButtonRight.draw(this, 29, 217);
					}
					else
					{
						emptyTankButtonRight.draw(this, 10, 217);
					}
				}
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException
	{
		if(inventory.hasTileEntity())
		{
			if(bedButton.inButton(this, mouseX, mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SleepingBagPacket(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()));
            }
		}
		
		if(!inventory.hasTileEntity() && !CapabilityUtils.isWearingBackpack(playerInventory.player))
		{
			if(equipButton.inButton(this, mouseX, mouseY))
			{
				TravelersBackpack.NETWORK.sendToServer(new EquipBackpackPacket(true));
			}
		}
		
		if(!inventory.hasTileEntity() && CapabilityUtils.isWearingBackpack(playerInventory.player) && isWearing)
		{
			if(unequipButton.inButton(this, mouseX, mouseY))
			{
				TravelersBackpack.NETWORK.sendToServer(new UnequipBackpackPacket(true));
			}
			
			if(inventory.getLeftTank().getFluid() != null)
			{
				if(emptyTankButtonLeft.inButton(this, mouseX, mouseY))
				{
					TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(1, Reference.EMPTY_TANK));
				}
			}
			
			if(inventory.getRightTank().getFluid() != null)
			{
				if(emptyTankButtonRight.inButton(this, mouseX, mouseY))
				{
					TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(2, Reference.EMPTY_TANK));
				}
			}
		}
		super.mouseClicked(mouseX, mouseY, button);
	}
}
