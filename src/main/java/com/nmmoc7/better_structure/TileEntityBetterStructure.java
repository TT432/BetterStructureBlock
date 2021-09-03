package com.nmmoc7.better_structure;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TileEntityBetterStructure extends StructureBlockTileEntity {
    @Override
    public boolean detectSize() {
        if (this.getMode() != StructureMode.SAVE) {
            return false;
        } else {
            BlockPos blockpos = this.getPos();
            BlockPos blockpos1 = new BlockPos(blockpos.getX() - 256, 0, blockpos.getZ() - 256);
            BlockPos blockpos2 = new BlockPos(blockpos.getX() + 256, 255, blockpos.getZ() + 256);
            List<StructureBlockTileEntity> list = this.getNearbyCornerBlocks(blockpos1, blockpos2);
            List<StructureBlockTileEntity> list1 = this.filterRelatedCornerBlocks(list);
            if (list1.size() < 1) {
                return false;
            } else {
                MutableBoundingBox structureboundingbox = this.calculateEnclosingBoundingBox(blockpos, list1);
                if (structureboundingbox.maxX - structureboundingbox.minX > 1 &&
                        structureboundingbox.maxY - structureboundingbox.minY > 1 &&
                        structureboundingbox.maxZ - structureboundingbox.minZ > 1) {

                    this.setPosition(new BlockPos(structureboundingbox.minX - blockpos.getX() + 1,
                            structureboundingbox.minY - blockpos.getY() + 1,
                            structureboundingbox.minZ - blockpos.getZ() + 1));

                    this.setSize(new BlockPos(structureboundingbox.maxX - structureboundingbox.minX - 1,
                            structureboundingbox.maxY - structureboundingbox.minY - 1,
                            structureboundingbox.maxZ - structureboundingbox.minZ - 1));

                    this.markDirty();
                    BlockState iblockstate = this.world.getBlockState(blockpos);
                    this.world.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 3);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        int i = nbt.getInt("posX");
        int j = nbt.getInt("posY");
        int k = nbt.getInt("posZ");
        setPosition(new BlockPos(i, j, k));
        int l = Math.max(nbt.getInt("sizeX"), 0);
        int i1 = Math.max(nbt.getInt("sizeY"), 0);
        int j1 = Math.max(nbt.getInt("sizeZ"), 0);
        setSize(new BlockPos(l, i1, j1));

        this.updateBlockState();
    }

    @Override
    public boolean save(boolean writeToDisk) {
        if (this.getMode() == StructureMode.SAVE && !this.world.isRemote && !StringUtils.isNullOrEmpty(this.getName())) {
            BlockPos blockpos = this.getPos().add(this.getPosition());
            ServerWorld ServerWorld = (ServerWorld)this.world;
            TemplateManager templatemanager = ServerWorld.getStructureTemplateManager();
            Template template = templatemanager.getTemplateDefaulted(new ResourceLocation(getName()));
            template.takeBlocksFromWorld(this.world, blockpos, this.getStructureSize(), !this.ignoresEntities(), Blocks.STRUCTURE_VOID);
            template.setAuthor(this.author);
            return !writeToDisk || templatemanager.writeToFile(new ResourceLocation(getName()));
        } else {
            return false;
        }
    }

    @Override
    public TileEntityType<?> getType() {
        return RegistryHandler.TILE;
    }

    private List<StructureBlockTileEntity> getNearbyCornerBlocks(BlockPos p_184418_1_, BlockPos p_184418_2_) {
        List<StructureBlockTileEntity> list = Lists.newArrayList();

        for(BlockPos blockpos : BlockPos.getAllInBoxMutable(p_184418_1_, p_184418_2_)) {
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (blockstate.matchesBlock(Blocks.STRUCTURE_BLOCK) || blockstate.matchesBlock(RegistryHandler.BLOCK)) {
                TileEntity tileentity = this.world.getTileEntity(blockpos);
                if (tileentity instanceof StructureBlockTileEntity) {
                    list.add((StructureBlockTileEntity)tileentity);
                }
            }
        }

        return list;
    }

    private List<StructureBlockTileEntity> filterRelatedCornerBlocks(List<StructureBlockTileEntity> structureBlocks) {
        Predicate<StructureBlockTileEntity> predicate = (structureBlock) -> {
            return structureBlock.getMode() == StructureMode.CORNER && this.getName().equals(structureBlock.getName());
        };
        return structureBlocks.stream().filter(predicate).collect(Collectors.toList());
    }

    private MutableBoundingBox calculateEnclosingBoundingBox(BlockPos p_184416_1_, List<StructureBlockTileEntity> structureBlocks) {
        MutableBoundingBox mutableboundingbox;
        if (structureBlocks.size() > 1) {
            BlockPos blockpos = structureBlocks.get(0).getPos();
            mutableboundingbox = new MutableBoundingBox(blockpos, blockpos);
        } else {
            mutableboundingbox = new MutableBoundingBox(p_184416_1_, p_184416_1_);
        }

        for(StructureBlockTileEntity structureblocktileentity : structureBlocks) {
            BlockPos blockpos1 = structureblocktileentity.getPos();
            if (blockpos1.getX() < mutableboundingbox.minX) {
                mutableboundingbox.minX = blockpos1.getX();
            } else if (blockpos1.getX() > mutableboundingbox.maxX) {
                mutableboundingbox.maxX = blockpos1.getX();
            }

            if (blockpos1.getY() < mutableboundingbox.minY) {
                mutableboundingbox.minY = blockpos1.getY();
            } else if (blockpos1.getY() > mutableboundingbox.maxY) {
                mutableboundingbox.maxY = blockpos1.getY();
            }

            if (blockpos1.getZ() < mutableboundingbox.minZ) {
                mutableboundingbox.minZ = blockpos1.getZ();
            } else if (blockpos1.getZ() > mutableboundingbox.maxZ) {
                mutableboundingbox.maxZ = blockpos1.getZ();
            }
        }

        return mutableboundingbox;
    }

    private void updateBlockState() {
        if (this.world != null) {
            BlockPos blockpos = this.getPos();
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (blockstate.matchesBlock(Blocks.STRUCTURE_BLOCK)) {
                this.world.setBlockState(blockpos, blockstate.with(StructureBlock.MODE, this.getMode()), 2);
            }

        }
    }
}
