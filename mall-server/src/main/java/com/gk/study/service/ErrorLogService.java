package com.gk.study.service;

/**
 *  * @author Administrator
 *  * @date 2024-03-26
 */
import com.gk.study.entity.ErrorLog;

import java.util.List;

public interface ErrorLogService {
    List<ErrorLog> getErrorLogList();
    void createErrorLog(ErrorLog errorLog);
    void deleteErrorLog(String id);
    void updateErrorLog(ErrorLog errorLog);
}
