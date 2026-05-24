package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.CvDao;
import com.bupt.tarecruit.model.CV;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CvDaoImpl implements CvDao {
    private final Path cvsFile;

    public CvDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("cvs.json"));
    }

    public CvDaoImpl(final Path cvsFile) {
        this.cvsFile = cvsFile;
    }

    @Override
    public Optional<CV> findByUserId(final String userId) {
        List<CV> cvs = FileStorageUtil.readList(cvsFile, new TypeToken<>() { });
        return cvs.stream().filter(cv -> cv.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void save(final CV cv) {
        List<CV> cvs = new ArrayList<>(FileStorageUtil.<CV>readList(cvsFile, new TypeToken<>() { }));
        cvs.removeIf(existing -> existing.getUserId().equals(cv.getUserId()));
        cvs.add(cv);
        FileStorageUtil.writeList(cvsFile, cvs);
    }
}
