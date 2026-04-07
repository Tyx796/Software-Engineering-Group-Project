package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.CV;
import java.util.Optional;

public interface CvDao {
    Optional<CV> findByUserId(String userId);
    void save(CV cv);
}
