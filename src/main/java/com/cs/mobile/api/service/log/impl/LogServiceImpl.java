package com.cs.mobile.api.service.log.impl;

import com.cs.mobile.api.model.log.LogModel;
import com.cs.mobile.api.model.log.request.LogRequest;
import com.cs.mobile.api.model.log.response.LogSummaryListResponse;
import com.cs.mobile.api.model.log.response.LogSummaryResponse;
import com.cs.mobile.api.service.log.LogService;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Slf4j
@Service
public class LogServiceImpl implements LogService{
    @Value("${path.log}")
    private String logAddress;

    /**
     * 获取接口的最大消耗时间和最小消耗时间
     * @param logRequest
     * @return
     * @throws Exception
     */
    @Override
    public LogSummaryListResponse queryLogSummaryList(LogRequest logRequest) throws Exception {
        if(null == logRequest){
            ExceptionUtils.wapperBussinessException("参数错误");
        }
        LogSummaryListResponse logSummaryListResponse = new LogSummaryListResponse();
        List<LogSummaryResponse> result = new ArrayList<>();
        //解析指定的日志文件中的接口
        List<LogModel> list = analysisLogFile(logRequest);
        if(null != list && list.size() > 0){
            //对list进行分组处理
            Map<String,List<LogModel>> groupMap= list.stream().collect(Collectors.groupingBy(LogModel::getRequestUrl));

            for(Map.Entry<String,List<LogModel>> entry : groupMap.entrySet()){
                LogSummaryResponse logSummaryResponse = new LogSummaryResponse();
                String key = entry.getKey();
                List<LogModel> value = entry.getValue();
                //对value进行排序
                value = value.stream().sorted(new Comparator<LogModel>() {
                    @Override
                    public int compare(LogModel o1, LogModel o2) {
                        BigDecimal value1 = new BigDecimal(StringUtils.isEmpty(o1.getSecond())?"0":o1.getSecond());
                        BigDecimal value2 = new BigDecimal(StringUtils.isEmpty(o2.getSecond())?"0":o2.getSecond());
                        return value1.compareTo(value2);
                    }
                }).collect(Collectors.toList());
                //获取最大消耗时间的日志
                LogModel max = value.get(value.size() - 1);
                //获取最小消耗时间的日志
                LogModel min = value.get(0);
                logSummaryResponse.setRequestUrl(key);
                logSummaryResponse.setRequestCount(String.valueOf(value.size()));
                logSummaryResponse.setMaxConsumTime(max.getConsumTime());
                logSummaryResponse.setMinConsumTime(min.getConsumTime());
                logSummaryResponse.setRequestMaxTime(max.getRequestTime());
                logSummaryResponse.setRequestMinTime(min.getRequestTime());
                result.add(logSummaryResponse);
            }
        }
        logSummaryListResponse.setList(result);
        return logSummaryListResponse;
    }

    /**
     * 解析指定的日志文件中的接口
     * @param logRequest
     * @return
     * @throws Exception
     */
    private List<LogModel> analysisLogFile(LogRequest logRequest)throws Exception{
        List<LogModel> logs = new ArrayList<>();
        String path = logAddress;
        //默认查询当天的
        String timeStr = DateUtils.parseDateToStr("yyyy-MM-dd",new Date());
        //默认查询所有接口
        String interfaceStr = "/api/headPage";

        if(StringUtils.isNotEmpty(logRequest.getAddress())){
            path = logRequest.getAddress();
        }
        if(StringUtils.isNotEmpty(logRequest.getTime())){
            timeStr = logRequest.getTime();
        }
        if(StringUtils.isNotEmpty(logRequest.getUrl())){
            interfaceStr = logRequest.getUrl();
        }

        String filePath = "";
        if(StringUtils.isNotEmpty(logRequest.getAddress())){
            filePath = logRequest.getAddress() + File.separator + "info." + timeStr + ".log";
        }else{
            filePath = path + File.separator + "info" + File.separator + "info." + timeStr + ".log";
        }

//        log.info("========filePath:" + filePath);
//        log.info("========timeStr:" + timeStr);
//        log.info("========interfaceStr:" + interfaceStr);

        File loggerFile = new File(filePath);
        if(!loggerFile.isFile() || !loggerFile.exists()){
            ExceptionUtils.wapperBussinessException("日志文件不存在");
        }
        //获取文件的编码集
        String encode = codeString(loggerFile);
//        log.info("========encode:" + encode);
        InputStreamReader is = new InputStreamReader(new FileInputStream(loggerFile),"utf-8");
        BufferedReader bf = new BufferedReader(is);
        String str = "";
        while((str = bf.readLine()) != null){
            if(str.contains(interfaceStr)){
//                log.info("========str:" + str);
                LogModel logModel = new LogModel();
                //请求时间
                String timeRegex = "[\\d]{2}:[\\d]{2}:[\\d]{2}.[\\d]{3}";
                Pattern p = Pattern.compile(timeRegex);
                Matcher matcher = p.matcher(str);
                if(matcher.find()){
                    logModel.setRequestTime(matcher.group());
                }
                //请求地址
                String addressStr = str.substring(str.indexOf("请求"),str.lastIndexOf(";"));
                logModel.setRequestUrl(addressStr.substring(addressStr.indexOf("/")));
                //请求消耗时间
                String time = str.substring(str.indexOf("耗时"),str.lastIndexOf("]"));
                logModel.setConsumTime(time.substring(str.indexOf(":")  + 1));
                String regex_1 = "[\\d]{1,}";
                p = Pattern.compile(regex_1);
                matcher = p.matcher(time);
                if(matcher.find()){
                    logModel.setSecond(matcher.group());
                }
                logs.add(logModel);
            }
        }
        return logs;
    }


    /**
     * 获取文件的编码集
     * @param file
     * @return
     * @throws Exception
     */
    private String codeString(File file) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        // 其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            case 0x5c75:
                code = "ANSI|ASCII";
                break;
            default:
                code = "GBK";
        }
        return code;
    }

    /**
     * 本地分析日志文件
     * @param args
     */
    public static void main(String[] args) {
        LogRequest logRequest = new LogRequest();
        //设置指定请求
        logRequest.setUrl("/api/headPage");
        //文件夹路径
        logRequest.setAddress("C:\\Users\\songjisn\\Desktop\\日志分析");
        //设置指定时间的日志
        logRequest.setTime("2019-08-11");
        LogServiceImpl logService = new LogServiceImpl();
        try {
            System.out.println(logService.queryLogSummaryList(logRequest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
