package com.zph.course.biz.service.ad;

import com.baidu.aip.speech.AipSpeech;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class AipSpeechService {
    public static final String APP_ID = "";
    public static final String API_KEY = "";
    public static final String SECRET_KEY = "";

    public GlobalResult getSpeech(byte[] data) {
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        HashMap<String, Object> options = new HashMap<>();
        options.put("dev_pid", 1737);
        JSONObject res = client.asr(data, "pcm", 16000, options);
        log.info("===res result==\n" + res.toString());
        try {
            JSONArray result = res.getJSONArray("result");
            return GlobalResult.builder().data(result.getString(0)).status(res.getInt("err_no")).msg(res.getString("err_msg")).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return GlobalResult.builder().msg(res.getString("err_msg")).status(res.getInt("err_no")).build();
        }
    }
}
