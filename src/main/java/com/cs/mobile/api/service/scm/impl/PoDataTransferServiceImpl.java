package com.cs.mobile.api.service.scm.impl;

import com.cs.mobile.api.dao.scm.PoDataTransferDao;
import com.cs.mobile.api.model.scm.PoTransferDetail;
import com.cs.mobile.api.model.scm.PoTransferHead;
import com.cs.mobile.api.service.scm.PoDataTransferService;
import com.cs.mobile.common.utils.DateUtils;
import com.cs.mobile.common.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PoDataTransferServiceImpl implements PoDataTransferService {
    @Value("${path.transferFilePath}")
    private String transferFilePath;
    @Autowired
    private PoDataTransferDao poDataTransferDao;

    /**
     * 同步订单数据
     *
     * @param
     * @return void
     * @author wells.wong
     * @date 2019/9/5
     */
    public void transferData() {
        List<PoTransferHead> poTransferHeadList = poDataTransferDao.getPoTransferData();
        if (poTransferHeadList != null && poTransferHeadList.size() > 0) {
            Map<String, List<PoTransferHead>> map =
                    poTransferHeadList.stream().collect(Collectors.groupingBy(PoTransferHead::getWhCode));
            String headFileName = "";
            String detailFileName = "";
            List<PoTransferHead> headList = null;
            PoTransferHead poTransferHead = null;
            StringBuilder headContent = null;
            StringBuilder detailContent = null;
            String now = null;
            List<String> snList = new ArrayList<String>();
            for (Map.Entry<String, List<PoTransferHead>> entry : map.entrySet()) {
                now = DateUtils.dateTimeNow();
                headFileName = "BCS_PO_H_W" + entry.getKey() + "_" + now + ".dat";
                detailFileName = "BCS_PO_L_W" + entry.getKey() + "_" + now + ".dat";
                headList = entry.getValue();
                headContent = new StringBuilder();
                detailContent = new StringBuilder();
                int detailCount = 0;
                for (int i = 0; i < headList.size(); i++) {
                    poTransferHead = headList.get(i);
                    snList.add(poTransferHead.getPoAsnSn());
                    poTransferHead.setPublishTime(now);
                    headContent.append(poTransferHead.getTransferHeadData());
                    List<PoTransferDetail> poTransferDetailList = poTransferHead.getPoTransferDetailList();
                    for (int j = 0; j < poTransferDetailList.size(); j++) {
                        detailCount++;
                        PoTransferDetail poTransferDetail = poTransferDetailList.get(j);
                        poTransferDetail.setPoAsnSn(poTransferHead.getPoAsnSn());
                        poTransferDetail.setSeqNo(String.valueOf(i + 1));
                        poTransferDetail.setPublish_time(now);
                        detailContent.append(poTransferDetail.getTransferDetailData());
                    }
                }
                headContent.append("END|" + headList.size());
                headContent.append("\r\n");
                detailContent.append("END|" + detailCount);
                detailContent.append("\r\n");
                try {
                    poDataTransferDao.updateTransferStatus(snList, headFileName);
                    FileUtils.WriteContentToFile(transferFilePath + File.separator + headFileName,
                            headContent.toString());
                    FileUtils.WriteContentToFile(transferFilePath + File.separator + detailFileName,
                            detailContent.toString());
                } catch (Exception e) {
                    log.error("基地回货订单文件生成失败", e);
                }
            }
        }
    }
}
