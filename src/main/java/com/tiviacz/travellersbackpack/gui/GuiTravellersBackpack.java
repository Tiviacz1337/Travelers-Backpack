package com.tiviacz.travellersbackpack.gui;

import java.io.IOException;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.gui.container.ContainerTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.handlers.ConfigHandler;
import com.tiviacz.travellersbackpack.network.CycleToolPacket;
import com.tiviacz.travellersbackpack.network.EquipBackpackPacket;
import com.tiviacz.travellersbackpack.network.SleepingBagPacket;
import com.tiviacz.travellersbackpack.network.UnequipBackpackPacket;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.EnumSource;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiTravellersBackpack extends GuiContainer
{
	public static final ResourceLocation GUI_ADVENTURE_BACKPACK = new ResourceLocation(TravellersBackpack.MODID, "textures/gui/travellers_backpack.png");
	private static GuiImageButtonNormal bedButton = new GuiImageButtonNormal(5, 96, 18, 18);
	private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(5, 96, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(5, 96, 18, 18);
    private static GuiImageButtonNormal emptyTankButtonLeft = new GuiImageButtonNormal(14, 78, 9, 9);
    private static GuiImageButtonNormal emptyTankButtonRight = new GuiImageButtonNormal(225, 78, 9, 9);
    private static GuiImageButtonNormal disabledCraftingButton = new GuiImageButtonNormal(225, 96, 18, 18);
	private TileEntityTravellersBackpack tile;
	private final InventoryPlayer playerInventory;
	private final IInventoryTravellersBackpack inventory;
	private boolean isWearing;
	private GuiTank tankLeft;
	private GuiTank tankRight;
	
	public GuiTravellersBackpack(World world, InventoryPlayer playerInventory, IInventoryTravellersBackpack inventory, boolean isWearing) 
	{
		super(new ContainerTravellersBackpack(world, playerInventory, inventory, isWearing ? EnumSource.WEARABLE : EnumSource.ITEM));
		this.playerInventory = playerInventory;
		this.inventory = inventory;
		this.isWearing = isWearing;
		this.tankLeft = new GuiTank(inventory.getLeftTank(), 25, 7, 100, 16);
		this.tankRight = new GuiTank(inventory.getRightTank(), 207, 7, 100, 16);
		
		this.xSize = 248;
		this.ySize = 207;
	}
	
	public GuiTravellersBackpack(World world, InventoryPlayer playerInventory, TileEntityTravellersBackpack tile)
	{
		super(new ContainerTravellersBackpack(world, playerInventory, tile, EnumSource.TILE));
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
		int currentRight = (inventory.getRightTank().getFluidAmount() * 100) / (inventory.getRightTank().getCapacity());
		int currentLeft = (inventory.getLeftTank().getFluidAmount() * 100) / (inventory.getLeftTank().getCapacity());
		
		if(this.inventory.getLeftTank().getFluid() != null)
		{
			this.tankLeft.drawGuiFluidBar(this, currentLeft);
		}
		if(this.inventory.getRightTank().getFluid() != null)
		{
			this.tankRight.drawGuiFluidBar(this, currentRight);
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
        	if(this.emptyTankButtonLeft.inButton(this, mouseX, mouseY) || this.emptyTankButtonRight.inButton(this, mouseX, mouseY))
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
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(GUI_ADVENTURE_BACKPACK);
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
                TravellersBackpack.NETWORK.sendToServer(new SleepingBagPacket(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()));
            }
		}
		
		if(!inventory.hasTileEntity() && !CapabilityUtils.isWearingBackpack(playerInventory.player))
		{
			if(equipButton.inButton(this, mouseX, mouseY))
			{
				TravellersBackpack.NETWORK.sendToServer(new EquipBackpackPacket(true));
			}
		}
		
		if(!inventory.hasTileEntity() && CapabilityUtils.isWearingBackpack(playerInventory.player) && isWearing)
		{
			if(unequipButton.inButton(this, mouseX, mouseY))
			{
				TravellersBackpack.NETWORK.sendToServer(new UnequipBackpackPacket(true));
			}
			
			if(inventory.getLeftTank().getFluid() != null)
			{
				if(emptyTankButtonLeft.inButton(this, mouseX, mouseY))
				{
					TravellersBackpack.NETWORK.sendToServer(new CycleToolPacket(1, 3));
				}
			}
			
			if(inventory.getRightTank().getFluid() != null)
			{
				if(emptyTankButtonRight.inButton(this, mouseX, mouseY))
				{
					TravellersBackpack.NETWORK.sendToServer(new CycleToolPacket(2, 3));
				}
			}
		}
		super.mouseClicked(mouseX, mouseY, button);
	}
}
