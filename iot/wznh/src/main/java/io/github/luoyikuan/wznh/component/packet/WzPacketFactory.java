package io.github.luoyikuan.wznh.component.packet;

import static io.github.luoyikuan.wznh.component.enums.CommonType.ARCHIVES_ACK;
import static io.github.luoyikuan.wznh.component.enums.CommonType.DEVICE;
import static io.github.luoyikuan.wznh.component.enums.CommonType.MD5;
import static io.github.luoyikuan.wznh.component.enums.CommonType.NOTIFY;
import static io.github.luoyikuan.wznh.component.enums.CommonType.REPORT;
import static io.github.luoyikuan.wznh.component.enums.CommonType.REQUEST;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cn.hutool.core.net.NetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import io.github.luoyikuan.wznh.component.enums.CommonType;
import io.github.luoyikuan.wznh.config.SensorConfig;
import io.github.luoyikuan.wznh.config.WzConfig;
import io.github.luoyikuan.wznh.entity.Sensor;
import io.github.luoyikuan.wznh.entity.SensorLog;
import io.github.luoyikuan.wznh.util.XmlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lyk
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WzPacketFactory {

    private final WzConfig wzConfig;
    private final SensorConfig sensorConfig;

    // GB2312 GBK
    public final Charset GB_2312 = Charset.forName("GBK");

    public final AES AES = new AES(Mode.CBC, Padding.ZeroPadding,
        new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
            (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d, (byte) 0x0e, (byte) 0x0f,
            (byte) 0x10},
        new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
            (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d, (byte) 0x0e, (byte) 0x0f,
            (byte) 0x10});

    private Root generateRoot(CommonType type) {
        Root root = new Root();

        Common common = new Common();
        common.setBuildingId(wzConfig.getBuildingId());
        common.setGatewayId(wzConfig.getGatewayId());
        common.setType(type);

        root.setCommon(common);
        return root;
    }

    private WzBasePacket generatePacket(Root root) {
        String xml = XmlUtils.writeValueAsString(root);

        if (log.isDebugEnabled()) {
            log.debug("{}\n{}", root.getCommon().getType().getDesc(), xml);
        }

        return WzBasePacket.generatePacket(AES.encrypt(xml.getBytes(GB_2312)));
    }

    public WzBasePacket requestValidate() {
        Root root = generateRoot(REQUEST);

        IdValidate idValidate = new IdValidate();
        idValidate.setOperation(REQUEST);
        root.setIdValidate(idValidate);

        return generatePacket(root);
    }

    public WzBasePacket requestMd5(String sequence) {
        Root root = generateRoot(MD5);

        IdValidate idValidate = new IdValidate();
        idValidate.setOperation(MD5);
        idValidate.setMd5(DigestUtil.md5Hex(sequence + wzConfig.getSecretKey()));
        root.setIdValidate(idValidate);

        return generatePacket(root);
    }

    public WzBasePacket requestDevice() {
        Root root = generateRoot(DEVICE);

        Device device = new Device();
        device.setSoftware("https://luoyikuan.github.io");
        device.setIp(NetUtil.getLocalhostStr());
        device.setMac(NetUtil.getLocalMacAddress());
        root.setDevice(device);

        return generatePacket(root);
    }

    public WzBasePacket requestArchives() {
        Root root = generateRoot(ARCHIVES_ACK);

        Instruction instruction = new Instruction();
        instruction.setAttr("1");

        BuildInfo buildInfo = new BuildInfo();
        // 网关名称
        buildInfo.setBuildName(wzConfig.getGatewayName());
        instruction.setBuildInfo(buildInfo);

        NetInfo netInfo = new NetInfo();
        netInfo.setPeriod("30");
        instruction.setNetInfo(netInfo);

        List<Protocol> protocolInfo = new ArrayList<>();

        // 电表协议
        {
            Protocol protocol = new Protocol();
            protocol.setId("2");
            protocol.setType("2");
            protocol.setMType("1");
            protocol.setName("electricity");
            protocol.setMutiple("1");
            protocol.setType188("00");

            List<Item> items = new ArrayList<>();

            Item item = new Item();
            item.setId("01");
            item.setMflag("1");
            item.setFnId("01");
            item.setName("ZXYG");
            item.setCmd("11");
            item.setDi("00000000");
            item.setOffset("00");
            item.setLen("04");
            item.setDt("10");
            item.setCalc("d/100");
            items.add(item);

            protocol.setItems(items);
            protocolInfo.add(protocol);
        }

        // 水表协议
        {
            Protocol protocol = new Protocol();
            protocol.setId("9");
            protocol.setType("2");
            protocol.setMType("2");
            protocol.setName("coldWater");
            protocol.setMutiple("1");
            protocol.setType188("00");

            List<Item> items = new ArrayList<>();
            Item item = new Item();
            item.setId("01");
            item.setMflag("1");
            item.setFnId("11");
            item.setCmd("11");
            item.setDi("00000000");
            item.setOffset("00");
            item.setLen("04");
            item.setDt("10");
            item.setCalc("d/100");

            items.add(item);
            protocol.setItems(items);

            protocolInfo.add(protocol);
        }

        instruction.setProtocolInfo(protocolInfo);

        List<Meter> meterInfo = sensorConfig.getSensor().stream().map(sensor -> {
            Meter meter = new Meter();

            meter.setId(sensor.getDjId());
            meter.setMeterId(sensor.getDjId());
            meter.setAddr(sensor.getDjNum14());
            meter.setMType("02");
            meter.setCom("1");
            meter.setComType("02");
            meter.setTUnit("1");
            meter.setCode(sensor.getElectricalType());
            meter.setCt("1");
            meter.setMemo(sensor.getName());
            meter.setPt("1");
            meter.setSampleId("01");

            return meter;
        }).collect(Collectors.toList());

        instruction.setMeterInfo(meterInfo);
        root.setInstruction(instruction);

        return generatePacket(root);
    }

    public WzBasePacket requestNotify() {
        Root root = generateRoot(NOTIFY);

        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setOperation(NOTIFY);

        root.setHeartBeat(heartBeat);

        return generatePacket(root);
    }

    public WzBasePacket requestReport(SensorLog sensorLog) {
        Root root = generateRoot(REPORT);

        Data data = new Data();
        data.setOperation(CommonType.REPORT);
        data.setSequence(Integer.toString((int) (Math.random() * 1000)));
        data.setParse("yes");
        data.setTime(sensorLog.getLogTime());

        Sensor sensor = sensorConfig.getSensor().stream()
            .filter(s -> sensorLog.getSensorId().equals(s.getId()))
            .findAny()
            .get();

        Meter meter = new Meter();
        meter.setId(sensor.getDjId());
        meter.setAddr(sensor.getDjNum12());
        meter.setCom("1");
        meter.setTp("202");
        meter.setName(sensor.getName());

        Function function = new Function();
        function.setId("01");
        function.setCoding(sensor.getElectricalType());
        function.setError("192");
        function.setValue(sensorLog.getValue());
        meter.setFunction(function);

        data.setMeter(meter);
        root.setData(data);

        return generatePacket(root);
    }
}
