package com.zph.course.biz.service.ad;

import com.baidu.aip.util.Base64Util;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class AiService {
    @Resource
    private AipSpeechService aipSpeechService;
    private static final String[] IMAGE_TYPE = new String[]{".pcm", ".wav", ".amr"};

    public GlobalResult getSpeechByBase64(String base64Str) {
        FileUploadResult fileUploadResult = FileUploadResult.builder().build();
        GlobalResult result;
        try {
            result = aipSpeechService.getSpeech(Base64Util.decode(base64Str.split(",")[1]));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            //上传失败
            fileUploadResult.setStatus("error");
            return GlobalResult.builder().status(30902).msg("接口，无法上传音频").data(fileUploadResult).build();
        }

        return result;
    }

    public GlobalResult aDPrediction(String text) {
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/text_cls/adtest";
        //封装Result对象，并且将文件的byte数组放置到result对象中
        //获取access_token
//        String accessToken = BaiduAuthService.getADAuth();
        try {

//            String param = "{\"text\":\""+text+"\"}";
//            String resData = HttpUtil.post(url, accessToken, "application/json", param);
//            JSONObject res =  new JSONObject(resData);
//            JSONArray resultArray = res.getJSONArray("results");
//            log.info(resultArray.toString());
//            double score = resultArray.getJSONObject(0).getDouble("score");
            double score = 0.45;
            ADResult adResult = ADResult.builder().score(score).count(getModal(countWord(text))).build();

            score = scoreCheck(score, getSubstantive(countWord(text)));
            adResult.setScore(score);
            if (score < 0.45) {
                adResult.setText("Health");
            } else if (score < 0.75) {
                adResult.setText("Mild Cognitive Impairment");
            } else {
                adResult.setText("May Be Alzheimer's Syndrome");
            }

            return GlobalResult.builder().status(200).msg("success").data(adResult).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GlobalResult.builder().msg("false").status(5000).build();
    }

    private Map<String, Integer> countWord(String text) {

        List<String> lists = new ArrayList<String>();  //存储过滤后单词的列表
        if (!Strings.isNullOrEmpty(text)) {
            String[] wordsArr1 = text.split("[^a-zA-Z]");  //过滤出只含有字母的
            for (String word : wordsArr1) {
                if (word.length() != 0) {  //去除长度为0的行
                    lists.add(word);
                }
            }
        } else {
            return new HashMap<>();
        }

        Map<String, Integer> wordsCount = new TreeMap<String, Integer>();  //存储单词计数信息，key值为单词，value为单词数

        //单词的词频统计
        for (String li : lists) {
            wordsCount.merge(li, 1, Integer::sum);

        }

        return wordsCount;
    }

    public static void SortMap(Map<String, Integer> oldmap) {

        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldmap.entrySet());

        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (Map.Entry<String, Integer> stringIntegerEntry : list) {
            System.out.println(stringIntegerEntry.getKey() + ": " + stringIntegerEntry.getValue());
        }
    }

    public Integer getSubstantive(Map<String, Integer> wordMap) {
        AtomicInteger sum = new AtomicInteger();
        sum.set(0);
        final HashSet<String> wordSet = new HashSet<String>() {
            {
                add("cookie");
                add("her");
                add("sink");
                add("dishes");
                add("she");
                add("stool");
                add("over");
                add("water");
                add("jar");
                add("boy");
                add("little");
                add("mother");
                add("he");
                add("girl");
                add("drying");
                add("window");
                add("running");
                add("reaching");
                add("his");
                add("hand");
                add("up");
            }
        };
        wordMap.forEach((k, v) -> {
            if (wordSet.contains(k)) {
                sum.set(sum.get() + v);
            }
        });

        return sum.get();
    }

    public Integer getModal(Map<String, Integer> wordMap) {
        AtomicInteger sum = new AtomicInteger();
        sum.set(0);
        final HashSet<String> wordSet = new HashSet<String>() {
            {
                add("huh");
                add("um");
                add("uh");
                add("er");
                add("ah");
                add("like");
                add("okay");
                add("right");
                add("you");
                add("know");
            }
        };
        wordMap.forEach((k, v) -> {
            if (wordSet.contains(k)) {
                sum.set(sum.get() + v);
            }
        });
        return sum.get();
    }

    public double scoreCheck(double score, Integer keyWordCount) {
        if (keyWordCount == 0) {
            if (score + 0.3 < 1) {
                score = score + 0.3;
            }
            if (score > 0.6) {
                score = 0.6;
            }
            return score;
        } else {
            float coefficient = (float) keyWordCount / 30;
            if (coefficient > 1) {
                coefficient = (float) 0.9;
            }
            score = score * (1 - coefficient);
        }
        return score;
    }
}
