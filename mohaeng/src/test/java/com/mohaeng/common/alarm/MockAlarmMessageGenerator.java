//package com.mohaeng.common.alarm;
//
//import com.mohaeng.alarm.domain.model.AlarmMessageGenerator;
//import com.mohaeng.alarm.domain.model.value.AlarmMessage;
//import com.mohaeng.alarm.domain.model.value.AlarmType;
//
//public class MockAlarmMessageGenerator implements AlarmMessageGenerator {
//
//    @Override
//    public AlarmMessage generate(final AlarmEvent alarmEvent) {
//        return new AlarmMessage("MOCK ALARM TITLE", "MOCK ALARM MESSAGE");
//    }
//
//    @Override
//    public AlarmType alarmType() {
//        return null;
//    }
//}