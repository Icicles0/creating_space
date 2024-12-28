package com.rae.creatingspace.init.graphics;

import com.jozufozu.flywheel.core.PartialModel;
import com.rae.creatingspace.CreatingSpace;

public class PartialModelInit {
    public static final PartialModel MEMORY_ROLL = block("flight_recorder/memory_roll"),
            CATALYST_CARRIER_HEAD = block("catalyst_carrier/head"),
            ELECTROLYZER_HEAD = block("mechanical_electrolyzer/head"),
            SMALL_BELL_NOZZLE = block("rocket_engine/small_bell_nozzle"),
            SMALL_POWER_HEAD = block("rocket_engine/small_power_head"),
            SMALL_AEROSPIKE = block("rocket_engine/small_aerospike");

    private static PartialModel block(String path) {
        return new PartialModel(CreatingSpace.resource("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}