package io.github.luoyikuan.wznh.config;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

import io.github.luoyikuan.wznh.component.packet.BuildInfo;
import io.github.luoyikuan.wznh.component.packet.Common;
import io.github.luoyikuan.wznh.component.packet.Data;
import io.github.luoyikuan.wznh.component.packet.Device;
import io.github.luoyikuan.wznh.component.packet.Function;
import io.github.luoyikuan.wznh.component.packet.HeartBeat;
import io.github.luoyikuan.wznh.component.packet.IdValidate;
import io.github.luoyikuan.wznh.component.packet.Instruction;
import io.github.luoyikuan.wznh.component.packet.Item;
import io.github.luoyikuan.wznh.component.packet.Meter;
import io.github.luoyikuan.wznh.component.packet.Meters;
import io.github.luoyikuan.wznh.component.packet.NetInfo;
import io.github.luoyikuan.wznh.component.packet.Protocol;
import io.github.luoyikuan.wznh.component.packet.ReportConfig;
import io.github.luoyikuan.wznh.component.packet.Root;
import io.github.luoyikuan.wznh.entity.SensorLog;

/**
 * 原生编译配置
 */
@Configuration
@RegisterReflectionForBinding({ SensorLog.class,
        BuildInfo.class, Common.class, Data.class, Device.class, Function.class, HeartBeat.class,
        IdValidate.class, Instruction.class, Item.class, Meter.class, Meters.class, NetInfo.class,
        Protocol.class, ReportConfig.class, Root.class
})
public class RuntimeHintsRegistrarConfig {

}
