package com.demo.matching.profile.domain.exception;

import com.demo.matching.common.exception.BusinessException;
import com.demo.matching.common.exception.BusinessResponseStatus;

public class ProfileNotFoundException extends BusinessException {
    public ProfileNotFoundException() {
        super(BusinessResponseStatus.PROFILE_NOT_FOUND);
    }
}
