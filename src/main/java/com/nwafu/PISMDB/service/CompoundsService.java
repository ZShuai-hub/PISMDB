package com.nwafu.PISMDB.service;

import com.nwafu.PISMDB.dao.CompoundsDao;
import com.nwafu.PISMDB.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
public interface CompoundsService {
    int getCompoundsCount();
    List<Compounds> findAll();
    List<Compounds> findById();
    List<CompoundsBasicInformationBean> FindBasicInformation();
    List<CompoundsBasic> FindBasic();
    List<CompoundsPathway> FindPathway();
    List<CompoundsRelatedCompounds> FindRelatedCompounds();
    List<CompoundSupportingInformation> FindSupportingInformation();
    List<String> findRelatedById(String pismid);
    List<Reference> findReference();
    Compounds findByPISMID(String pismid);
}
