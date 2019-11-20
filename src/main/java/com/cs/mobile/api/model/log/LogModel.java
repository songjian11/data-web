package com.cs.mobile.api.model.log;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class LogModel implements Serializable{
    private static final long serialVersionUID = 1L;
    //请求时间
    private String requestTime;
    //请求地址
    private String requestUrl;
    //消耗时间
    private String consumTime;
    //时间(不带单位)
    private String second;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LogModel logModel = (LogModel) o;
        return Objects.equals(requestUrl, logModel.requestUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requestUrl);
    }
}
