package com.nwafu.PISMDB.service.impl;

import com.nwafu.PISMDB.dao.TargetsDao;
import com.nwafu.PISMDB.entity.*;
import com.nwafu.PISMDB.service.PathwaysService;
import com.nwafu.PISMDB.service.TargetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class TargetsServiceImp implements TargetsService {
    @Autowired
    private TargetsDao targetsDao;
    @Autowired
    private TargetsService targetsService;
    @Autowired
    private PathwaysService pathwaysService;


    @Override
    public int getTargetsCount() {
        return targetsDao.getTargetsCount();
    }

    @Override
    public List<Targets> findTargets(){
        return targetsDao.findTargets();
    }

    @Override
    public List<FormatData<Targets>> findTargetsFormat()
    {
        List<FormatData<Targets>> result = new ArrayList<>();
        List<Targets> targetsList = targetsService.findTargets();
        log.info("{}",targetsList.size());
        for(Targets t : targetsList){
            FormatData formatData = new FormatData();
            formatData.setId(t.getTargetID());
            formatData.setBasic(t);
            List<Pathways> pathwaysList = pathwaysService.getPathwaysByTargetId(t.getTargetID());
            List<String> pathwaysIdList = new ArrayList<>();
            for(Pathways p : pathwaysList){
                pathwaysIdList.add(p.getPathwayID());
            }
            List<String> pathwaysNameList = new ArrayList<>();
            for(Pathways p : pathwaysList){
                pathwaysNameList.add(p.getPathwayName());
            }
            CompoundsPathway compoundsPathway = new CompoundsPathway();
            compoundsPathway.setPathwayID(pathwaysIdList.toString());
            compoundsPathway.setPathwayName(pathwaysNameList.toString());
            formatData.setPathway(compoundsPathway);
            CompoundsRelatedCompounds compoundsRelatedCompounds = new CompoundsRelatedCompounds();
            if(t.getPISMID() != null){
                compoundsRelatedCompounds.setCompoundsList(Arrays.asList(t.getPISMID().split("%%")));
                formatData.setRelated(compoundsRelatedCompounds);
            }else{
                formatData.setRelated(compoundsRelatedCompounds);
            }
            formatData.setSupporting(null);
            result.add(formatData);
        }

        return result;
    }

    @Override
    public Targets findTargetByUniportID(String UniprotID){
        return targetsDao.findTargetByUniportID(UniprotID);
    }
}

