        ServerPlayNetworking.registerGlobalReceiver(SPECIAL_ACTION_ID, (server, player, handler, buf, response) ->
        {
            double scrollDelta = buf.readDouble();
            byte actionId = buf.readByte();
            byte screenID = buf.readByte();
            BlockPos pos = null;

            if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID) pos = buf.readBlockPos();
            BlockPos finalPos = pos;

            server.execute(() -> {
                if(player != null)
                {
                    if(actionId == Reference.SWAP_TOOL)
                    {
                        ServerActions.swapTool(player, scrollDelta);
                    }

                    else if(actionId == Reference.SWITCH_HOSE_MODE)
                    {
                        ServerActions.switchHoseMode(player, scrollDelta);
                    }

                    else if(actionId == Reference.TOGGLE_HOSE_TANK)
                    {
                        ServerActions.toggleHoseTank(player);
                    }

                    else if(actionId == Reference.EMPTY_TANK)
                    {
                        ServerActions.emptyTank(scrollDelta, player, player.world, screenID, finalPos);
                    }
                }
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(SORTER_ID, (server, player, handler, buf, response) ->
        {
            byte screenID = buf.readByte();
            byte button = buf.readByte();
            boolean shiftPressed = buf.readBoolean();
            BlockPos pos = null;

            if(buf.writerIndex() == 11)
            {
                pos = buf.readBlockPos();
            }

            BlockPos finalBlockPos = pos;

            server.execute(() -> {
                if(player != null)
                {
                    ServerActions.sortBackpack(player, screenID, button, shiftPressed, finalBlockPos);
                }
            });
        });
    }
}