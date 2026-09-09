package com.cvmatcher.dao;

import com.cvmatcher.model.Cv;
import java.util.Optional;

public interface CvDao {
    Cv save(Cv cv);
    Optional<Cv> findByUserId(int userId);
}
