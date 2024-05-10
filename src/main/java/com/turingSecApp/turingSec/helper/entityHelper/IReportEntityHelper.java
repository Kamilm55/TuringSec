package com.turingSecApp.turingSec.helper.entityHelper;

import com.turingSecApp.turingSec.dao.entities.ReportEntity;

public interface IReportEntityHelper {
    ReportEntity deleteReportChildEntities(ReportEntity report);

    //todo: populateReportEntity
}
